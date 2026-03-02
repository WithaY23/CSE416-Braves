import React from "react";
import '../../styles/VRA-Analysis.css'

function BarChart() {
    return (
        <div className="VRAAnalysisGraphs">Bar Chart</div>
    )
}

export default function VRAAnalysis() {

  return (
		<span id="VRAAnalysisMain">
			<div className="VRAAnalysisContainers">
                <h2>VRA Constrained Ensemble</h2>
                <BarChart />
			</div>
			<div className="VRAAnalysisContainers">
                <h2>Race Blind</h2>
                <BarChart />
			</div>
		</span>
  );
}