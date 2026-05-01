# EULO – Deployment-Anleitung

## Voraussetzungen

- Docker Desktop (Windows/Mac) oder Docker Engine (Linux)
- Git (optional, alternativ: ZIP-Download)
- Schulnetz-Zugang (Techjam WLAN) für Server-Deployment

---

## Lokal deployen

### 1. Repository klonen / entpacken
```bash
git clone <repo-url>
cd EULO
```

### 2. Umgebungsvariablen setzen
```bash
cp .env.example .env
# .env bearbeiten: JWT_SECRET auf langen, zufälligen String setzen
```

### 3. Stack starten
```bash
docker compose up --build -d
```

### 4. Erreichbarkeit prüfen
```bash
# Backend Health
curl http://localhost:8080/actuator/health

# Frontend
# Browser: http://localhost:3000
```

### Demo-Login
- E-Mail: `alex.rivera@school.edu`
- Passwort: `password123`

---

## Server-Deployment (TechJam: AppServer 10.10.50.40)

### Architektur mit zwei Servern

```
DBServer  (10.10.50.30)  →  MongoDB
AppServer (10.10.50.40)  →  Backend + Frontend
```

### DBServer einrichten (10.10.50.30)

```bash
ssh g03admin@10.10.50.30
# Passwort: Sursee6210

su -
apt update && apt install -y docker.io
systemctl enable --now docker

# MongoDB als Container starten
docker run -d \
  --name eulo-mongo \
  --restart unless-stopped \
  -p 27017:27017 \
  -v mongo_data:/data/db \
  mongo:7
```

### AppServer einrichten (10.10.50.40)

```bash
ssh g03admin@10.10.50.40
su -
apt update && apt install -y docker.io docker-compose-plugin
systemctl enable --now docker
usermod -aG docker g03admin
exit
```

### Code auf AppServer kopieren

**Von lokalem PC (Windows):**
```powershell
scp -r C:\projects\EULO g03admin@10.10.50.40:/home/g03admin/eulo
```

### .env auf AppServer konfigurieren

```bash
cd /home/g03admin/eulo
cp .env.example .env
nano .env
```
```env
JWT_SECRET=EinSehrLangesZufälligesPasswort123!XYZ
MONGODB_URI=mongodb://10.10.50.30:27017/eulo
```

### Stack starten

```bash
docker compose up --build -d
docker compose ps     # Status prüfen
```

Die App ist erreichbar unter: **http://10.10.50.40:3000**

---

## Umgebungsvariablen

| Variable | Default (lokal) | Produktionswert |
|---|---|---|
| `JWT_SECRET` | `euloChangeThis...` | Langer, zufälliger String (min. 32 Zeichen) |
| `MONGODB_URI` | `mongodb://mongodb:27017/eulo` | `mongodb://10.10.50.30:27017/eulo` |

---

## Netzwerk-Voraussetzungen (Schule)

1. **Techjam WLAN** verbinden
2. **Statische IP** auf WLAN-Adapter setzen: `10.10.50.5 / 255.255.255.0`
3. SSH-Verbindung testen: `ping 10.10.50.40`
4. Für Internet auf Servern: USB-Tethering vom Handy + Internet-Sharing aktivieren

---

## Docker-Befehle Referenz

| Befehl | Beschreibung |
|---|---|
| `docker compose up -d` | Stack starten (Hintergrund) |
| `docker compose up --build -d` | Stack neu bauen und starten |
| `docker compose ps` | Container-Status anzeigen |
| `docker compose logs -f` | Live-Logs aller Container |
| `docker compose logs -f eulo-backend` | Nur Backend-Logs |
| `docker compose stop` | Graceful Stop (Container bleiben) |
| `docker compose down` | Stop + Container entfernen |
| `docker compose down -v` | ⚠️ Auch Volumes löschen (Datenverlust!) |
| `docker compose restart eulo-backend` | Einzelnen Service neustarten |

---

## Tests auf Server ausführen

```bash
# Erstes Mal (Internet nötig):
docker compose --profile test run --rm test

# Offline (nach erstem Mal):
docker compose --profile test run --rm test mvn test -o -B
```
