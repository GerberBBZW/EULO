# EULO – Betriebskonzept / Runbook

## D4.1 Start / Stop / Restart

### Gesamten Stack starten
```bash
docker compose up -d
docker compose ps          # Status prüfen: alle 3 Container "running"
```

### Einzelne Services starten
```bash
docker compose up -d eulo-mongo      # Datenbank
docker compose up -d eulo-backend    # Backend
docker compose up -d eulo-frontend   # Frontend
```

### Stoppen
```bash
docker compose stop                  # Graceful Stop (Daten bleiben)
docker compose down                  # Stop + Container entfernen
docker compose down -v               # ⚠️ VORSICHT: Volumes/Daten gelöscht
```

### Neustart
```bash
docker compose restart               # Alle Container
docker compose restart eulo-backend  # Nur Backend
docker compose restart eulo-frontend # Nur Frontend
docker compose restart eulo-mongo    # Nur MongoDB
```

### Rebuild nach Code-Änderung
```bash
docker compose build eulo-backend
docker compose up -d eulo-backend
# oder alles auf einmal:
docker compose up --build -d
```

---

## D4.2 Health-Checks

### Backend erreichbar?
```bash
curl http://localhost:8080/actuator/health
# Erwartet: {"status":"UP"}

# Auf Server:
curl http://10.10.50.40:8080/actuator/health
```

### Frontend erreichbar?
```
Browser: http://localhost:3000
Browser (Server): http://10.10.50.40:3000
```

### MongoDB erreichbar?
```bash
docker exec eulo-mongo mongosh --eval "db.adminCommand('ping')"
# Erwartet: { ok: 1 }
```

### Alle Container-Status prüfen
```bash
docker compose ps
# Erwartet: alle 3 Container Status "running"
```

---

## D4.3 Monitoring & Logging

### Logging-Konfiguration
```properties
logging.level.root=WARN
logging.level.com.eulo=INFO
logging.level.org.springframework.security=WARN
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n
```

### Logs live anzeigen
```bash
docker compose logs -f eulo-backend   # Backend-Logs
docker compose logs -f                # Alle Logs
docker compose logs --tail=100 eulo-backend  # Letzte 100 Zeilen
```

### Messpunkte

**M1 – Auth-Events**
```
INFO  AUTH_OK: Benutzer=alex@school.edu hat sich eingeloggt
WARN  AUTH_FAIL: ungültiger Token von IP=10.0.0.1 auf Endpunkt=/api/sessions
```

**M2 – Business-Events (Buchungen)**
```
INFO  EVENT=BOOKING_CREATED tutorId=u2 studentId=u1 subject=Web Development
INFO  EVENT=BOOKING_CANCELLED reason=user-action bookingId=sess1
INFO  EVENT=BOOKING_STATUS_CHANGED bookingId=sess1 from=open to=matched
```

**M3 – 5xx Fehler**
```
ERROR Unhandled error on [POST] /api/sessions: Connection refused (MongoDB)
```

### Alarm-Trigger

| Trigger | Bedingung | Massnahme |
|---|---|---|
| 5xx-Fehlerrate | > 5 Fehler / 5 Min. | Backend-Logs prüfen, Neustart |
| Auth-Fehler (Brute-Force) | > 10 AUTH_FAIL / IP / 2 Min. | IP blockieren, Alert |

---

## D4.4 Incident Recovery

### Szenario A: Backend antwortet nicht (502 / Timeout)

```bash
# 1. Symptom prüfen
curl http://localhost:8080/actuator/health  # kein 200?

# 2. Container-Status
docker compose ps

# 3. Logs lesen
docker compose logs --tail=100 eulo-backend

# 4. Neustart versuchen
docker compose restart eulo-backend

# 5. Health-Check wiederholen
curl http://localhost:8080/actuator/health  # erwartet: UP

# 6. Falls kein Erfolg: Rebuild
docker compose build eulo-backend && docker compose up -d eulo-backend
```

### Szenario B: MongoDB-Verbindung verloren

```bash
# 1. Log-Symptom: MongoTimeoutException oder "Connection refused"
docker compose logs --tail=50 eulo-backend | grep -i mongo

# 2. MongoDB-Status prüfen
docker compose ps eulo-mongo

# 3. MongoDB-Logs
docker compose logs --tail=50 eulo-mongo

# 4. MongoDB neustarten
docker compose restart eulo-mongo

# 5. Backend neustarten (Connection-Pool neu aufbauen)
docker compose restart eulo-backend

# 6. Datenintegrität prüfen
docker exec eulo-mongo mongosh --eval "db.getCollectionNames()"
```

### Szenario C: JWT-Auth ausgefallen (alle Requests → 401)

```bash
# 1. Log prüfen
docker compose logs eulo-backend | grep AUTH_FAIL

# 2. Symptom: JWT_SECRET geändert? → alle Tokens ungültig
# Alle User müssen sich neu einloggen

# 3. Secret prüfen (in .env)
cat .env | grep JWT_SECRET

# 4. Backend neustarten nach Secret-Korrektur
docker compose restart eulo-backend
```

### Szenario D: Frontend zeigt leere Seite

```bash
# 1. Browser-Konsole prüfen (F12)
# 2. Nginx-Logs
docker compose logs eulo-frontend

# 3. API erreichbar?
curl http://localhost:8080/actuator/health

# 4. Frontend neustarten
docker compose restart eulo-frontend
```

---

## D4.5 Known Issues & Grenzen

| # | Issue | Auswirkung | Workaround |
|---|---|---|---|
| 1 | MongoDB (NoSQL) statt relational | Kein JOIN, kein ACID | Collections = konzeptuelle Tabellen |
| 2 | Kein Token-Refresh | Session läuft nach 24h ab | User muss sich neu einloggen |
| 3 | Kein Rate-Limiting | Spam-Requests möglich | AUTH_FAIL-Logs als Frühwarnung |
| 4 | Keine E-Mail-Verifikation | Fake-Accounts möglich | Schulinternes Netzwerk |
| 5 | Keine automatischen Backups | Datenverlust bei Crash | Manuell: `mongodump` |
| 6 | Demo-Credentials hardcoded | Sicherheitsrisiko in Prod | Nur für PoC/Demo |

---

## D4.6 Offline-Strategie

EULO benötigt im laufenden Betrieb **keine** Internetverbindung.

| Komponente | Internet-Abhängigkeit | Status offline |
|---|---|---|
| Spring Boot Backend | Maven-Deps im Image eingebettet | ✅ Vollständig offline |
| React Frontend | npm-Pakete im Nginx-Image | ✅ Vollständig offline |
| MongoDB | Docker-Image bereits gepullt | ✅ Offline nach erstem Pull |
| JWT-Verarbeitung | Reine HMAC-Signatur, lokal | ✅ Vollständig offline |
| Dependency-Cache | maven_cache Volume | ✅ Tests offline mit `-o` Flag |

**Tests offline ausführen:**
```bash
docker compose --profile test run --rm test mvn test -o -B
```
