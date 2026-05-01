# EULO – API-Dokumentation

Base URL: `http://<host>:3000/api`  
Alle geschützten Endpoints erfordern: `Authorization: Bearer <JWT-Token>`

---

## Authentifizierung

### POST /api/auth/login
Benutzer einloggen und JWT-Token erhalten.

**Request:**
```json
{ "email": "alex.rivera@school.edu", "password": "password123" }
```
**Response 200:**
```json
{
  "token": "eyJhbGci...",
  "user": { "id": "u1", "name": "Alex Rivera", "email": "...", "role": "student" }
}
```
**Fehler:** `401` bei falschen Credentials

---

### POST /api/auth/register
Neuen Benutzer registrieren.

**Request:**
```json
{ "name": "Max Muster", "email": "max@school.edu", "password": "sicher123", "role": "student" }
```
**Response 201:**
```json
{ "token": "eyJhbGci...", "user": { "id": "...", "name": "Max Muster", ... } }
```
**Fehler:** `400` bei fehlenden Pflichtfeldern, `409` wenn E-Mail bereits vergeben

---

## Benutzer

### GET /api/users/{id} 🔒
Benutzerprofil abrufen. Passwort-Hash wird **nie** zurückgegeben.

**Response 200:**
```json
{ "id": "u1", "name": "Alex Rivera", "email": "...", "role": "student", "bio": "...", "sessionsCompleted": 12 }
```
**Fehler:** `404` wenn nicht gefunden

---

### PUT /api/users/{id} 🔒
Profil aktualisieren. Passwort-Hash bleibt serverseitig erhalten.

**Request:**
```json
{ "name": "Alex Updated", "bio": "Neue Bio", "vocationalGroup": "Information Technology" }
```
**Response 200:** Aktualisierter User (ohne password)

---

## Fächer & Berufsgruppen

### GET /api/subjects 🌐
Alle Fächer abrufen (öffentlich, kein Token nötig).

**Response 200:**
```json
[{ "id": "s1", "name": "Web Development", "category": "vocational", "tutorCount": 5 }]
```

---

### GET /api/vocational-groups 🌐
Alle Berufsgruppen abrufen (öffentlich).

**Response 200:**
```json
[{ "id": "vg1", "name": "Information Technology", "description": "...", "icon": "💻", "subjects": [...] }]
```

---

## Nachhilfeangebote

### GET /api/offers 🌐
Alle Angebote abrufen mit optionalen Filtern (öffentlich).

**Query-Parameter:**
- `search` — Freitext-Suche (Tutorname, Fach, Beschreibung)
- `subject` — Filter nach Fachname
- `mode` — Filter nach Modus (`online`, `onsite`, `both`)

**Response 200:**
```json
[{ "id": "o1", "tutorId": "u2", "tutorName": "Sarah Chen", "subjectName": "Web Development", "mode": "online", ... }]
```

---

### GET /api/offers/tutor/{tutorId} 🔒
Eigene Angebote eines Tutors abrufen.

**Response 200:** Array von TutoringOffer

---

### POST /api/offers 🔒
Neues Nachhilfeangebot erstellen.

**Request:**
```json
{
  "tutorId": "u1", "tutorName": "Alex Rivera",
  "subjectId": "s1", "subjectName": "Web Development",
  "mode": "online", "description": "HTML & CSS", "availability": "Mon, Wed"
}
```
**Validierung:** `tutorId`, `tutorName`, `subjectId`, `subjectName`, `mode` sind Pflichtfelder. `description` max 500 Zeichen.  
**Response 200:** Erstelltes TutoringOffer  
**Fehler:** `400` bei Validierungsfehler

---

### DELETE /api/offers/{id} 🔒
Angebot löschen.

**Response 204:** No Content

---

## Sessions (Buchungen)

### GET /api/sessions?userId={id} 🔒
Alle Sessions eines Benutzers abrufen (als Seeker und als Tutor).

**Response 200:**
```json
[{ "id": "sess1", "seekerId": "u1", "tutorId": "u2", "subjectName": "Web Development", "status": "matched", "date": "2026-05-02T10:00:00Z", "mode": "online" }]
```

---

### POST /api/sessions 🔒
Neue Session (Buchungsanfrage) erstellen.

**Request:**
```json
{
  "seekerId": "u1", "seekerName": "Alex Rivera",
  "tutorId": "u2", "tutorName": "Sarah Chen",
  "subjectId": "s1", "subjectName": "Web Development",
  "status": "open", "date": "2026-05-10T14:00:00Z", "mode": "online"
}
```
**Validierung:** Alle Felder ausser `notes` sind Pflicht. `notes` max 500 Zeichen.  
**Response 200:** Erstellte Session

---

### PATCH /api/sessions/{id}/status 🔒
Session-Status ändern.

**Request:**
```json
{ "status": "matched" }
```
**Statusübergänge:** `open` → `matched` → `completed` oder `cancelled`  
**Response 200:** Aktualisierte Session  
**Fehler:** `404` wenn Session nicht gefunden

---

## Dashboard

### GET /api/dashboard/stats?userId={id} 🔒
Dashboard-Statistiken für einen Benutzer.

**Response 200:**
```json
{
  "upcomingSessions": 2,
  "availableTutors": 5,
  "subjectsOffered": 5,
  "upcomingSession": { "id": "sess1", "status": "matched", "date": "2026-05-02T10:00:00Z", ... }
}
```
Nur zukünftige Sessions (Datum > jetzt) werden gezählt. `upcomingSession` = nächste gematchte Session als Seeker, sortiert nach Datum.

---

## Health-Check

### GET /actuator/health 🌐
Backend-Gesundheitsstatus (öffentlich, kein Token nötig).

**Response 200:**
```json
{ "status": "UP" }
```

---

## Fehler-Format

Alle Validierungsfehler und Server-Fehler liefern:
```json
{ "error": "VALIDATION_ERROR", "message": "email: muss eine gültige E-Mail-Adresse sein" }
```

| HTTP-Code | Bedeutung |
|---|---|
| 200 | OK |
| 201 | Created (Register) |
| 204 | No Content (Delete) |
| 400 | Validation Error |
| 401 | Unauthorized (kein/ungültiger Token) |
| 404 | Not Found |
| 409 | Conflict (E-Mail bereits vergeben) |
| 500 | Internal Server Error |

---

## Legende

- 🌐 Öffentlich (kein Token erforderlich)
- 🔒 Authentifizierung erforderlich (`Authorization: Bearer <token>`)
