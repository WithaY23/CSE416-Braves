import React, { useEffect, useRef, useState } from "react";
import "../../styles/minority-map.css";
import { useParams } from "react-router-dom";
import axios from "axios";
import L from "leaflet";
import { topologyToFeatureCollection } from "../utils/topology.js";
import { defaultGetColor, getFeaturePercentage, getHeatmapColor, normalizeMinorityGroup } from "../utils/minorityHeatMap.js";

function createLegendControl(bins) {
  const legend = L.control({ position: "bottomright" });

  legend.onAdd = function onAdd() {
    const div = L.DomUtil.create("div", "info legend");
    const fallbackGrades = [0, 10, 20, 30, 40, 50, 60, 70, 80];

    div.innerHTML += "<h4>% Population</h4>";

    if (Array.isArray(bins) && bins.length > 0) {
      bins.forEach((bin) => {
        const label = bin.max >= 100 ? `${bin.min}+` : `${bin.min}-${bin.max}`;
        div.innerHTML += `
          <div style="display:flex; align-items:center; margin-bottom:4px;">
            <i style="background:${bin.color}; display:inline-block;"></i>
            ${label}
          </div>`;
      });
      return div;
    }

    for (let index = 0; index < fallbackGrades.length; index += 1) {
      div.innerHTML += `
        <div style="display:flex; align-items:center; margin-bottom:4px;">
          <i style="background:${defaultGetColor(fallbackGrades[index] + 0.01)}; display:inline-block;"></i>
          ${fallbackGrades[index]}${fallbackGrades[index + 1] ? `&ndash;${fallbackGrades[index + 1]}` : "+"}
        </div>`;
    }

    return div;
  };

  return legend;
}

export default function MinorityHeatMap({ currMinority, switchMinority }) {
  const { stateName } = useParams();
  const mapContainerRef = useRef(null);
  const mapRef = useRef(null);

  const [bins, setBins] = useState([]);
  const [precinctGroupShareByGeoid, setPrecinctGroupShareByGeoid] = useState({});
  const [topologyData, setTopologyData] = useState(null);
  const [geometryLoadFailed, setGeometryLoadFailed] = useState(false);

  const OregonGroups = ["Latino", "Asian"];
  const SCGroups = ["Black", "Latino"];

  function shareLookup(payload) {
    const rows = payload?.precinctGroupShares;
    if (Array.isArray(rows)) {
      return Object.fromEntries(
        rows
          .filter((row) => row?.geoid != null && Number.isFinite(row?.share))
          .map((row) => [String(row.geoid), Number(row.share)])
      );
    }
    return payload?.precinctGroupShareByGeoid ?? {};
  }

  function styleFeature(feature) {
    return {
      fillColor: getHeatmapColor(getFeaturePercentage(feature, precinctGroupShareByGeoid), bins),
      weight: 2,
      opacity: 1,
      color: "white",
      dashArray: "3",
      fillOpacity: 0.7,
    };
  }

  function createMapInstance(target) {
    delete target._leaflet_id;
    target.innerHTML = "";

    const map = L.map(target, {
      zoomControl: false,
      doubleClickZoom: false,
      keyboard: false,
      zoomSnap: 0.1,
      minZoom: stateName === "Oregon" ? 6.1 : 6.9,
      maxBounds: stateName === "Oregon" ? [[47, -125], [41, -114.4]] : [[35.6, -83.3], [31.5, -77.5]],
    }).setView(stateName === "Oregon" ? [44.1, -119.6] : [33.33, -80.5], stateName === "Oregon" ? 6.3 : 7.1);

    L.tileLayer("https://{s}.tile.osm.org/{z}/{x}/{y}.png", {
      attribution: '&amp;copy <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    return map;
  }

  const minorityOptions = stateName === "Oregon"
    ? OregonGroups.map((minority) => (
      <option key={minority} value={minority}>
        {minority}
      </option>
    ))
    : SCGroups.map((minority) => (
      <option key={minority} value={minority}>
        {minority}
      </option>
    ));

  useEffect(() => {
    let isActive = true;
    const stateCode = stateName === "Oregon" ? "OR" : stateName === "South Carolina" ? "SC" : null;
    const group = normalizeMinorityGroup(currMinority);
    if (!stateCode || !group) {
      setBins([]);
      setPrecinctGroupShareByGeoid({});
      setTopologyData(null);
      setGeometryLoadFailed(true);
      return undefined;
    }

    setGeometryLoadFailed(false);

    (async () => {
      try {
        const response = await axios.get(`/api/states/${stateCode}/heatmap/precincts`, { params: { group } });
        if (isActive) {
          setBins(response.data?.bins ?? []);
          setPrecinctGroupShareByGeoid(shareLookup(response.data));
        }
      } catch {
        if (isActive) {
          setBins([]);
          setPrecinctGroupShareByGeoid({});
        }
      }
    })();

    (async () => {
      try {
        const response = await axios.get(`/api/states/${stateCode}/precincts/topology`);
        if (isActive) {
          setTopologyData(topologyToFeatureCollection(response.data));
          setGeometryLoadFailed(false);
        }
      } catch {
        if (isActive) {
          setTopologyData(null);
          setGeometryLoadFailed(true);
        }
      }
    })();

    return () => {
      isActive = false;
    };
  }, [currMinority, stateName]);

  useEffect(() => {
    const target = mapContainerRef.current;
    const hasShares = Object.keys(precinctGroupShareByGeoid).length > 0;
    if (!target || !topologyData?.features?.length || !hasShares) {
      return undefined;
    }

    mapRef.current?.remove();

    let cancelled = false;
    let map = null;
    const timer = window.setTimeout(() => {
      if (cancelled) {
        return;
      }

      map = createMapInstance(target);
      mapRef.current = map;

      const legend = createLegendControl(bins);
      legend.addTo(map);

      const layer = L.geoJSON(topologyData, {
        style: styleFeature,
        onEachFeature(_feature, featureLayer) {
          featureLayer.on({
            mouseover(event) {
              event.target.setStyle({
                weight: 2,
                color: "#666",
                dashArray: "",
                fillOpacity: 0.7,
              });
              event.target.bringToFront();
            },
            mouseout(event) {
              layer.resetStyle(event.target);
            },
          });
        },
      }).addTo(map);

      const bounds = layer.getBounds();
      if (bounds.isValid()) {
        map.fitBounds(bounds, {
          animate: false,
          padding: [12, 12],
        });
      }
    }, 1500);

    return () => {
      cancelled = true;
      window.clearTimeout(timer);
      map?.remove();
      if (mapRef.current === map) {
        mapRef.current = null;
      }
      delete target._leaflet_id;
      target.innerHTML = "";
    };
  }, [topologyData, bins, precinctGroupShareByGeoid, stateName]);

  if (!stateName) {
    return <div style={{ fontWeight: "bolder", margin: "1rem" }}>Error: State not found</div>;
  }

  if (!topologyData) {
    if (geometryLoadFailed) {
      return <div style={{ fontWeight: "bolder", margin: "1rem" }}>Unable to load precinct topology</div>;
    }
    return <div style={{ fontWeight: "bolder", margin: "1rem" }}>Loading precinct topology...</div>;
  }

  return (
    <>
      <div className="minority-selector-container">
        <label htmlFor="minoritySelector" style={{ fontWeight: "bolder" }}>Select a racial group: </label>
        <select name="minoritySelector" value={currMinority} onChange={(event) => { switchMinority(event.target.value); }}>
          {minorityOptions}
        </select>
      </div>
      <div id="minoritymap">
        <div ref={mapContainerRef} className="minorityLeafletMap" />
      </div>
    </>
  );
}
