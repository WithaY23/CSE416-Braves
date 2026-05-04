# CSE 416 Braves — VRA Districting Impact Analysis

Full-stack web app comparing minority representation probability distributions under VRA vs. race-blind districting. States covered: **Oregon** (6 districts) and **South Carolina** (7 districts).

**Tech stack:** React 18 + Vite (frontend) · Spring Boot 3.3.4 (backend) · MongoDB (database)

---

## Quick Start

### Prerequisites

- Java 22
- MongoDB Community Server running on `127.0.0.1:27017`
- Node.js + npm

### 1. Start the backend

```bash
cd server
MONGODB_URI="mongodb://127.0.0.1:27017/cse416_braves" ./mvnw spring-boot:run
```

Verify it's up:

```bash
curl http://localhost:8080/health
curl http://localhost:8080/health/db
```

### 2. Start the frontend

In a separate terminal from the project root:

```bash
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173). The Vite dev server proxies `/api` and `/health` to the backend at port 8080.

### 3. Run tests

```bash
npm test                              # frontend (Vitest)
cd server && ./mvnw test              # backend (JUnit)
```

---

## Repository Layout

```
.
├── src/                    # React frontend
│   ├── components/         # Page-level and UI components
│   ├── charts/             # Recharts/SVG chart components
│   ├── queries/            # TanStack Query hooks (one per API endpoint)
│   ├── lib/                # queryClient + centralized query key factory
│   ├── utils/              # Formatting, TopoJSON conversion, state helpers
│   └── data/               # Static reference data (OR/SC district defs, TopoJSON sources)
├── server/                 # Spring Boot backend
│   └── src/main/java/.../
│       ├── api/            # REST controllers
│       ├── service/        # Business logic (BackendDataService, GeometryAssetService)
│       ├── repository/     # Spring Data MongoDB repositories
│       ├── model/          # MongoDB document models
│       ├── dto/            # API response DTOs
│       └── config/         # CORS, Caffeine cache, OpenAPI, index config
├── mock-data/v1/           # Seed JSON payloads loaded into MongoDB on startup
├── schemas/v1/             # JSON Schema definitions for payload validation
├── preprocessing/          # Jupyter notebooks + SeaWulf output artifacts
├── scripts/                # GeoJSON-to-TopoJSON converter and mock data generators
└── docs/                   # All project documentation (start here)
```

---

## Key Documentation

| File | What it explains |
|------|-----------------|
| `docs/codebase-file-index.md` | Every source file with a one-line description; quick use-case → file lookup table |
| `docs/use-case-requirements.md` | Full professor requirements for all 19 GUI, 10 Prepro, and 12 SeaWulf use cases |
| `docs/seawulf-prepro-payload-schemas.md` | API response contracts and MongoDB schemas — primary frontend ↔ backend reference |
| `docs/schema-field-guide.md` | MongoDB collection and field reference |
| `docs/mongo-schema-and-use-case-mapping.md` | Maps every API route to its MongoDB collection |
| `docs/caching-architecture.md` | TanStack Query + Caffeine + HTTP ETag two-tier caching design |
| `docs/topojson-implementation.md` | Why geometry is served as TopoJSON from the backend, not bundled in the frontend |
| `server/README.md` | Full backend setup: MongoDB config, build commands, endpoint verification, troubleshooting |
| `AGENTS.md` | Coding conventions, commit format, never-do list, payload invariants |

---

## Domain Concepts (quick reference)

- **VRA Section 2** — prohibits minority vote dilution; project compares proportionality benchmark vs. race-blind baseline.
- **Ensemble** — GerryChain ReCom: *race-blind* (~5,000 plans) vs. *VRA-constrained* (meets minimum minority-effective district count).
- **Minority effectiveness** — calibrated statewide score (`s_state`) ≥ 0.6 threshold.
- **EI** — PyEI estimates group-level voting; outputs support distributions, precinct CIs, KDE comparisons.
- **Gingles preconditions** — (1) compact minority, (2) cohesive minority vote, (3) white bloc voting; GUI-9 scatter plots.
- **Feasible group** — state population ≥ 200,000. OR: Latino, Asian, White. SC: Black, Latino, White.
- **Population measure** — VAP (voting-age population) preferred throughout.
- **Party of choice** — highest avg EI-estimated vote for `2024_pres`. Never "candidate of choice."

---

## Implemented Use Cases

**GUI (19):** 1, 2, 3, 4, 6, 7, 8, 9, 10, 12, 13, 15, 16, 17, 19, 20, 21, 22, 24  
**Not implementing:** 5, 11, 14, 18, 23

See `docs/use-case-requirements.md` for full descriptions of every use case.

---

## OpenAPI / Swagger

With the backend running, visit [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) to browse all REST endpoints interactively.
