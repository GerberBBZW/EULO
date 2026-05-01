# Logging

## Übersicht

Der EULO-Backend schreibt Logs gleichzeitig in zwei Ziele:

| Ziel | Konfiguration | Zweck |
|------|--------------|-------|
| Konsole (stdout) | immer aktiv | Docker-Logs (`docker logs eulo-backend`) |
| Datei `/app/logs/eulo.log` | `logging.file.name` | Persistenz, WAZUH-Integration |

---

## Log-Level

| Logger | Level | Inhalt |
|--------|-------|--------|
| `com.eulo` | INFO | Auth-Events, Buchungen, Rate-Limit-Warnungen |
| `org.springframework.security` | WARN | Security-Fehler |
| Alles andere | WARN | Framework-Warnungen und Fehler |

---

## Datei-Logging

### Pfade

```
/app/logs/eulo.log          ← aktuelle Log-Datei
/app/logs/eulo-YYYY-MM-DD.N.log  ← archivierte Dateien (täglich rotiert)
```

Auf dem **Host** (neben `docker-compose.yml`):

```
./logs/eulo.log
./logs/eulo-2026-05-01.0.log
```

### Rolling Policy

| Parameter | Wert | Bedeutung |
|-----------|------|-----------|
| `max-file-size` | 10 MB | Neue Datei wenn Limit erreicht |
| `max-history` | 30 Tage | Ältere Dateien werden gelöscht |
| `total-size-cap` | 500 MB | Gesamtlimit aller Archiv-Dateien |

### Log-Format

```
2026-05-01 14:23:01 INFO  [main] c.eulo.security.RateLimitFilter - RATE_LIMIT: Login gesperrt IP=1.2.3.4 noch 890s
2026-05-01 14:23:05 INFO  [main] c.eulo.controller.AuthController - AUTH_OK: Benutzer=alex@school.edu hat sich eingeloggt
2026-05-01 14:23:10 INFO  [main] c.eulo.service.SessionService - EVENT=BOOKING_CREATED tutorId=u2 studentId=u1 subject=Web Development
```

---

## Docker-Volume

Das Verzeichnis `./logs/` ist in `docker-compose.yml` als Volume eingebunden:

```yaml
backend:
  volumes:
    - ./logs:/app/logs
```

Das Verzeichnis wird beim ersten Start automatisch angelegt. Logs überleben einen Container-Neustart.

---

## WAZUH-Integration

### Agent-Konfiguration

In `/var/ossec/etc/ossec.conf` auf dem WAZUH-Agent-Host:

```xml
<ossec_config>
  <localfile>
    <log_format>syslog</log_format>
    <location>/pfad/zum/projekt/logs/eulo.log</location>
  </localfile>
</ossec_config>
```

Nach der Konfiguration Agent neu starten:

```bash
systemctl restart wazuh-agent
```

### Relevante Log-Events für WAZUH-Regeln

| Event | Log-Inhalt | Empfohlene Regel |
|-------|-----------|------------------|
| Fehlgeschlagener Login | `AUTH_FAIL: Fehlgeschlagener Login für Email=` | Brute-Force-Erkennung |
| Rate-Limit ausgelöst | `RATE_LIMIT: Login IP=` | Angriffserkennung |
| Rate-Limit global | `RATE_LIMIT: Global IP=` | DDoS-Erkennung |
| Erfolgreicher Login | `AUTH_OK: Benutzer=` | Audit-Trail |
| Buchung erstellt | `EVENT=BOOKING_CREATED` | Aktivitätsmonitoring |
| Buchung storniert | `EVENT=BOOKING_CANCELLED` | Aktivitätsmonitoring |

---

## Logs manuell lesen

```bash
# Aktuelle Logs (live)
docker logs -f eulo-backend

# Log-Datei direkt
tail -f ./logs/eulo.log

# Nur Fehler
grep -E "ERROR|WARN" ./logs/eulo.log

# Auth-Events
grep "AUTH_" ./logs/eulo.log

# Rate-Limit-Events
grep "RATE_LIMIT" ./logs/eulo.log
```
