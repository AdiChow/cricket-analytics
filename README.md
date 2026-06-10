# cricket-analytics

[![CI](https://github.com/AdiChow/cricket-analytics/actions/workflows/ci.yml/badge.svg)](https://github.com/AdiChow/cricket-analytics/actions/workflows/ci.yml)

## Continuous Integration

GitHub Actions runs `./mvnw verify` and builds the Docker image for every pull request and every push to `main`. The workflow validates the build only; it does not publish images or deploy the application.

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

## Known Limitation

`ballsFaced` currently counts wide deliveries because the database stores only total extras, not the Cricsheet extras type. A future schema update will persist `extras.wides` during import and update batting queries to exclude wides from `ballsFaced`.
