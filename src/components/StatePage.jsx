import React, { useEffect } from "react";
import '../../styles/state-page.css'
import { useNavigate, useParams } from "react-router-dom";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import statesData from "../../data/us-states.js";

export default function StatePage() {
  const { stateName } = useParams()

	useEffect(() => {
		const map = L.map("map", {
			center: stateName === 'Oregon' ? [44.1, -120.5] : [33.6, -80.9],
			zoomControl: false,
			zoom: stateName === 'Oregon' ? 6 : 6.7,
			zoomSnap: 0.1,
			minZoom: 5,
			maxZoom: 8,
			dragging: false,
			scrollWheelZoom: false,
			doubleClickZoom: false,
			keyboard: false,
		});

		L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
			attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
		}).addTo(map);

			// Something like this to color each district
			//////////////////////////////////////////////
			// function getColor(d) {
			// 	 return d > 1000 ? '#800026' :
			// 				d > 500  ? '#BD0026' :
			// 				d > 200  ? '#E31A1C' :
			// 				d > 100  ? '#FC4E2A' :
			// 				d > 50   ? '#FD8D3C' :
			// 				d > 20   ? '#FEB24C' :
			// 				d > 10   ? '#FED976' :
			// 										'#FFEDA0';
			// }

			// function style(feature) {
			// 	 return {
			// 			fillColor: getColor(feature.properties.density),
			// 			weight: 2,
			// 			opacity: 1,
			// 			color: 'white',
			// 			dashArray: '3',
			// 			fillOpacity: 0.7
			// 	 };
			// }

			// function highlightFeature(e) {
			// 	var layer = e.target;

			// 	layer.setStyle({
			// 			weight: 5,
			// 			color: '#666',
			// 			dashArray: '',
			// 			fillOpacity: 0.7
			// 	});

			// 	layer.bringToFront();
			// 	info.update(layer.feature.properties);
			// }

			// function resetHighlight(e) {
			// 	geojson.resetStyle(e.target);
			// 	info.update();
			// }

			// function onEachFeature(feature, layer) {
			// 	layer.on({
			// 		mouseover: highlightFeature,
			// 		mouseout: resetHighlight,
			// 	});
			// }

		const geojson = L.geoJson(statesData, {}).addTo(map);

		// Cleanup
    return () => {
      map.remove();
    };
  }, []);

  return (
    <>
      <div id="mapContainer">
        <div id="map"></div>
      </div>
    </>
  );
}