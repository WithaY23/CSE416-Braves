# Code Review Report

**Date:** 2026-05-14
**Branch:** main
**Reviewer:** Claude (automated via /code-review skill)

---

## Presentation Prep Summary

**Recommended Starting Use Case: GUI-9 (Gingles Scatter Plot)**

Rationale: GUI-9 has the most complete, defensible end-to-end real data pipeline in the project. Preprocessing output flows through MongoDB → API → React chart. Both OR and SC are supported, and a unit test exists for the chart component.

**Suggested Trace Path (GUI-9):**
1. `Gingles.jsx` → `useGingles(stateCode, group)` (`src/queries/stateQueries.js`)
2. Axios GET `/api/states/{stateId}/analysis/gingles?group=latino&election=2024_pres`
3. `StateController.java` → `dataService.getGingles(stateId, group, election)`
4. `BackendDataService.java` → `ginglesResultRepository.findByStateIdAndGroupKeyAndElectionId()`
5. MongoDB `gingles_results` collection (seeded from real preprocessing output)
6. `GinglesScatterChart.jsx` renders scatter points + regression curves

**2–3 Pivot Use Cases:**
- **GUI-16 (Ensemble Splits):** `Simulation.jsx` → `useEnsembleSplits` → `StateController.getEnsembleSplits` → `ensemble_splits` collection → `SingleEnsembleSplitsChart.jsx`. Also rendered in `VRAAnalysis.jsx`.
- **GUI-3 (State Summary):** `StatePage.jsx` → `useStateSummary` → `/api/states/{stateId}/state-summary` → `state_summaries` collection. Short trace; be ready to discuss the rough-proportionality gap.
- **GUI-22 (ME Histogram):** `Simulation.jsx` → `useMeHistogram` → `StateController.getMinorityEffectivenessHistogram`. Illustrates the GUI-21/22 pair-fetch and merge pattern.

**AI Tools to Mention:**
- Claude Code was used for: Spring Boot service layer structuring, TanStack Query key design (`queryKeys.js`), `SeedDataLoader` invariant validation logic, `GeometryAssetService` sanitize-and-hash pipeline, and CSS/component layout.
- Be prepared to explain: which parts the team wrote vs. AI-suggested, how AI suggestions were reviewed before committing, and how the invariant validations in `SeedDataLoader` demonstrate domain understanding.

---

## Executive Summary

| Category | PASS | PARTIAL | MISSING |
|----------|------|---------|---------|
| Required GUI (13 total) | 7 | 4 | 2 |
| Preferred GUI (9 in scope) | 2 | 2 | 5 |

**Mock vs. Real Data Summary:**
- **Real preprocessing output (fully migrated):** GUI-9, GUI-10
- **Hardcoded JS constants (never call an API):** GUI-26, GUI-8, GUI-19
- **Seeded synthetic fixtures (call real API, but data is not yet from SeaWulf/PyEI):** GUI-12, GUI-13, GUI-15, GUI-16, GUI-17, GUI-20, GUI-21, GUI-22
- **Partial mock (API fetched but ignored in rendering):** GUI-4

**Top 3 Most Urgent Issues:**
1. `Simulation.jsx` lines 447–459 — `MinorityEffectiveDistrictsBar` and `MajorityMinorityDistrictsBar` use a **hardcoded constant** (`minorityEffectiveDistsData`) instead of calling the API. GUI-26 is a required use case and is currently PARTIAL.
2. `Compare.jsx` lines 15–18, 109–122 — Entire Compare page uses **3 hardcoded local JS files** (`sc_test_compare_left.js`, `sc_test_packing.js`, `sc_test_cracking.js`). `useInterestingPlan` is commented out. GUI-8 and GUI-19 are blocked.
3. `stateUtils.js` line 21 — `groupOptionsForState` returns only `['Latino']` for OR and `['Black']` for SC, **silently blocking all other group data** from being displayed across every analysis page, even though the backend serves it.

---

## GUI Use Case Coverage Matrix

| ID | Name | Priority | Status | Data Source | Notes |
|----|------|----------|--------|-------------|-------|
| GUI-1 | Select state to display | Required | **PASS** | REAL | `/api/states` + `useUsStatesTopology`. Both OR/SC. |
| GUI-2 | Display enacted district map | Required | **PASS** | REAL | Classpath TopoJSON via `GeometryAssetService`. Both states. |
| GUI-3 | State data summary | Required | **PARTIAL** | REAL | Fetches from `/api/states/{stateId}/state-summary`. `groupRoughProportionality` field empty (`StatePage.jsx:112`) — Prepro-12 gap. |
| GUI-4 | Demographic heat map | Required | **PARTIAL** | PARTIAL | `useHeatmap` fetches real API bins, but `MinorityHeatMap.jsx:63` uses hardcoded `grades = [0,5,10,20,30,40,50]` for legend and coloring. API bins are fetched but ignored. |
| GUI-6 | Congressional representation table | Required | **PARTIAL** | REAL | Fetches from `/api/states/{stateId}/districts/enacted/table`. `effectivenessScore` and `calibratedEffectivenessScore` are placeholder values — Prepro-13/14 gap. |
| GUI-7 | Highlight district | Preferred | **PASS** | N/A | Click-to-highlight in district table working for both states. |
| GUI-8 | Compare two district plans | Preferred | **PARTIAL** | **MOCK** | Uses hardcoded `SCCurrentDistrictData`, `SCPackingData`, `SCCrackingData` local JS files. `useInterestingPlan` commented out at lines 109–122. Only works for SC. |
| GUI-9 | Gingles scatter plot | Required | **PASS** | **REAL** | Real preprocessing output for OR/SC. Regression curves rendered. Both states. |
| GUI-10 | Gingles precinct table | Preferred | **PASS** | **REAL** | Real preprocessing output. Paginated. Both states. |
| GUI-12 | EI support distribution | Required | **PASS** | MOCK-SEED | Synthetic fixtures in `mock-data/v1/ei-support/`. No curve overlap % (Prepro-15 gap). |
| GUI-13 | EI precinct bar + CI | Preferred | **PASS** | MOCK-SEED | Synthetic fixtures in `mock-data/v1/ei-precinct-bar-ci/`. |
| GUI-15 | EI KDE results | Preferred | **PASS** | MOCK-SEED | Synthetic fixtures. Both states. |
| GUI-16 | Ensemble splits bar chart | Required | **PASS** | MOCK-SEED | Synthetic splits. Real API call. Both states. |
| GUI-17 | Box & Whisker | Required | **PASS** | MOCK-SEED | Synthetic data. Both race_blind and vra_constrained. Both states. |
| GUI-19 | Display interesting district plan | Preferred | **PARTIAL** | **MOCK** | `InterestingMap` exists; `useInterestingPlan` hook commented out. Uses hardcoded SC test data. |
| GUI-20 | VRA impact threshold table | Required | **PASS** | MOCK-SEED | Synthetic threshold data. Both states. |
| GUI-21 | ME box & whisker | Required | **PASS** | MOCK-SEED | Synthetic data. Merges rb+vra responses. Both states. |
| GUI-22 | ME histogram | Required | **PASS** | MOCK-SEED | Synthetic data. Both states. |
| GUI-23 | Reset page | Preferred | **MISSING** | N/A | No reset button found in any component. |
| GUI-26 | ME & majority-minority bar charts | Required | **PARTIAL** | **MOCK** | `Simulation.jsx:447–459` uses hardcoded `minorityEffectiveDistsData` constant for both `MinorityEffectiveDistrictsBar` and `MajorityMinorityDistrictsBar`. No API call made. |

---

## Mock Data Migration Priority List

### Priority 1 — Required use cases on hardcoded data (fix immediately)

**GUI-26 — `Simulation.jsx:447–459`**
- Mock: `const minorityEffectiveDistsData = [{ label: "Latino", raceBlind: { min: 1, max: 3 }, vraConstrained: { min: 2, max: 4 } }]` — single hardcoded entry for both bar charts
- Real: `meBwData.groupSummaries` is already fetched at lines 422–423 via `useMeBoxWhiskerRb`/`useMeBoxWhiskerVra`. Each entry has `raceBlindSummary.min/max` and `vraConstrainedSummary.min/max`.
- Fix: Map `meBwData.groupSummaries` into `{ label, raceBlind: { min, max }, vraConstrained: { min, max } }` shape. For `MajorityMinorityDistrictsBar`, show a "data not yet available" placeholder until SeaWulf computes majority-minority counts.

**GUI-4 — `MinorityHeatMap.jsx:63–71`**
- Mock: `const grades = [0, 5, 10, 20, 30, 40, 50]` drives legend and `percentageColor()` coloring. API bins are fetched but commented out / ignored.
- Real: `heatmap.data.bins` from `useHeatmap()` contains `{ min, max, color }` per bin — already in scope.
- Fix: Uncomment and use `bins` from API for both `LegendControl` rendering and the `featurePercent` color logic. Remove `percentageColor` function and hardcoded `grades` array.

**GUI-8 / GUI-19 — `Compare.jsx:15–18, 109–122, 185–194, 258`**
- Mock: `SCCurrentDistrictData` (from `src/data/sc_test_compare_left.js`), `SCPackingData` (`sc_test_packing.js`), `SCCrackingData` (`sc_test_cracking.js`) imported at module top. `testPlanList` hardcoded at lines 185–194. Dropdown at line 258 renders `testPlanList` not `sortedPlanList`.
- Real: `GET /api/states/{stateId}/districts/interesting/list` and `GET /api/states/{stateId}/districts/interesting?planId=...` are live. `useInterestingPlanList` and `useInterestingPlan` hooks exist in `stateQueries.js`.
- Fix: Uncomment `useInterestingPlan` at lines 109–122. Replace `testPlanList` with `sortedPlanList` in dropdown. Wire left map to `enactedTopo` from `useDistrictTopology`; wire right map to `planData.topology`. Remove the 3 local JS file imports.

### Priority 2 — Seeded synthetic fixtures (replace when SeaWulf/PyEI output is available)

| Use Case | Mock Data Location | Replace When |
|----------|-------------------|--------------|
| GUI-12 | `mock-data/v1/ei-support/` | PyEI output available |
| GUI-13 | `mock-data/v1/ei-precinct-bar-ci/` | PyEI output available |
| GUI-15 | `mock-data/v1/ei-kde/` | PyEI output available |
| GUI-16 | `mock-data/v1/ensemble-splits/OR_compare.json`, `SC_compare.json` | SeaWulf output available |
| GUI-17 | `mock-data/v1/box-whisker/*.json` | SeaWulf-11 output available |
| GUI-20 | `mock-data/v1/vra-impact-thresholds/*.json` | SeaWulf-10 output available |
| GUI-21, 22 | `mock-data/v1/minority-effectiveness-*/` | SeaWulf-6/10/11 output available |

### Already migrated to real preprocessing output
- **GUI-9, GUI-10:** `preprocessing/output/OR_2024_latino_gingles_scatter.json`, `SC_2024_black_gingles_scatter.json` — REAL

---

## Blockers (Required Use Cases Not Fully Implemented)

**BLOCKER — GUI-26 — Minority-effective and majority-minority bar charts**
- Requirement: "Display bar charts for the range of minority effective districts and the range of majority-minority districts for each feasible minority group. Bar charts should be available for race-blind and VRA-constrained ensembles."
- Missing: `Simulation.jsx:447–459` uses hardcoded single-entry constant. No API call made. Charts render UI chrome but show fake data.
- Fix: Derive from `meBwData.groupSummaries` (already fetched). Majority-minority requires new SeaWulf data.

**BLOCKER — GUI-4 — Heatmap legend and coloring use hardcoded bins**
- Requirement: "The monochromatic heat map will show the percentage range of the selected group in each precinct. Choose a number of bins that effectively shows the population distribution with bin ranges that are equal."
- Missing: `MinorityHeatMap.jsx:63` legend is hardcoded; `percentageColor()` uses hardcoded thresholds instead of server-computed bins. API bin endpoint is fetched but ignored.
- Fix: Use `heatmap.data.bins` from API for both coloring and legend rendering.

**BLOCKER — GUI-3 — Rough proportionality missing**
- Requirement: "The summary table will also contain the measure of rough proportionality for each feasible minority group."
- Missing: `StatePage.jsx:112` renders `<p className="statePageData statePageDataNum"></p>` — empty, never populated.
- Fix: Complete Prepro-12, add `groupRoughProportionality` to seeded state summary, render in `EnsembleData`.

**PARTIAL BLOCKER — GUI-6 — Per-group effectiveness scores missing**
- Requirement: "Each row will for each feasible minority group also contain the calculated effectiveness score and the calibrated effectiveness score for the district."
- Missing: Single `effectivenessScore`/`calibratedEffectivenessScore` per district (not per group). Placeholder values only.
- Fix: Requires Prepro-13/14. Shape change needed in payload.

**PARTIAL BLOCKER — GUI-12 — Curve overlap % not displayed**
- Requirement: "Display the percentage overlap between the curves for each racial/language group in voting for a particular candidate."
- Missing: No overlap calculation or display in `EiAnalysisPanel`. `polarizedVotingPercentage` not in seeded EI documents (Prepro-15 not done).

---

## Performance Findings

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| WARNING | `SeedDataLoader.java:1252–1272` | Individual `save()` calls in nested loops (16 saves for ME box-whisker) | Use `saveAll()` for bulk inserts |
| WARNING | `Compare.jsx:15–18` | 3 large topology objects imported into bundle via local JS files | Remove imports once real API is wired |
| INFO | `SeedDataLoader.java:968–975` | O(n²) `.stream().noneMatch()` in sampling fallback pass | Acceptable at 500-point target; document with comment |
| INFO | `MinorityHeatMap.jsx:39` | `key={currMinority}` forces full layer remount on group change | Intentional Leaflet pattern; causes visible flash but is correct |

Note: `GeometryAssetService` correctly caches via `@Cacheable` + ETag (7-day max-age). `useMeBoxWhiskerRb`/`useMeBoxWhiskerVra` fire in parallel correctly.

---

## Error Handling Findings

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| WARNING | `Compare.jsx:40` | `onChangeTab` called in `InformationTable` click handler but never passed as prop → `TypeError` on district click | Add `onChangeTab` to `InformationTable` prop signature and pass from both call sites |
| WARNING | `Simulation.jsx:207–214` | `TooltipContent` accesses `.raceBlindSummary` without null check → crash if group summary is null | Add null guard before accessing `.min`, `.median`, etc. |
| INFO | `StateController.java:429` | `ensembleType` `@RequestParam` has no `@NotBlank` validation — empty string bypasses default | Add `@NotBlank` annotation |
| INFO | `ApiExceptionHandler.java:44` | Generic 500 handler suppresses exception from server logs entirely | Add `LOG.error("Unhandled exception", ex)` before building response |
| INFO | Frontend | TanStack Query default retry (3x) not disabled in `setupTests.js` — may cause slow/flaky tests | Add `queryClient.setDefaultOptions({ queries: { retry: false } })` in test setup |

---

## Test Coverage Findings

### Backend — PARTIAL

- **`StateControllerTest.java`:** 5 tests covering topology routes, state summary, ensemble summary, and a broad smoke-test of all endpoints with mocked service responses. Missing: error paths (missing group, invalid state), `getInterestingPlanList`, `listInterestingPlans`, `getMinorityEffectivenessBoxWhisker` with ensemble index variants.
- **`BackendDataService.java`:** No direct unit tests. `normalizeGroupSelector`, `requireFeasibleGroup`, `withStoredMetadata`, `normalizeParty` have no dedicated tests.
- **Payload invariant tests:** None found that assert `repWins + demWins = totalDistricts`, frequency sums, box-whisker ordering constraints, or VRA impact 3-row rule at the API response layer. Seeder validates at seed time only.
- **`GeometryAssetServiceTest.java`** and **`SeedDataLoaderTest.java`:** Present per file index.

### Frontend — PARTIAL

| Component | Tested |
|-----------|--------|
| `GinglesScatterChart.jsx` | ✓ (`GinglesScatterChart.test.jsx`) |
| `StatePage.jsx` | ✓ (`StatePage.test.jsx` — 2 tests) |
| `Simulation.jsx` | ✗ |
| `EI.jsx` | ✗ |
| `Compare.jsx` | ✗ |
| `MinorityHeatMap.jsx` | ✗ |
| `VRAAnalysis.jsx` | ✗ |
| `BoxWhiskerChart.jsx` | ✗ |
| `EiSupportChart.jsx` | ✗ |
| `SingleEnsembleSplitsChart.jsx` | ✗ |

**Tests to add:** `MinorityEffectivenessBoxWhisker` null guard; `EnsembleSplits` label merging when one series has a split absent from the other; `Compare` `onChangeTab` prop error path; backend test for `listInterestingPlans`.

---

## Code Quality & Coding Conventions Findings

### A. Modularity & Screen Readability

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| SMELL | `Simulation.jsx` (513 lines) | 15+ distinct components/functions in one file. `MinorityEffectivenessBoxWhisker`, `EnsembleSplits`, `VRAImpact`, etc. all defined here. | Split into per-chart files under `src/components/simulation/`. One component per file. |
| SMELL | `SeedDataLoader.java` (1,348 lines) | `samplePrecinctsForPlotting` (75 lines), `buildLockedGinglesChartPayload` (59 lines), `buildGinglesChartInternal` (64 lines) all exceed one screen. | Extract into `GinglesPayloadBuilder`, `GinglesValidator`, `EiPayloadSeeder`, etc. |
| SMELL | `Compare.jsx` (291 lines) | `CONGRESSIONAL_DATA`, `TEST_PACKING_DATA`, `TEST_CRACKING_DATA`, `testPlanList` defined inside component function body — re-created every render. | Move to module scope, or remove once real API is wired. |
| SMELL | `BackendDataService.java` | Constructor with 16 repository parameters (lines 43–77). | Group related repositories into a sub-record, or accept the length as acceptable Spring convention. |

### B. Data Structures

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| SMELL | `Gingles.jsx:95` | Fallback `gingles.data?.points` used as table rows — scatter points have `demVoteShare`/`repVoteShare`, not `republicanVotes`/`democraticVotes`. Table will render undefined. | Change to `const rows = ginglesTable.data?.rows ?? [];` |
| INFO | `GroupThresholds.java` | `Map.of()` is unordered; groups are only looked up by key so no bug, but `LinkedHashMap` would be more explicit. | Minor — acceptable. |

### C. Naming Conventions

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| SMELL | `Simulation.jsx:235, 389` | `"Vra-Constrained Ensemble"` in Recharts legend `name` prop — "Vra" should be "VRA" | Change to `"VRA-Constrained Ensemble"` |
| MINOR | `Compare.jsx` | `CONGRESSIONAL_DATA` declared with `const` inside function body but uses ALL_CAPS (implies module-level constant) | Move to module scope or rename to camelCase |

### D. Comments

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| SMELL | `Simulation.jsx:125`, `App.jsx:112,114,127,134,141` | Commented-out JSX blocks — dead code | Remove before review session |
| SMELL | `Compare.jsx:94–122` | Entire `useEffect` + `useInterestingPlan` block commented out | Uncomment (it's the real implementation) or remove |
| MINOR | `MinorityHeatMap.jsx:66–71` | Real bins-driven legend commented out adjacent to hardcoded version | Implement (uncomment and fix) or remove entirely |

### E. Magic Numbers

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| SMELL | `Simulation.jsx:348, 380` | `(group === "Latino" ? 6 : 7)` hardcodes district counts | Use `payload?.totalDistricts` from API data |
| SMELL | `MinorityHeatMap.jsx:63` | `[0, 5, 10, 20, 30, 40, 50]` hardcoded bin thresholds | Use `heatmap.data.bins` from API |
| SMELL | `SeedDataLoader.java:169–170` | Raw census population literals `4_272_371`, `5_478_831` as local variables | Promote to named class-level constants |
| SMELL | `SeedDataLoader.java:172–173` | Min/max population bound literals in validation method | Name as constants with explanatory comment |
| INFO | `SeedDataLoader.java:149` | `count() < 16` — `16` is magic | Define `EXPECTED_ME_BOX_WHISKER_COUNT = 16` with comment: 2 states × 2 types × 4 indices |

### F. Java-Specific Formatting

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| MINOR | `SeedDataLoader.java:140–142` | Multiple statements on one line (e.g. `eiSupportResultRepository.deleteAll(); seedEiSupport(root);`) | Split each statement to its own line |

Note: `StateController.java` and `BackendDataService.java` annotation formatting, brace placement, and method spacing are all correct and consistent.

### G. Intra-Language Consistency

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| SMELL | `stateUtils.js:21` | OR returns `['Latino']` only; SC returns `['Black']` only — but backend serves asian/white/latino for OR and black/latino/white for SC | Fix to `['Latino','Asian','White']` for OR and `['Black','Latino','White']` for SC. Single change unlocks multi-group selection across all analysis pages. |
| SMELL | `Compare.jsx` | Mixes TanStack Query hooks (left map via `useDistrictTopology`) with manual `useEffect`+`useState` (right map at lines 196–207) | Migrate right map to `useInterestingPlan` hook pattern |
| SMELL | `App.jsx:52–60` | `minorityData` static array defined inside component — re-created every render | Move to module scope or derive from API |
| SMELL | `Gingles.jsx:95` | `isPolarized` hardcoded: `stateCode === "SC" ? true : false` — always true for SC, always false for OR | Should come from backend data (Prepro-15). Use `null` or omit until computed. |

### H. Configuration & Test Data

| Severity | Location | Issue | Suggested Fix |
|----------|----------|-------|---------------|
| SMELL | `SeedDataLoader.java` | OR/SC census population values hardcoded in Java, not in `application.properties` | Move to property files or seed JSON |
| INFO | `MongoIndexConfig.java` | `MinorityEffectivenessBoxWhiskerDocument` index does not include `ensembleType` + `ensembleIndex` — 4-field compound lookup may be slow at scale | Add `ensembleType` and `ensembleIndex` to the compound index |
| GOOD | `SeedDataLoader.java` | `APP_SEED_ENABLED` and `APP_SEED_ROOT_PATH` are configurable via env vars | No change needed |
| GOOD | `MongoIndexConfig.java` | All primary lookup fields have corresponding index definitions | No change needed |

---

## Recommended Action Items

**Blockers — fix before review:**

1. **[GUI-26]** `Simulation.jsx:447–459` — Remove hardcoded `minorityEffectiveDistsData`. Derive `MinorityEffectiveDistrictsBar` data from `meBwData.groupSummaries` (already fetched). Show placeholder for `MajorityMinorityDistrictsBar`.
2. **[GUI-4]** `MinorityHeatMap.jsx:63–71` — Use `heatmap.data.bins` for both legend and coloring. Remove hardcoded `grades` array and `percentageColor` function.
3. **[GUI-8/19]** `Compare.jsx` — Uncomment `useInterestingPlan` (lines 109–122), replace `testPlanList` with `sortedPlanList` in dropdown, wire maps to real API, remove 3 local JS imports.
4. **[GUI-3]** `StatePage.jsx:112` — Add rough proportionality display (requires Prepro-12 completion).

**High priority — fix before final submission:**

5. **[All use cases]** `stateUtils.js:21` — Fix `groupOptionsForState` to return full group lists for OR (`['Latino','Asian','White']`) and SC (`['Black','Latino','White']`). Single biggest unlock across all analysis pages.
6. **[GUI-26]** `Simulation.jsx:348, 380` — Replace `(group === "Latino" ? 6 : 7)` with `payload?.totalDistricts`.
7. **[Compare.jsx:40]** Fix `onChangeTab is not a function` crash — pass prop to `InformationTable` from both call sites.
8. **[GUI-21]** `Simulation.jsx:207–214` — Add null guard in `TooltipContent` for `raceBlindSummary`/`vraConstrainedSummary`.
9. **[Gingles.jsx:95]** Fix fallback logic: `gingles.data?.points` → `[]` for table rows.
10. **[Gingles.jsx:95]** Remove hardcoded `isPolarized` boolean — show "polarization result pending" indicator.

**Code quality:**

11. **[Simulation.jsx]** Split into per-chart component files under `src/components/simulation/`.
12. **[Simulation.jsx:235, 389]** Fix `"Vra-Constrained"` → `"VRA-Constrained"` in Recharts legend names.
13. **[SeedDataLoader.java:140–142]** Split compound statements to separate lines.
14. **[MongoIndexConfig.java]** Add `ensembleType` + `ensembleIndex` to ME box-whisker compound index.
15. **[ApiExceptionHandler.java]** Add `LOG.error("Unhandled exception", ex)` before returning 500.
16. **[Tests]** Add: `MinorityEffectivenessBoxWhisker` null guard test, `EnsembleSplits` label merging test, `Compare` prop error test, backend test for `listInterestingPlans`.
