# cricket-analytics

[![CI](https://github.com/AdiChow/cricket-analytics/actions/workflows/ci.yml/badge.svg)](https://github.com/AdiChow/cricket-analytics/actions/workflows/ci.yml)

## Live Demo

Frontend:
https://your-vercel-url.vercel.app

Backend API:
https://cricket-analytics-68188355784.asia-south1.run.app

Swagger:
https://cricket-analytics-68188355784.asia-south1.run.app/swagger-ui.html

---
## Screenshots

### Home

![Home](docs/screenshots/home.png)

### Search Players

![Search](docs/screenshots/search.png)

### Player Profile

![Player Profile](docs/screenshots/profile.png)

### Batting Leaderboard

![Leaderboard](docs/screenshots/leaderboard.png)

## Overview

Cricket Intelligence Platform is a backend-first cricket analytics application built using Spring Boot and PostgreSQL.

The platform currently contains:

- 903 Test matches
- 1,484 players
- 1.74M deliveries

Key capabilities:

- Player Search
- Player Profile
- Batting Analytics
- Leaderboards
- Cloud Deployment
- Swagger API Documentation

Tech Stack:

- Spring Boot
- PostgreSQL
- React
- Docker
- GitHub Actions
- Cloud Run
- Cloud SQL

---

## Continuous Integration

GitHub Actions runs `./mvnw verify` and builds the Docker image for every pull request and every push to `main`. The workflow validates the build only; it does not publish images or deploy the application.

## Frontend

The React, Vite, TypeScript, and Tailwind frontend is located in `frontend/` and connects to the deployed Cloud Run API through `VITE_API_BASE_URL`.

```bash
cd frontend
npm install
npm run dev
```

The local site is available at [http://localhost:5173](http://localhost:5173). To use a different backend, update `frontend/.env` or provide `VITE_API_BASE_URL` when starting or building the frontend.

The frontend includes player search, player profiles, leaderboards, and a comparison page at `/compare`. Select two players there to load their profile statistics side by side from the live comparison API.

The backend applies CORS to `/api/**` and always allows `http://localhost:5173` for local development. Set `CORS_ALLOWED_ORIGINS` to the deployed frontend origin before deploying the backend, for example `https://cricket.example.com`. Multiple deployed origins can be supplied as a comma-separated list. Wildcard origins are rejected.

## Cloud Run Readiness

The application honors Cloud Run's `PORT` environment variable and accepts database configuration through `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. For a production deployment, set `JPA_DDL_AUTO=validate`, `JPA_SHOW_SQL=false`, and an appropriate `DB_MAX_POOL_SIZE`; Cloud SQL connectivity is not configured yet.

## Docker Compose

Start PostgreSQL and the application from the repository root:

```bash
docker compose up --build
```

The application is available at [http://localhost:8080](http://localhost:8080), and its health endpoint is available at [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

Database settings can be overridden with `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, and `POSTGRES_PORT`. The application port can be overridden with `APP_PORT`. Cricsheet import remains disabled in Docker Compose.

Stop the services with `docker compose down`. The PostgreSQL data volume is retained unless `docker compose down -v` is used.

## API Documentation

With the application running, Swagger UI is available at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html). The generated OpenAPI document is available at [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs).

Compare two player profiles with:

```text
GET /api/stats/players/compare?player1Id=53&player2Id=49
```

## Known Limitation

`ballsFaced` currently counts wide deliveries because the database stores only total extras, not the Cricsheet extras type. A future schema update will persist `extras.wides` during import and update batting queries to exclude wides from `ballsFaced`.
