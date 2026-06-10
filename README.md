# cricket-analytics

## Known Limitation

`ballsFaced` currently counts wide deliveries because the database stores only total extras, not the Cricsheet extras type. A future schema update will persist `extras.wides` during import and update batting queries to exclude wides from `ballsFaced`.
