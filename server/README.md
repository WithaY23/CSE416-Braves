# Braves Server (Backend)

Spring Boot + Jakarta-style REST API for required CSE 416 use cases.

## Run

1. Start MongoDB locally (default URI `mongodb://localhost:27017/cse416_braves`) or set `MONGODB_URI`.
2. From `server/`, use Maven Wrapper:
   - `./mvnw -DskipTests compile`
   - `./mvnw test`

Environment variables:
- `MONGODB_URI`: Mongo connection string
- `APP_SEED_ENABLED`: `true|false` (default `true`)
- `APP_SEED_ROOT_PATH`: optional absolute path to repository root

## Endpoints (Base `/api`)

- Implemented:
  - `GET /states`
  - `GET /states/{stateId}/districts/enacted/geojson` (GUI-2)
- Skeleton (`501 Not Implemented`, `{schemaVersion,status,message,route}`):
  - `GET /states/{stateId}/summary`
  - `GET /states/{stateId}/heatmap/precincts?group={group}`
  - `GET /states/{stateId}/districts/enacted/table?election=2024_pres`
  - `GET /states/{stateId}/analysis/gingles?group={group}&election=2024_pres`
  - `GET /states/{stateId}/analysis/ei-support?groups={g1,g2...}&election=2024_pres&party={DEM|REP}`
  - `GET /states/{stateId}/ensembles/splits?ensembleSize={test|final}&election=2024_pres`
  - `GET /states/{stateId}/ensembles/box-whisker?group={group}&ensembleType={vra_constrained|race_blind}&metric={minority_share}`

`/api/v1/*` is no longer used.

Support:
- `GET /health`
- OpenAPI docs: `/swagger-ui.html`

## Frontend mapping from existing mock loaders

- `getGinglesPayload(state)` -> `GET /states/{state}/analysis/gingles?group=...&election=2024_pres`
- `getEiSupportPayload(state)` -> `GET /states/{state}/analysis/ei-support?groups=...&election=2024_pres&party=DEM`
- `getEnsembleSplitsPayload(state)` -> `GET /states/{state}/ensembles/splits?ensembleSize=final&election=2024_pres`
- `getBoxWhiskerPayloads(state)` ->
  - `GET /states/{state}/ensembles/box-whisker?group=...&ensembleType=vra_constrained&metric=minority_share`
  - `GET /states/{state}/ensembles/box-whisker?group=...&ensembleType=race_blind&metric=minority_share`

## Notes

- Backend enforces demographic threshold of `200,000`.
- Precinct realism gate (`>=1000` for OR/SC) is validated during seed loading.
- Population measure defaults to `TOTAL`, CVAP-specific data responses return `populationMeasureUsed=CVAP`.
