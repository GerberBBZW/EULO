# EULO – Systemarchitektur

## Übersicht

EULO (Education Unlocked by Learning One-to-one) ist eine Peer-to-Peer-Nachhilfeplattform für Berufsschüler. Die Applikation ist als vollständig containerisierter Stack konzipiert und läuft offline ohne externe Dienste.

```
Browser
  │
  ▼
┌─────────────────────────────┐
│  Frontend (React + Nginx)   │  Port 3000
│  eulo-frontend Container    │
│  Nginx: /api/ → backend     │
└────────────┬────────────────┘
             │ HTTP (intern)
             ▼
┌─────────────────────────────┐
│  Backend (Spring Boot 3.2)  │  Port 8080 (intern)
│  eulo-backend Container     │
│  JWT Stateless Auth         │
└────────────┬────────────────┘
             │ MongoDB Wire Protocol
             ▼
┌─────────────────────────────┐
│  Datenbank (MongoDB 7)      │  Port 27017 (intern)
│  eulo-mongo Container       │
│  Persistenz: mongo_data Vol │
└─────────────────────────────┘
```

---

## Tech Stack

| Schicht | Technologie | Version |
|---|---|---|
| Frontend | React + TypeScript + Vite | 18 / 5 |
| Styling | Tailwind CSS | 3 |
| HTTP-Server | Nginx (Alpine) | latest |
| Backend | Spring Boot | 3.2.5 |
| Sprache | Java | 21 |
| Build | Maven | 3.9 |
| Authentifizierung | JWT (jjwt) | 0.12.5 |
| Passwort-Hashing | BCrypt | Spring Security |
| Datenbank | MongoDB | 7 |
| ODM | Spring Data MongoDB | - |
| Containerisierung | Docker + Docker Compose | - |

---

## Projektstruktur

```
EULO/
├── docker-compose.yml          # Multi-Service Orchestrierung
├── .env.example                # Umgebungsvariablen-Vorlage
├── docs/                       # Dokumentation
│   ├── ARCHITECTURE.md
│   ├── API.md
│   ├── SECURITY.md
│   ├── TESTING.md
│   ├── DEPLOYMENT.md
│   └── RUNBOOK.md
├── frontend/
│   ├── Dockerfile              # Multi-Stage: Node → Nginx
│   ├── nginx.conf              # SPA-Fallback + API-Proxy
│   ├── vite.config.ts          # Dev-Proxy → localhost:8080
│   └── src/
│       ├── api.ts              # Zentraler API-Client (fetch + JWT)
│       ├── hooks/useAuth.tsx   # Auth-State + JWT-Handling
│       ├── pages/              # LoginPage, Dashboard, FindTutoring,
│       │                       # OfferTutoring, MySessions, Profile
│       ├── components/         # SessionCard, TutorCard, Avatar, ...
│       └── types/index.ts      # TypeScript-Interfaces
└── backend/
    ├── Dockerfile              # Maven Build → JRE-Image
    ├── pom.xml
    └── src/main/java/com/eulo/
        ├── EuloApplication.java
        ├── DataInitializer.java        # Seed-Daten beim ersten Start
        ├── config/
        │   ├── SecurityConfig.java     # Spring Security, CSRF-off, Stateless
        │   └── CorsConfig.java         # CORS: localhost + Server-IPs
        ├── security/
        │   ├── JwtUtil.java            # Token generieren/validieren
        │   └── JwtFilter.java          # OncePerRequestFilter + Logging
        ├── controller/                 # REST-Controller (7 Stück)
        ├── service/                    # Business-Logik (6 Stück)
        ├── model/                      # Datenmodelle + Validation
        ├── repository/                 # Spring Data Repositories
        └── exception/
            └── GlobalExceptionHandler.java  # Zentrales Fehlerhandling
```

---

## Datenmodell

```
User
├── id (String, PK)
├── name, email (unique), password (@JsonIgnore)
├── role: student | teacher
├── vocationalGroup, avatarUrl, bio
├── subjectsTutored: String[]
└── sessionsCompleted: int

Subject
├── id, name, category, tutorCount

VocationalGroup
├── id, name, description, icon (Emoji)
└── subjects: Subject[]

TutoringOffer
├── id, tutorId, tutorName, tutorAvatar
├── subjectId, subjectName
├── mode: online | onsite | both
├── description (@Size max=500)
└── availability

Session
├── id
├── seekerId, seekerName
├── tutorId, tutorName
├── subjectId, subjectName
├── status: open | matched | completed | cancelled
├── date (ISO-8601)
├── mode: online | onsite
└── notes (@Size max=500)
```

---

## Authentifizierungsflow

```
1. POST /api/auth/login  {email, password}
2. Backend: BCrypt.matches(password, user.passwordHash)
3. JwtUtil.generateToken(userId)  →  HS384, 24h Ablauf
4. Response: { token, user }  (password-Feld via @JsonIgnore ausgeblendet)
5. Frontend: token in localStorage, bei jedem Request als Bearer-Header
6. JwtFilter: Token validieren → SecurityContext setzen
7. Bei 401: Token löschen + Reload (automatisches Logout)
```

---

## Container-Netzwerk

Alle Container kommunizieren im internen Docker-Netzwerk `eulo-net`.  
Nach aussen ist nur Port **3000** (Frontend/Nginx) exponiert.  
Das Backend (8080) und MongoDB (27017) sind **nicht** von aussen erreichbar.

```
Host:3000 → eulo-frontend:80 → [Nginx] → eulo-backend:8080 → eulo-mongo:27017
```

---

## Demo-Zugangsdaten

| Name | E-Mail | Passwort | Rolle |
|---|---|---|---|
| Alex Rivera | alex.rivera@school.edu | password123 | student |
| Sarah Chen | sarah.chen@school.edu | password123 | teacher |
| Marcus Johnson | marcus.johnson@school.edu | password123 | teacher |
| David Kim | david.kim@school.edu | password123 | teacher |
| Jenny Wilson | jenny.wilson@school.edu | password123 | student |
