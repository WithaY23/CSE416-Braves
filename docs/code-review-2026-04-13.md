# Code Review Report — 2026-04-13

## Executive Summary

- **12 required GUI use cases**: 9 PASS, 0 PARTIAL, 3 MISSING
- **7 preferred GUI use cases**: 5 PASS, 1 PARTIAL, 1 MISSING
- **Top 3 most urgent issues:**
  1. **BLOCKER — GUI-20, GUI-21, GUI-22 have no frontend consumption.** Backend endpoints exist but no component calls `/analysis/vra-impact-thresholds`, `/analysis/minority-effectiveness/box-whisker`, or `/analysis/minority-effectiveness/histogram`. All three are **required**.
  2. **BLOCKER — GUI-8 (Compare) right map loads enacted plan instead of interesting plan** (`Compare.jsx:83` calls the same enacted topology endpoint as the left map).
  3. **Performance — GeometryAssetService has no caching** (`GeometryAssetService.java`). Every request re-reads and re-parses classpath JSON files (~25 MB total for precincts).

---

## GUI Use Case Coverage Matrix

| ID | Name | Priority | Status | Notes |
|----|------|----------|--------|-------|
| GUI-1 | Select state | Required | **PASS** | `GET /api/states` → `SplashPage.jsx` US map + click handler |
| GUI-2 | District map | Required | **PASS** | `GET .../districts/enacted/topology` → `DistrictMap.jsx` Leaflet render |
| GUI-3 | State summary | Required | **PASS** | `GET .../state-summary` → `StatePage.jsx` sidebar |
| GUI-4 | Heatmap | Required | **PASS** | `GET .../precincts/topology` + `/heatmap/precincts` → `MinorityHeatMap.jsx` with bin legend |
| GUI-6 | District table | Required | **PASS** | `GET .../districts/enacted/table` → `DistrictTable.jsx` (rendered in `StatePage.jsx`) |
| GUI-7 | Highlight district | Preferred | **PASS** | N/A — client-only, click handler in `DistrictTable.jsx`/`DistrictMap.jsx` |
| GUI-8 | Compare plans | Preferred | **PARTIAL** | Right map should load interesting plan but loads enacted plan instead (`Compare.jsx:83`) |
| GUI-9 | Gingles scatter | Required | **PASS** | `GET .../analysis/gingles` → `GinglesScatterChart.jsx` in `CrossStateAnalysis.jsx` |
| GUI-10 | Gingles table | Preferred | **PASS** | `GET .../analysis/gingles/table` → paginated table in `CrossStateAnalysis.jsx:51` |
| GUI-12 | EI support | Required | **PASS** | `GET .../analysis/ei-support` → `EiSupportChart.jsx` in `EI.jsx` |
| GUI-13 | EI bar + CI | Preferred | **PASS** | `GET .../analysis/ei-precinct-bar-ci` → `EiBarPanel` in `EI.jsx:52` |
| GUI-15 | EI KDE | Preferred | **PASS** | `GET .../analysis/ei-kde` → `EiKdePanel` in `EI.jsx:91` |
| GUI-16 | Ensemble splits | Required | **PASS** | `GET .../ensembles/splits` → `SingleEnsembleSplitsChart.jsx` in `VRAAnalysis.jsx` |
| GUI-17 | Box & whisker | Required | **PASS** | `GET .../ensembles/box-whisker` → `BoxWhiskerChart.jsx` in `StateSimulationMinorityData.jsx` and `StateCustomAnalysis.jsx` |
| GUI-19 | Interesting plan | Preferred | **MISSING** | Backend endpoint exists but no frontend component calls `GET .../districts/interesting`. `InterestingMap.jsx` exists but receives data as prop and nobody fetches it. |
| GUI-20 | VRA impact table | Required | **MISSING** | Backend endpoint `GET .../analysis/vra-impact-thresholds` exists. `VRAAnalysis.jsx` only fetches ensemble splits (GUI-16), not VRA thresholds. |
| GUI-21 | Effectiveness B&W | Required | **MISSING** | Backend endpoint `GET .../analysis/minority-effectiveness/box-whisker` exists. No frontend component calls it. `StateSimulationMinorityData.jsx` only uses GUI-17 box-whisker. |
| GUI-22 | Effectiveness histogram | Required | **MISSING** | Backend endpoint `GET .../analysis/minority-effectiveness/histogram` exists. No frontend component calls it. |
| GUI-24 | Reset page | Preferred | **PASS** | N/A — client-only, state cleared via navigation back to splash |

---

## Blockers (Required use cases not fully implemented)

### BLOCKER 1: GUI-20 — VRA Impact Threshold Table (Required)

**Requirement:** "Display a summary table comparing the race-blind and VRA-constrained ensembles across three legal threshold metrics."

**What's missing:** `VRAAnalysis.jsx` fetches `GET /api/states/{stateCode}/ensembles/splits` (GUI-16 data) and renders ensemble split bar charts. It does **not** call `GET /api/states/{stateCode}/analysis/vra-impact-thresholds` and does **not** render a 3-row comparison table. The backend endpoint and seed data are ready — only the frontend fetch + table render is missing.

**Fix:** Add an API call in `VRAAnalysis.jsx` to `/analysis/vra-impact-thresholds?group=...&election=2024_pres` and render a table with 3 metric rows showing `raceBlindShare` vs `vraConstrainedShare`.

### BLOCKER 2: GUI-21 — Minority Effectiveness Box & Whisker (Required)

**Requirement:** "Display box & whisker data comparing minority effectiveness across the Race-Blind and VRA-Constrained ensembles. For each feasible group, two side-by-side boxes."

**What's missing:** No frontend component calls `GET /api/states/{stateCode}/analysis/minority-effectiveness/box-whisker`. `StateSimulationMinorityData.jsx` uses the regular `ensembles/box-whisker` endpoint (GUI-17), not the minority-effectiveness-specific one that returns `groupSummaries[]` with `raceBlindSummary`/`vraConstrainedSummary` pairs.

**Fix:** Create a component (or extend existing) that fetches the minority-effectiveness box-whisker endpoint and renders side-by-side boxes per feasible group.

### BLOCKER 3: GUI-22 — Minority Effectiveness Histogram (Required)

**Requirement:** "Display overlapping histograms comparing the distribution of minority-effective districts across both ensembles."

**What's missing:** No frontend component calls `GET /api/states/{stateCode}/analysis/minority-effectiveness/histogram`. Backend endpoint and seed data exist.

**Fix:** Create a histogram component that fetches the endpoint and renders overlapping `raceBlind` vs `vraConstrained` frequency bars.

---

## Performance Findings

### CRITICAL

**P1: GeometryAssetService has no caching** — `GeometryAssetService.java`

Every request to `/districts/enacted/topology`, `/precincts/topology`, or `/maps/us-states/topology` reads from the classpath and parses JSON via ObjectMapper. Precinct topology files are ~11-14 MB each. Under concurrent load this will be slow and allocate excessive garbage.

**Fix:** Add a `ConcurrentHashMap<String, Map<String, Object>>` cache keyed by file path. Files are static — cache on first load, never evict.

### WARNING

**P2: MinorityHeatMap fires two sequential API calls** — `MinorityHeatMap.jsx:150-176`

The heatmap bin request and precinct topology request are launched as independent `async` IIFEs but not grouped in `Promise.all()`. The topology request is the bottleneck (~11-14 MB); starting both simultaneously would reduce perceived latency.

**Fix:** Use `Promise.all([binRequest, topologyRequest])`.

**P3: Dead geometry files in `src/data/` (30 MB)** — `src/data/`

Files `OR-precincts-with-results.json` (11 MB), `SC-precincts-with-results.json` (14 MB), `oregonCongressionalDistricts.js` (2.2 MB), `scCongressionalDistricts.js` (3.4 MB) exist in the frontend source tree but are **not imported anywhere**. They are dead weight in the repo. They don't bloat the Vite bundle (since they're not imported), but they inflate clone/checkout size.

**Fix:** Delete these files. Geometry is now served from the backend classpath.

**P4: `chartPayloads.js` is dead code** — `src/data/chartPayloads.js`

Imports 10 mock JSON files from `mock-data/v1/` but is never imported by any component.

**Fix:** Delete `chartPayloads.js`.

### INFO

**P5: HTTP compression is correctly configured** — `application.properties:18-20`

`server.compression.enabled=true` with appropriate MIME types and 1024-byte threshold. Good.

**P6: MongoDB indexes are properly configured** — `MongoIndexConfig.java`

Composite indexes match all repository query patterns. No missing index coverage detected.

---

## Error Handling Findings

### Backend Error Handling — GOOD

**Global exception handler** (`ApiExceptionHandler.java`) correctly maps:
- `MissingServletRequestParameterException` → 400
- `IllegalArgumentException` → 422
- `NoSuchElementException` → 404
- `UnsupportedOperationException` → 501
- Catch-all `Exception` → 500 (with generic message, no stack trace leakage)

**Input validation** is present: `@NotBlank` on path/query params, `requireFeasibleGroup()` checks, `normalizeState()` validates state codes, `normalizeParty()` validates DEM/REP.

### Frontend Error Handling — INCONSISTENT

| Severity | Component | Issue |
|----------|-----------|-------|
| WARNING | `VRAAnalysis.jsx` | No loading indicator — renders empty label div while fetching. On error, payload stays `null` and UI shows just the label with no error message. |
| WARNING | `StateSimulationMinorityData.jsx` | No loading indicator — box whisker charts disappear while re-fetching after minority change. Error silently sets `payloads` to `null`. |
| WARNING | `CrossStateAnalysis.jsx` | No loading/error UI — charts and table just don't appear if API fails. |
| WARNING | `SplashPage.jsx` | No error UI when `GET /api/maps/us-states/topology` fails — map just doesn't render. |
| INFO | `EI.jsx` | Excellent — all three panels show "Loading..." and "No data available for {minority}" states. Best example in the codebase. |
| INFO | `StatePage.jsx` | Good — proper loading/error/empty states for all four API calls. |
| WARNING | `MinorityHeatMap.jsx:178-182` | Cleanup calls `switchMinority('')` — this side-effect on unmount can cause unexpected state updates in parent components. |

---

## Test Coverage Findings

### Backend Tests — PARTIAL

| Area | Status | Details |
|------|--------|---------|
| Controller tests | PARTIAL | `StateControllerTest.java` has 5 tests covering: topology (OR), US states topology, precinct topology, ensemble summary happy path, ensemble summary 422 rejection. **Missing:** All other 15+ endpoints untested. |
| Service tests | UNTESTED | No tests for `BackendDataService.java`. |
| GeometryAssetService tests | COVERED | `GeometryAssetServiceTest.java` has 3 tests. |
| SeedDataLoader tests | UNTESTED | No tests for seed data loading, validation, or fallback logic. |
| Payload invariant tests | UNTESTED | No tests verify: ensemble splits sum to ensembleSize, box whisker ordering (min<=q1<=median<=q3<=max), EI CI bounds (ciLow<=peak<=ciHigh), VRA impact 3-row rule, histogram frequency sums. |

**Recommended additions:**
- Happy-path tests for each endpoint in `StateController`
- Payload invariant tests using seed data
- Service-level unit tests for `BackendDataService` normalization/validation logic

### Frontend Tests — UNTESTED

| Area | Status | Details |
|------|--------|---------|
| Component tests | UNTESTED | Only `src/test/setupTests.js` exists (Vitest config). No component test files found. |
| Chart rendering | UNTESTED | No tests for any Recharts or SVG components. |
| Edge cases | UNTESTED | No loading/error/empty state tests. |

**Recommended additions:** At minimum, smoke tests for `StatePage`, `EI`, `CrossStateAnalysis`, and each chart component with mock API responses.

---

## Code Quality Findings

### BUG

**Q1: `Compare.jsx:83` — Right map fetches wrong endpoint**

Both left and right `useEffect` blocks call `GET /api/states/${stateCode}/districts/enacted/topology`. The right map should call `GET /api/states/${stateCode}/districts/interesting?planId=...` to fetch an interesting plan. Without this, GUI-8 shows two identical maps.

### SMELL

**Q2: Duplicated `toStateCode()` function** — 6+ components

`toStateCode(stateName)` is copy-pasted in `Compare.jsx:11`, `EI.jsx:23`, `StateCustomAnalysis.jsx:22`, `MinorityHeatMap.jsx:138` (inline), `VRAAnalysis.jsx:13` (inline), `StateSimulationMinorityData.jsx:44` (inline). Should be extracted to a shared utility.

**Q3: `StateCustomAnalysis.jsx` — 15 useState hooks**

Three independent "slot" configurations each with 3 state variables (currentData, secondData, thirdData) results in 15 hooks. A `useReducer` or slot-object pattern would be cleaner and less error-prone.

**Q4: `displayData()` exported from two files**

`displayData()` is defined identically in both `StateMinorityAnalysis.jsx:8` and `StateCustomAnalysis.jsx:30`. Should be extracted to a shared utility.

**Q5: Population measure inconsistency in seed data** — `SeedDataLoader.java`

Some entities use `PopulationMeasure.TOTAL` (states, summaries, district tables, heatmaps, gingles, ensemble splits, interesting plans) while others use `PopulationMeasure.CVAP` (EI, box whiskers, VRA thresholds, minority effectiveness). The CLAUDE.md says "VAP preferred" but neither TOTAL nor CVAP is VAP. This may be intentional for different data types but should be documented.

### MINOR

**Q6: Commented-out code** — `StateCustomAnalysis.jsx:60-68` has a commented-out `PartyDot` component.

**Q7: Hardcoded map coordinates** — `MinorityHeatMap.jsx:198-205` has hardcoded center/zoom/bounds per state. Adding a third state would require code changes rather than data changes.

---

## Recommended Action Items

Ordered by urgency:

| # | Priority | Use Case | Action |
|---|----------|----------|--------|
| 1 | **BLOCKER** | GUI-20 | Wire `VRAAnalysis.jsx` to call `/analysis/vra-impact-thresholds` and render the 3-row comparison table |
| 2 | **BLOCKER** | GUI-21 | Create frontend component calling `/analysis/minority-effectiveness/box-whisker` and rendering side-by-side boxes per group |
| 3 | **BLOCKER** | GUI-22 | Create frontend component calling `/analysis/minority-effectiveness/histogram` and rendering overlapping histogram |
| 4 | **BLOCKER** | GUI-8 | Fix `Compare.jsx:83` to call `/districts/interesting?planId=...` for the right map panel |
| 5 | HIGH | GUI-19 | Wire a component to actually fetch `GET /districts/interesting` — currently `InterestingMap.jsx` exists but nobody fetches the data for it |
| 6 | HIGH | Performance | Add in-memory caching to `GeometryAssetService.java` |
| 7 | HIGH | Error handling | Add loading/error states to `VRAAnalysis.jsx`, `StateSimulationMinorityData.jsx`, `CrossStateAnalysis.jsx` |
| 8 | MEDIUM | Tests | Add controller tests for all 15+ untested endpoints |
| 9 | MEDIUM | Tests | Add payload invariant tests (splits sum, box ordering, CI bounds, histogram sums) |
| 10 | MEDIUM | Code quality | Extract shared `toStateCode()` to `src/utils/stateCode.js` |
| 11 | MEDIUM | Performance | Use `Promise.all()` in `MinorityHeatMap.jsx` for parallel API calls |
| 12 | LOW | Cleanup | Delete dead files: `src/data/chartPayloads.js`, `src/data/OR-precincts-with-results.json`, `src/data/SC-precincts-with-results.json`, `src/data/oregonCongressionalDistricts.js`, `src/data/scCongressionalDistricts.js` |
| 13 | LOW | Cleanup | Remove commented-out `PartyDot` in `StateCustomAnalysis.jsx:60-68` |
| 14 | LOW | Bug | Fix `MinorityHeatMap.jsx:180-181` cleanup calling `switchMinority('')` — side effect on unmount |
