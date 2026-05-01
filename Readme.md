# EULO – Education Unlocked by Learning One-to-one

Peer-to-Peer Nachhilfeplattform für Berufsschüler | BBZW Sursee | Gruppe 04 – Code Crafters

## Schnellstart

```bash
cp .env.example .env          # JWT_SECRET setzen
docker compose up --build -d  # Stack starten
# → http://localhost:3000
```

**Demo-Login:** `alex.rivera@school.edu` / `password123`

---

## Dokumentation

| Dokument | Inhalt |
|---|---|
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | Systemaufbau, Tech Stack, Datenmodell |
| [API.md](docs/API.md) | Alle Endpoints mit Request/Response |
| [SECURITY.md](docs/SECURITY.md) | Security Controls SC-1/SC-2/SC-3 (INA) |
| [TESTING.md](docs/TESTING.md) | Teststrategie, 64 Tests, Protokoll |
| [DEPLOYMENT.md](docs/DEPLOYMENT.md) | Lokal + Server (10.10.50.40) |
| [RUNBOOK.md](docs/RUNBOOK.md) | Start/Stop/Restart/Incident Recovery |

---

## Tech Stack

```
React + TypeScript + Vite + Tailwind + Nginx
        ↓
Spring Boot 3.2 (Java 21) + Spring Security + JWT
        ↓
MongoDB 7  (eulo-mongo Container)
```

---

## Tests ausführen

```bash
# Erstes Mal (Internet nötig – lädt Maven-Deps):
docker compose --profile test run --rm test

# Offline (nach erstem Mal):
docker compose --profile test run --rm test mvn test -o -B
```

**64 Tests** in 10 Klassen: Unit + Integration + Smoke – alle grün ✅

---

## Security Controls (INA – LB245-2)

| # | Control | Nachweis |
|---|---|---|
| SC-1 | JWT Auth – Token-Validierung, AUTH_FAIL/OK Logging | `JwtFilter.java` + `AuthIntegrationTest` |
| SC-2 | Input-Validierung – @NotBlank/@Email/@Size, HTTP 400 | `ValidationIntegrationTest` |
| SC-3 | Fehlerhandling – kein Stack Trace in API, nur {error,message} | `GlobalExceptionHandler.java` |

---

## Server-Deployment (TechJam)

```
AppServer: ssh g03admin@10.10.50.40  →  http://10.10.50.40:3000
DBServer:  ssh g03admin@10.10.50.30  →  MongoDB :27017
Passwort:  Sursee6210
```

Vollständige Anleitung: [DEPLOYMENT.md](docs/DEPLOYMENT.md)

---

## Health-Check

```bash
curl http://localhost:8080/actuator/health
# → {"status":"UP"}
```
