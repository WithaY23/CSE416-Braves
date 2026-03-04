import React, { useEffect, useRef } from "react";
import { useParams } from "react-router-dom";
import { MapContainer, TileLayer, GeoJSON, useMap } from "react-leaflet";
import * as topojson from "topojson-client"
import ORPrecincts from "../data/OR-precincts-with-results.json"
import SCPrecincts from "../data/SC-precincts-with-results.json"

function getColor(percentage) {
	return percentage > 80 ? '#004529' :
		percentage > 70 ? '#006837' :
			percentage > 60 ? '#238443' :
				percentage > 50 ? '#41ab5d' :
					percentage > 40 ? '#78c679' :
						percentage > 30 ? '#addd8e' :
							percentage > 20 ? '#d9f0a3' :
								percentage > 10 ? '#f7fcb9' :
									'#ffffe5';
}

function TopoJSON(props) {
	const layerRef = useRef(null)
	const { data } = props

	function style(feature) {
		return {
			fillColor: getColor(Math.random() * 100),
			weight: 2,
			opacity: 1,
			color: 'white',
			dashArray: '3',
			fillOpacity: 0.7
		};
	}

	function highlightFeature(e) {
		var layer = e.target;

		layer.setStyle({
			weight: 5,
			color: '#666',
			dashArray: '',
			fillOpacity: 0.7
		});

		layer.bringToFront();
		// info.update(layer.feature.properties);
	}

	function resetHighlight(e) {
		layerRef.current.resetStyle(e.target);
		// info.update();
	}

	function onEachFeature(feature, layer) {
		layer.on({
			mouseover: highlightFeature,
			mouseout: resetHighlight,
		});
	}

	function addData(layer, jsonData) {
		if (jsonData.type === "Topology") {
			for (let key in jsonData.objects) {
				let geojson = topojson.feature(jsonData, jsonData.objects[key])
				layer.addData(geojson)
			}
		} else {
			layer.addData(jsonData)
		}
	}

	useEffect(() => {
		const layer = layerRef.current
		layer.clearLayers()
		addData(layer, data)
	}, [data]);

	return <GeoJSON ref={layerRef} style={style} onEachFeature={onEachFeature} />
}

function Legend() {
	const map = useMap();

	useEffect(() => {
		const legend = L.control({ position: "bottomright" });

		legend.onAdd = function () {
			const div = L.DomUtil.create("div", "info legend");

			const grades = [0, 10, 20, 30, 40, 50, 60, 70, 80];

			div.innerHTML += "<h4>% Population</h4>";

			for (let i = 0; i < grades.length; i++) {
				div.innerHTML +=
					`<div style="display:flex; align-items:center; margin-bottom:4px;">
            <i style="
              background:${getColor(grades[i] + 0.01)};
              display:inline-block;
            "></i>
            ${grades[i]}${grades[i + 1] ? "&ndash;" + grades[i + 1] : "+"}
          </div>`;
			}

			return div;
		};

		legend.addTo(map);

		return () => {
			legend.remove();
		};
	}, [map]);

	return null;
}

export default function MinorityHeatMap(minority) {
	const { stateName } = useParams()
	const data = stateName === "Oregon" ? ORPrecincts : stateName.replaceAll(' ', '') === "SouthCarolina" ? SCPrecincts : null

	if (!data) {
		return <div style={{ fontWeight: "bolder", margin: "1rem" }}>Error: State not found</div>
	}

	return (
		<MapContainer center={stateName === 'Oregon' ? [44.1, -119.6] : [33.33, -80.5]}
			zoom={stateName === 'Oregon' ? 6.3 : 7.1}
			zoomSnap={0.1}
			minZoom={stateName === 'Oregon' ? 6.1 : 6.9}
			style={{ width: "100%", height: "50vh" }}
			zoomControl={false}
			doubleClickZoom={false}
			keyboard={false}
			maxBounds={stateName === 'Oregon' ? [[47, -125], [41, -114.4]] : [[35.6, -83.3], [31.5, -77.5]]}>
			<TileLayer
				attribution='&amp;copy <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
				url="https://{s}.tile.osm.org/{z}/{x}/{y}.png"
			/>
			<TopoJSON
				data={data}
			/>
			<Legend />
		</MapContainer >
	)
}