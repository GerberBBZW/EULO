Startup
# Produktiv starten:
cp .env.example .env
# JWT_SECRET in .env setzen
docker compose up --build -d

Demo-Login: alex.rivera@school.edu / password123


Run tests (first time, needs internet):

docker compose --profile test run --rm test
Run tests offline (after first run):

docker compose --profile test run --rm test mvn test -o -B