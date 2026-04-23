export function normalizeMinorityGroup(group) {
  return String(group ?? "").trim().toLowerCase().replace(/\s+/g, "_");
}

export function defaultGetColor(percentage) {
  return percentage > 80 ? "#004529" :
    percentage > 70 ? "#006837" :
      percentage > 60 ? "#238443" :
        percentage > 50 ? "#41ab5d" :
          percentage > 40 ? "#78c679" :
            percentage > 30 ? "#addd8e" :
              percentage > 20 ? "#d9f0a3" :
                percentage > 10 ? "#f7fcb9" :
                  "#ffffe5";
}

export function getHeatmapColor(percentage, bins) {
  if (Array.isArray(bins) && bins.length > 0) {
    const match = bins.find((bin) => percentage >= bin.min && percentage < bin.max) ?? bins[bins.length - 1];
    return match?.color ?? defaultGetColor(percentage);
  }
  return defaultGetColor(percentage);
}

export function getFeaturePercentage(feature, precinctGroupShareByGeoid) {
  const properties = feature?.properties ?? {};
  const geoid = String(properties.GEOID ?? feature?.id ?? "").trim();
  const share = precinctGroupShareByGeoid?.[geoid];

  if (!geoid || !Number.isFinite(share) || share < 0) {
    return 0;
  }

  const percentage = Math.round(Number(share) * 100);
  return Math.max(0, Math.min(100, percentage));
}
