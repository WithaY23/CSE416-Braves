# Professor Feedback Changes — 2026-04-27

Changes implemented based on professor feedback received during the code review session.

## 1. Party Label: "Democrat" → "Democratic"
- `SeedDataLoader.java`: District rows and state summary now use `"Democratic"` for party affiliation.
- `src/data/oregon.js`: `partyControl` updated to `"Democratic"`.

## 2. Comma Formatting for finalPlanCount
- `StatePage.jsx` (`EnsembleData` component): `finalPlanCount` now formatted via the existing `num()` utility (e.g., `5,000`).

## 3. Multiple Interesting Plans
- New endpoint: `GET /api/states/{stateId}/districts/interesting/list` returns all plans for a state.
- Two plans seeded per state: `plan-42` (existing) and `plan-43` (race-blind baseline).
- `Compare.jsx`: Replaced raw axios fetching with TanStack Query hooks; added plan selector dropdown above the right map.
- New query hooks: `useInterestingPlanList(stateCode)`, `useInterestingPlan(stateCode, planId)`.
- New query keys: `interestingPlanList`, `interestingPlan`.

## 4. Java Enums for Magic Strings
Three enums added to `server/.../model/enums/`:
- `PartyKey` — `DEM` / `REP`; replaces `Set<String> PARTY_KEYS` in `BackendDataService`; used via `fromString()` in `normalizeParty()`.
- `EnsembleType` — `RACE_BLIND` ("race_blind") / `VRA_CONSTRAINED` ("vra_constrained"); replaces string literals throughout `SeedDataLoader`.
- `EnsembleSize` — `TEST` ("test") / `FINAL` ("final"); replaces string literals in ensemble seeding.

## Deferred
- Ensemble class grouping (professor suggestion; scope unclear, not implemented).
