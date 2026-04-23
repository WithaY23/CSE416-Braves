import { describe, expect, it } from "vitest";
import {
  defaultGetColor,
  getFeaturePercentage,
  getHeatmapColor,
  normalizeMinorityGroup,
} from "./minorityHeatMap.js";

describe("normalizeMinorityGroup", () => {
  it("normalizes spacing and case for API keys", () => {
    expect(normalizeMinorityGroup("  South Asian  ")).toBe("south_asian");
    expect(normalizeMinorityGroup("Latino")).toBe("latino");
  });
});

describe("getFeaturePercentage", () => {
  it("looks up Oregon Latino heatmap shares by precinct GEOID", () => {
    const feature = {
      properties: { GEOID: "41051001" },
    };

    expect(getFeaturePercentage(feature, { "41051001": 0.25 })).toBe(25);
  });

  it("rounds share percentages for Oregon Asian heatmaps", () => {
    const feature = {
      properties: { GEOID: "41051002" },
    };

    expect(getFeaturePercentage(feature, { "41051002": 0.246 })).toBe(25);
  });

  it("uses South Carolina Black shares by precinct GEOID", () => {
    const feature = {
      properties: { GEOID: "45001001" },
    };

    expect(getFeaturePercentage(feature, { "45001001": 0.4 })).toBe(40);
  });

  it("returns zero when the GEOID is missing", () => {
    expect(getFeaturePercentage({ properties: {} }, { "41051001": 0.3 })).toBe(0);
  });

  it("returns zero when the share map does not contain the precinct", () => {
    expect(getFeaturePercentage({ properties: { GEOID: "41051001" } }, {})).toBe(0);
  });
});

describe("getHeatmapColor", () => {
  it("uses configured bins before falling back to the default palette", () => {
    const bins = [
      { min: 0, max: 20, color: "#111111" },
      { min: 20, max: 40, color: "#222222" },
      { min: 40, max: 100, color: "#333333" },
    ];

    expect(getHeatmapColor(25, bins)).toBe("#222222");
    expect(getHeatmapColor(99, bins)).toBe("#333333");
    expect(getHeatmapColor(25, [])).toBe(defaultGetColor(25));
  });
});
