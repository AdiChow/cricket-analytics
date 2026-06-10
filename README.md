# cricket-analytics

## API Documentation

With the application running, Swagger UI is available at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html). The generated OpenAPI document is available at [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs).

## Known Limitation

`ballsFaced` currently counts wide deliveries because the database stores only total extras, not the Cricsheet extras type. A future schema update will persist `extras.wides` during import and update batting queries to exclude wides from `ballsFaced`.
