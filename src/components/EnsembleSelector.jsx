import React, { useState } from "react";
import "../../styles/ensemble-selector.css";
import arrowDropdown from "/white_arrow_drop_down.svg";

function labelFor(ensembleType, index) {
  return ensembleType === "rb" ? `Race-Blind Ensemble ${index}` : `VRA Ensemble ${index}`;
}

export default function EnsembleSelector({ ensembleType, currEnsemble, switchEnsemble, maxOptions = 4 }) {
  const [showList, setShowList] = useState(false);
  const ensembleList = Array.from(
    { length: Math.max(1, maxOptions) },
    (_, idx) => ({ ensembleId: idx + 1, ensembleName: labelFor(ensembleType, idx + 1) })
  );

  function toggleList() {
    setShowList(!showList);
  }

  return (
    <div className="ensemble-selector-container">
      <span className="ensemble-selector-selected" onClick={() => toggleList()}>
        {labelFor(ensembleType, currEnsemble)}
        <img id="dropdown-icon" src={arrowDropdown} width="20px"/>
      </span>
      {showList && (
      <div className="ensemble-selector-dropdown-container">
        {ensembleList.map((ensemble) => (
          <span key={ensemble.ensembleId} className={currEnsemble === ensemble.ensembleId ? "ensemble-selector-selected" : "ensemble-selector-option"}
          onClick={() => {switchEnsemble(ensemble.ensembleId); toggleList();}}>
            {ensemble.ensembleName}
          </span>
        ))}
      </div>
      )}
    </div>
  );
}
