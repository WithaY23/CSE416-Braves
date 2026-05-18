import React from "react";
import { describe, expect, it, beforeEach, afterEach, vi } from "vitest";
import { cleanup, render, screen, within } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import Compare from "./Compare.jsx";

const queryState = {
  districtTopology: { data: { type: "FeatureCollection", features: [] }, isLoading: false, isError: false },
  planList: { data: [], isLoading: false, isError: false },
  plan: { data: null, isLoading: false, isError: false },
};

vi.mock("../queries/stateQueries.js", () => ({
  useDistrictTopology: () => queryState.districtTopology,
  useInterestingPlanList: () => queryState.planList,
  useInterestingPlan: () => queryState.plan,
}));

vi.mock("./DistrictMap.jsx", () => ({ default: () => <div data-testid="district-map" /> }));
vi.mock("./MinorityHeatMap.jsx", () => ({ default: () => <div data-testid="minority-map" /> }));
vi.mock("./InterestingMap.jsx", () => ({ default: () => <div data-testid="interesting-map" /> }));

function renderCompare(path) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route
          path="/state/:stateName/Compare"
          element={<Compare currMap="District Map" currMinority="Latino" switchMinority={vi.fn()} />}
        />
      </Routes>
    </MemoryRouter>
  );
}

function rightComparisonTable() {
  const rightContainer = document.querySelector("#compare-page-right-container");
  if (!rightContainer) {
    throw new Error("Right compare container not found");
  }
  return within(rightContainer).getByRole("table");
}

describe("Compare effective district table", () => {
  beforeEach(() => {
    queryState.districtTopology = { data: { type: "FeatureCollection", features: [] }, isLoading: false, isError: false };
    queryState.planList = { data: [], isLoading: false, isError: false };
    queryState.plan = { data: null, isLoading: false, isError: false };
  });

  afterEach(() => {
    cleanup();
  });

  it("uses effectiveDistrictIds for Oregon plans", () => {
    queryState.planList = {
      data: [{ planId: "vra_cracking_districts_min_hispanic", planName: "Cracking districts min hispanic" }],
      isLoading: false,
      isError: false,
    };
    queryState.plan = {
      data: {
        planId: "vra_cracking_districts_min_hispanic",
        effectiveDistrictIds: [4],
        topology: {
          type: "FeatureCollection",
          features: [
            { type: "Feature", properties: { district_id: 3, hispanic: 100 } },
            { type: "Feature", properties: { district_id: 4, hispanic: 100 } },
          ],
        },
      },
      isLoading: false,
      isError: false,
    };

    renderCompare("/state/Oregon/Compare");

    const rightTable = rightComparisonTable();
    expect(within(rightTable).getByText("3")).toBeInTheDocument();
    expect(within(rightTable).getByText("4")).toBeInTheDocument();
    expect(within(rightTable).getByText("Yes")).toBeInTheDocument();
    expect(within(rightTable).getByText("No")).toBeInTheDocument();
  });

  it("renders all No when a South Carolina plan has no effective districts", () => {
    queryState.planList = {
      data: [{ planId: "vra_electoral_packing_max", planName: "Electoral packing max" }],
      isLoading: false,
      isError: false,
    };
    queryState.plan = {
      data: {
        planId: "vra_electoral_packing_max",
        effectiveDistrictIds: [],
        topology: {
          type: "FeatureCollection",
          features: [
            { type: "Feature", properties: { district_id: 1, black: 200 } },
            { type: "Feature", properties: { district_id: 6, black: 400 } },
          ],
        },
      },
      isLoading: false,
      isError: false,
    };

    renderCompare("/state/SouthCarolina/Compare");

    const rightTable = rightComparisonTable();
    expect(within(rightTable).getByText("1")).toBeInTheDocument();
    expect(within(rightTable).getByText("6")).toBeInTheDocument();
    expect(within(rightTable).getAllByText("No")).toHaveLength(2);
  });

  it("aggregates minority population by district id before rendering", () => {
    queryState.planList = {
      data: [{ planId: "vra_demo", planName: "Demo" }],
      isLoading: false,
      isError: false,
    };
    queryState.plan = {
      data: {
        planId: "vra_demo",
        effectiveDistrictIds: [2],
        topology: {
          type: "FeatureCollection",
          features: [
            { type: "Feature", properties: { district_id: 2, black: 100 } },
            { type: "Feature", properties: { district_id: 2, black: 530 } },
            { type: "Feature", properties: { district_id: 1, black: 50 } },
          ],
        },
      },
      isLoading: false,
      isError: false,
    };

    renderCompare("/state/SouthCarolina/Compare");

    const rightTable = rightComparisonTable();
    expect(within(rightTable).getByText("630")).toBeInTheDocument();
    expect(within(rightTable).getByText("50")).toBeInTheDocument();
    expect(within(rightTable).getByText("Yes")).toBeInTheDocument();
    expect(within(rightTable).getByText("No")).toBeInTheDocument();
  });
});
