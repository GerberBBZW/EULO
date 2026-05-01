# EULO – Security Controls (INA)

## Übersicht

Drei Security Controls wurden im Backend vollständig umgesetzt und sind durch Tests nachgewiesen.

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
Request → JwtFilter → isValid(token)?
  JA  → SecurityContext setzen → Controller
  NEIN → log.warn("AUTH_FAIL: IP={} Endpunkt={}") → 401
```

**Logging:**
```
INFO  AUTH_OK: Benutzer=alex@school.edu hat sich eingeloggt
WARN  AUTH_FAIL: ungültiger Token von IP=10.0.0.1 auf Endpunkt=/api/sessions
```

**Alarm:** > 10 `AUTH_FAIL`-Events von gleicher IP innerhalb 2 Minuten → Brute-Force-Warnung

**Nachweis:**
- `AuthIntegrationTest` — 401 bei falschem Passwort, ungültigem Token, fehlendem Token
- `JwtUtilTest` — manipulierter/abgelaufener Token wird korrekt abgelehnt

---

## SC-2: Serverseitige Input-Validierung

**Risiko (STRIDE):** Tampering — manipulierte oder fehlende Eingaben gelangen in die Datenbank.

**Umsetzung:**

| Annotation | Anwendung | Effekt |
|---|---|---|
| `@NotBlank` | Alle ID- und Name-Felder | Leere Strings werden abgelehnt |
| `@Email` | email in RegisterRequest | Muss gültige E-Mail sein |
| `@Size(max=500)` | notes, description | Text-Overflow verhindert |
| `@Valid` | Alle POST/PUT-Controller | Validierung wird ausgelöst |

**Betroffene Dateien:**
- `model/Session.java` — 8 Felder mit `@NotBlank`, notes mit `@Size(max=500)`
- `model/TutoringOffer.java` — 5 Felder mit `@NotBlank`, description mit `@Size(max=500)`
- `controller/AuthController.java` — `@NotBlank @Email` auf RegisterRequest
- `controller/SessionController.java` — `@Valid @RequestBody`
- `controller/TutoringOfferController.java` — `@Valid @RequestBody`

**Fehler-Response:**
```json
{ "error": "VALIDATION_ERROR", "message": "email: muss eine gültige E-Mail-Adresse sein" }
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
- Passwort-Hash → `@JsonIgnore` auf `User.password` — nie in keiner Response enthalten

**HTTP-Statuscodes:**
- `400` Validation Error
- `401` Unauthorized
- `404` Not Found
- `409` Conflict
- `500` Internal Error (generische Meldung)

**Nachweis:**
- `AuthIntegrationTest` — `$.user.password` existiert nie in Response
- `ActuatorAndHealthTest` — `/actuator/health` gibt kein `details`-Feld zurück
- Alle Integrationstests prüfen korrekte HTTP-Statuscodes

---

## Weitere Sicherheitsmassnahmen

| Massnahme | Implementierung |
|---|---|
| Passwort-Hashing | BCrypt (Spring Security Standard, Saltfaktor 10) |
| CORS eingeschränkt | Nur `localhost` + `10.10.50.40` als erlaubte Origins |
| Actuator eingeschränkt | Nur `/actuator/health` exponiert, ohne Details |
| JWT-Secret als Env-Var | Nie im Code, `.env` nicht eingecheckt |
| Stateless Auth | Kein Session-Cookie, kein CSRF-Risiko |

---

## Known Limitations (PoC-Scope)

| # | Limitation | Workaround |
|---|---|---|
| 1 | Kein Rate-Limiting auf API-Ebene | Brute-Force erkennbar über AUTH_FAIL-Logs |
| 2 | Kein automatisches Token-Refresh | 24h Ablaufzeit; User muss sich neu einloggen |
| 3 | Keine E-Mail-Verifikation bei Registrierung | Schulinternes Netzwerk minimiert Risiko |
| 4 | Frontend ohne Error Boundaries | Seitenreload bei unerwartetem Fehler |
