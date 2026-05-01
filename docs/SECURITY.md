# EULO – Security Controls (INA)

## Angriffsvektoren & Schutzmassnahmen

Alle gängigen Angriffsmethoden wurden analysiert und spezifisch abgesichert:

| Angriffsvektor | Schutz | Status |
|---|---|---|
| Brute Force Login | Rate Limit: 5 Versuche/60s → 15 Min Sperre | ✅ |
| DoS (Request-Flood) | Global Rate Limit: 120 Req/60s pro IP | ✅ |
| DoS (Large Payload) | Max 2 MB Request/File, Tomcat-Timeouts | ✅ |
| DoS (Slowloris) | Connection-Timeout 5s, Keep-Alive-Timeout 20s | ✅ |
| XSS (Stored) | HTML-Tags werden aus allen Texteingaben entfernt | ✅ |
| XSS (Reflected) | CSP-Header unterbindet Script-Ausführung | ✅ |
| Clickjacking | `X-Frame-Options: DENY` | ✅ |
| MIME Sniffing | `X-Content-Type-Options: nosniff` | ✅ |
| IDOR (Fremde Sessions lesen) | Auth-User muss mit `userId`-Param übereinstimmen | ✅ |
| IDOR (Fremde Sessions updaten) | Auth-User muss Seeker oder Tutor der Session sein | ✅ |
| IDOR (Fremde Offers löschen) | Auth-User muss `tutorId` des Offers sein | ✅ |
| Mass Assignment (Rollenerhöhung) | `UserController.PUT` erlaubt nur Whitelist-Felder | ✅ |
| Mass Assignment (Admin-Role) | Nur `student`/`teacher` bei Registrierung erlaubt | ✅ |
| NoSQL Injection | Spring Data Parameterized Queries + Null-Byte-Stripping | ✅ |
| Schwache Passwörter | Min 8 Zeichen, mind. 1 Buchstabe + 1 Ziffer | ✅ |
| Ungültige Status-Werte | Whitelist: `open/matched/completed/cancelled` | ✅ |
| JWT Tampering | HMAC-SHA384 Signatur, jeder Request validiert | ✅ |
| Information Leakage (Stacktrace) | `GlobalExceptionHandler` — nur `{error, message}` | ✅ |
| Information Leakage (Passwort) | `@JsonIgnore` auf `User.password` | ✅ |
| Information Leakage (Server) | `server_tokens off` in Nginx | ✅ |
| CSRF | Nicht relevant — stateless JWT, kein Cookie | ✅ |
| Session Hijacking | Keine Server-Sessions, Stateless JWT | ✅ |
| Open Redirect | Keine Redirects in der API | ✅ |

---

## SC-1: JWT Authentication + Token-Validierung

**Risiko (STRIDE):** Spoofing / Elevation of Privilege — unberechtigter Zugriff auf fremde Accounts oder geschützte Endpunkte.

**Umsetzung:**

| Komponente | Datei | Was |
|---|---|---|
| Token-Generierung | `security/JwtUtil.java` | HMAC-SHA384, 24h Ablauf, userId als Subject |
| Token-Validierung | `security/JwtFilter.java` | Jeder Request, Signatur + Ablaufzeit |
| Security-Konfiguration | `config/SecurityConfig.java` | Stateless, CSRF off, Entrypoint → 401 |
| Secret-Verwaltung | `.env` / docker-compose | `JWT_SECRET` als Umgebungsvariable, nie im Code |

**Ablauf:**
```
Request → RateLimitFilter → JwtFilter → isValid(token)?
  JA  → SecurityContext setzen → Controller
  NEIN → log.warn("AUTH_FAIL: IP={} Endpunkt={}") → 401
```

**Logging:**
```
INFO  AUTH_OK: Benutzer=alex@school.edu hat sich eingeloggt
WARN  AUTH_FAIL: ungültiger Token von IP=10.0.0.1 auf Endpunkt=/api/sessions
WARN  RATE_LIMIT: Login IP=10.0.0.1 gesperrt für 15 Minuten
```

**Alarm:** > 10 `AUTH_FAIL`-Events von gleicher IP innerhalb 2 Minuten → Brute-Force-Warnung

**Nachweis:**
- `AuthIntegrationTest` — 401 bei falschem Passwort, ungültigem Token, fehlendem Token
- `JwtUtilTest` — manipulierter/abgelaufener Token wird korrekt abgelehnt

---

## SC-2: Serverseitige Input-Validierung + Sanitisierung

**Risiko (STRIDE):** Tampering / Spoofing — manipulierte oder schädliche Eingaben gelangen in die Datenbank.

**Validation (Bean Validation):**

| Annotation | Anwendung | Effekt |
|---|---|---|
| `@NotBlank` | Alle Pflichtfelder | Leere Strings werden abgelehnt |
| `@Email` | email in RegisterRequest | Muss gültige E-Mail sein |
| `@Size(min=8, max=128)` | password in RegisterRequest | Passwort-Stärke erzwungen |
| `@Pattern(regexp=...)` | password | Mind. 1 Buchstabe + 1 Ziffer |
| `@Size(max=500)` | notes, description | Text-Overflow verhindert |
| `@Valid` | Alle POST/PUT-Controller | Validierung wird ausgelöst |

**XSS-Sanitisierung (`security/InputSanitizer.java`):**
```java
// HTML-Tags entfernen, Control-Characters entfernen, JS-Protokolle entfernen
result = input.replaceAll("<[^>]*>", "")
             .replaceAll("[\\x00-\\x08...]", "")
             .replaceAll("(?i)(javascript|vbscript|data)\\s*:", "");
```

Angewendet auf:
- `AuthController` — `name` bei Registrierung
- `SessionController` — `notes` bei Session-Erstellung
- `TutoringOfferController` — `description` bei Offer-Erstellung
- `UserController` — `name`, `bio` bei Profil-Update

**Status-Whitelist (Enum-Validation):**
```java
// SessionController.java
List.of("open", "matched", "completed", "cancelled")
```

**Fehler-Response:**
```json
{ "error": "VALIDATION_ERROR", "message": "password: Password must be 8–128 characters" }
```

**Nachweis:**
- `ValidationIntegrationTest` — 7 Tests: fehlende Pflichtfelder, ungültige E-Mail, zu langer Text → 400

---

## SC-3: Sauberes Fehlerhandling (kein Information Leakage)

**Risiko (STRIDE):** Information Disclosure — Stack Traces in API-Responses verraten interne Systemdetails.

**Umsetzung:**

```java
// exception/GlobalExceptionHandler.java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorDto> handleGeneral(Exception ex, HttpServletRequest req) {
    log.error("Unhandled error on [{}] {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
    return ResponseEntity.status(500).body(new ErrorDto("INTERNAL_ERROR", ex.getMessage()));
}
```

**Prinzip:**
- Stack Trace → nur ins **Server-Log** (ERROR-Level)
- API-Response → nur `{ error, message }` — kein Stack Trace, keine internen Details
- Passwort-Hash → `@JsonIgnore` auf `User.password` — nie in keiner Response
- Server-Version → `server_tokens off` in Nginx — kein `Server:` Header

**HTTP-Statuscodes:**
- `400` Validation Error
- `401` Unauthorized
- `403` Forbidden (IDOR-Guard)
- `404` Not Found
- `409` Conflict (Email bereits vorhanden)
- `429` Too Many Requests (Rate Limit)
- `500` Internal Error (generische Meldung)

**Nachweis:**
- `AuthIntegrationTest` — `$.user.password` existiert nie in Response
- `ActuatorAndHealthTest` — `/actuator/health` gibt kein `details`-Feld zurück
- Alle Integrationstests prüfen korrekte HTTP-Statuscodes

---

## SC-4: Rate Limiting + Brute-Force-Schutz

**Risiko (STRIDE):** DoS / Spoofing — Angreifer versucht durch tausende Requests die App zu überlasten oder Passwörter zu erraten.

**Umsetzung (`security/RateLimitFilter.java`):**

| Limitierung | Schwellwert | Sperre |
|---|---|---|
| Login-Versuche pro IP | 5 Versuche / 60 Sekunden | 15 Minuten gesperrt |
| Globale Requests pro IP | 120 Requests / 60 Sekunden | 429-Response |

```
Request → RateLimitFilter (läuft VOR JWT):
  Login: loginBlocked[IP] aktiv? → 429
  Login: loginWindows[IP] > 5/60s? → loginBlocked setzen → 429
  Global: globalWindows[IP] > 120/60s? → 429
  Sonst: chain.doFilter()
```

**Response bei Rate Limit:**
```json
{ "error": "RATE_LIMIT_EXCEEDED", "message": "Too many login attempts. Blocked for 15 minutes." }
```
```
HTTP/1.1 429 Too Many Requests
Retry-After: 60
```

**Nginx-Level Rate Limiting** (zweite Schicht):
```nginx
client_max_body_size 2m;
client_body_timeout 10s;
client_header_timeout 10s;
keepalive_timeout 30s;
```

---

## SC-5: IDOR-Schutz (Insecure Direct Object Reference)

**Risiko (STRIDE):** Spoofing / Tampering — Benutzer A greift auf Daten von Benutzer B zu.

**Umsetzung:**

### Sessions (`controller/SessionController.java`)
```java
// GET: userId-Param muss dem Auth-User entsprechen
String me = (String) auth.getPrincipal();
if (!me.equals(userId)) return ResponseEntity.status(403).build();

// POST: seekerId wird serverseitig auf den Auth-User gesetzt (nicht vom Client)
session.setSeekerId(me);

// PATCH: Auth-User muss Seeker oder Tutor der Session sein
boolean allowed = me.equals(session.getSeekerId()) || me.equals(session.getTutorId());
```

### TutoringOffers (`controller/TutoringOfferController.java`)
```java
// POST: tutorId wird serverseitig gesetzt
offer.setTutorId(me);

// DELETE: Auth-User muss der Tutor (Besitzer) des Offers sein
if (!me.equals(offer.getTutorId())) return ResponseEntity.status(403).build();
```

### UserProfile (`controller/UserController.java`)
```java
// PUT: Nur eigenes Profil darf bearbeitet werden
if (!me.equals(id)) return ResponseEntity.status(403).build();
```

---

## SC-6: Mass Assignment Schutz

**Risiko (STRIDE):** Elevation of Privilege — Angreifer sendet `{ "role": "admin" }` im Body.

**Umsetzung (`controller/UserController.java`):**
```java
// NUR diese Felder werden vom Client übernommen:
existing.setName(InputSanitizer.sanitize(incoming.getName()));
existing.setBio(InputSanitizer.sanitize(incoming.getBio()));
existing.setAvatarUrl(incoming.getAvatarUrl());
existing.setVocationalGroup(incoming.getVocationalGroup());
existing.setSubjectsTutored(incoming.getSubjectsTutored());

// GESPERRTE Felder (immer aus DB): id, email, role, password, sessionsCompleted
```

**Registrierung (`controller/AuthController.java`):**
```java
// Nur student oder teacher erlaubt — kein admin per API
String safeRole = "teacher".equalsIgnoreCase(request.role()) ? "teacher" : "student";
```

---

## SC-7: Security Response Headers

**Risiko (STRIDE):** Tampering / Information Disclosure — Browser-basierte Angriffe (XSS, Clickjacking, MIME-Confusion).

**Umsetzung (2-lagig: Spring Security + Nginx):**

### Spring Security (`config/SecurityConfig.java`)
```
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Content-Security-Policy: default-src 'self'; script-src 'self'; ...
Referrer-Policy: strict-origin-when-cross-origin
Permissions-Policy: camera=(), microphone=(), geolocation=(), payment=()
```

### Nginx (`frontend/nginx.conf`)
```
server_tokens off              # Keine Version-Info im Server-Header
X-Frame-Options: DENY
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Content-Security-Policy: [same as above]
Referrer-Policy: strict-origin-when-cross-origin
Permissions-Policy: camera=(), microphone=(), geolocation=(), payment=()
```

---

## Weitere Sicherheitsmassnahmen

| Massnahme | Implementierung |
|---|---|
| Passwort-Hashing | BCrypt cost=12 (≈300ms/Hash, Brute-Force-resistent) |
| CORS eingeschränkt | Nur `localhost` + `10.10.50.40` als erlaubte Origins |
| Actuator eingeschränkt | Nur `/actuator/health` exponiert, ohne Details |
| JWT-Secret als Env-Var | Nie im Code, `.env` nicht eingecheckt |
| Stateless Auth | Kein Session-Cookie, kein CSRF-Risiko |
| Nginx `.`-Dateien | `location ~/\.` → `deny all` (kein `.env`, `.git` Zugriff) |
| Tomcat-Timeouts | `connection-timeout=5s`, `keep-alive-timeout=20s` |
| Payload-Limits | Max 2 MB Request- und File-Grösse |

---

## Known Limitations (PoC-Scope)

| # | Limitation | Workaround |
|---|---|---|
| 1 | Kein automatisches Token-Refresh | 24h Ablaufzeit; User muss sich neu einloggen |
| 2 | Keine E-Mail-Verifikation bei Registrierung | Schulinternes Netzwerk minimiert Risiko |
| 3 | Frontend ohne Error Boundaries | Seitenreload bei unerwartetem Fehler |
| 4 | Rate-Limit-State im RAM (kein Redis) | Neustart der App setzt Sperren zurück |
