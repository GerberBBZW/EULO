# EULO – Teststrategie & Testprotokoll

## Übersicht

| Testklasse | Typ | Tests | Beschreibung |
|---|---|---|---|
| `AuthIntegrationTest` | Integration | 8 | Login Happy Path, Register, Negativtests |
| `OfferIntegrationTest` | Integration | 10 | Angebote CRUD + Filter |
| `SessionIntegrationTest` | Integration | 9 | Sessions CRUD + Status |
| `UserIntegrationTest` | Integration | 6 | Profil abrufen + aktualisieren |
| `SubjectAndDashboardIntegrationTest` | Integration | 6 | Subjects, Vocational Groups, Dashboard |
| `ValidationIntegrationTest` | Integration | 7 | Input-Validierung (SC-2 Nachweis) |
| `ActuatorAndHealthTest` | Smoke | 3 | Health-Endpoint |
| `JwtUtilTest` | Unit | 6 | Token-Generierung und Validierung |
| `UserServiceTest` | Unit | 5 | Authentifizierung und Benutzer-CRUD |
| `SessionServiceTest` | Unit | 4 | Session-Buchungslogik |
| **Total** | | **64** | |

---

## Tests ausführen

### Lokal (mit Docker, persistenter Cache)

```bash
# Erstes Mal (Internet nötig, lädt Abhängigkeiten):
docker compose --profile test run --rm test

# Alle weiteren Male offline:
docker compose --profile test run --rm test mvn test -o -B
```

### Lokal ohne Docker (Maven installiert):
```bash
cd backend
mvn test -B
```

---

## Technische Basis

- **@SpringBootTest + @AutoConfigureMockMvc** — vollständige HTTP-Schicht inkl. Spring Security
- **@MockBean** für alle 5 Repositories — kein laufendes MongoDB nötig
- **TestBase** — gemeinsame Basisklasse mit `testUser`, `obtainToken()` und Mock-Setup
- **Mockito** für Repository-Mocks

---

## Smoke-Tests (Happy Path)

| ID | Test | Vorgehen | Erwartung | Status |
|---|---|---|---|---|
| SM-01 | Backend erreichbar | `GET /actuator/health` | `{"status":"UP"}` | ✅ |
| SM-02 | Frontend erreichbar | Browser: `http://localhost:3000` | Login-Seite | ✅ |
| SM-03 | MongoDB erreichbar | `docker exec eulo-mongo mongosh --eval "db.adminCommand('ping')"` | `{ok:1}` | ✅ |
| SM-04 | Registrierung | `POST /api/auth/register` mit gültigen Daten | HTTP 201 + JWT | ✅ |
| SM-05 | Login | `POST /api/auth/login` mit gültigen Credentials | HTTP 200 + JWT | ✅ |
| SM-06 | Angebot erstellen | `POST /api/offers` mit JWT | HTTP 200 + Offer | ✅ |
| SM-07 | Session buchen | `POST /api/sessions` mit JWT | HTTP 200 + Session | ✅ |
| SM-08 | Dashboard | `GET /api/dashboard/stats?userId=u1` mit JWT | HTTP 200 + Stats | ✅ |

---

## Negativtests (Fehlerfälle)

| ID | Test | Vorgehen | Erwartung | Status |
|---|---|---|---|---|
| NT-01 | Falsches Passwort | `POST /api/auth/login` mit falschem PW | HTTP 401, AUTH_FAIL im Log | ✅ |
| NT-02 | Kein JWT-Token | `GET /api/sessions` ohne Authorization | HTTP 401 | ✅ |
| NT-03 | Fehlende E-Mail | `POST /api/auth/register` ohne email | HTTP 400, VALIDATION_ERROR | ✅ |
| NT-04 | Ungültiger Token | `GET /api/sessions` mit manipuliertem JWT | HTTP 401 | ✅ |
| NT-05 | Unbekannte ID | `GET /api/users/nobody` | HTTP 404 | ✅ |
| NT-06 | Notizen zu lang | `POST /api/sessions` mit notes > 500 Zeichen | HTTP 400 | ✅ |
| NT-07 | Fehlende tutorId | `POST /api/offers` ohne tutorId | HTTP 400 | ✅ |
| NT-08 | Abgelaufener Token | isValid(expiredToken) | false | ✅ |

---

## Unit-Tests (INA-Nachweis: 4–6 gefordert, 15 geliefert)

### JwtUtilTest (6 Tests)
```
✅ generateToken → Token nicht leer
✅ isValid → gültiger Token accepted
✅ isValid → manipulierter Token abgelehnt
✅ isValid → Garbage-Token abgelehnt
✅ extractUserId → korrekte User-ID extrahiert
✅ isValid → abgelaufener Token abgelehnt
```

### UserServiceTest (5 Tests)
```
✅ authenticate → korrektes Passwort → User zurück
✅ authenticate → falsches Passwort → leer
✅ authenticate → unbekannte E-Mail → leer
✅ findById → bekannte ID → User
✅ findById → unbekannte ID → leer
```

### SessionServiceTest (4 Tests)
```
✅ save → Session wird persistiert
✅ updateStatus → Status korrekt geändert
✅ updateStatus → unbekannte ID → leer, kein save
✅ findByUserId → gibt Seeker- und Tutor-Sessions zurück
```

---

## Test-Architektur

```
TestBase (abstract)
├── @SpringBootTest + @AutoConfigureMockMvc
├── @MockBean: 5 Repositories (kein MongoDB nötig)
├── testUser: BCrypt-gehashtes Passwort
└── obtainToken(): POST /api/auth/login → JWT

Alle Integrationstests extends TestBase:
├── AuthIntegrationTest
├── OfferIntegrationTest
├── SessionIntegrationTest
├── UserIntegrationTest
├── SubjectAndDashboardIntegrationTest
├── ValidationIntegrationTest
└── ActuatorAndHealthTest

Alle Unit-Tests mit Mockito (@ExtendWith(MockitoExtension.class)):
├── JwtUtilTest   (kein Spring Context, reine Java-Instanzierung)
├── UserServiceTest
└── SessionServiceTest
```
