import React from 'react'
import "../../styles/sidebar.css";
import { useNavigate, useParams } from 'react-router-dom';
import menuIcon from '/menu.svg';

// Props: currPage, switchPage, currMap, switchMap, precinctMapSelectable, switchMinority, (maybe add showMaps boolean)
export function SideBar(props)
{
  const {currPage, switchPage, currMap, switchMap, precinctMapSelectable, currMinority, switchMinority } = props;
  const navigate = useNavigate();
  const { stateName } = useParams();
  const OregonGroups = ["Asian", "Hispanics"];
  const SCGroups = ["Black"];
  const minorityOptions = stateName === "Oregon" ?
    OregonGroups.map((minority) =>
    <div
      key={minority}
      value={minority}
      className={currMinority === minority ? "minority-option activeTab" : "minority-option"}
      onClick={() => {switchMap('Precinct Map'); switchMinority(minority)}}>
      - {minority}
    </div>)
  : SCGroups.map((minority) =>
    <div
      key={minority}
      value={minority}
      className={currMinority === minority ? "minority-option activeTab" : "minority-option"}
      onClick={() => {switchMap('Precinct Map'); switchMinority(minority)}}>
      - {minority}
    </div>);

  return (
    <span className="sidebar-wrapper">
      <img id="sidebar-icon" src={menuIcon} width="24px" />
      <nav className="sidebar">
        <span className="sidebar-maps-container">
          <div className="sidebar-header">Maps</div>
          <div className={currMap === "District Map" ? "sidebar-tab activeTab" : "sidebar-tab"} onClick={() => switchMap('District Map')}>District Map</div>
          {precinctMapSelectable ?
            <>
              <div className={currMap === "Precinct Map" ? "sidebar-tab activeTab" : "sidebar-tab"} onClick={() => switchMap('Precinct Map')}>Precinct Heat Map</div>
              {minorityOptions}
            </>
          : null}
        </span>
        <span className="sidebar-analysis-container">
          <div className="sidebar-header">Analysis</div>
          <div className={currPage === "State" ? "sidebar-tab activeTab" : "sidebar-tab"} onClick={() => { switchPage('State'); navigate(`/state/${stateName}`)}}>State Data Summary</div>
          <div className={currPage === "Compare" ? "sidebar-tab activeTab" : "sidebar-tab"} onClick={() => { switchMap('District Map'); switchPage('Compare'); navigate(`/state/${stateName}/Compare`)}}>Compare District Plans</div>
          <div className={currPage === "Gingles" ? "sidebar-tab activeTab" : "sidebar-tab"} onClick={() => { switchPage('Gingles'); navigate(`/Cross State Analysis`)}}>Gingles Charts</div>
          <div className={currPage === "Voting Rights Analysis" ? "sidebar-tab activeTab" : "sidebar-tab"} onClick={() => { switchPage('Voting Rights Analysis'); navigate(`/state/${stateName}/Voting Rights Analysis`)}}>Voting Rights Analysis</div>
          <div className={currPage === "Minority" ? "sidebar-tab activeTab" : "sidebar-tab"} onClick={() => { switchPage('Minority'); navigate(`/state/${stateName}/Minority Analysis`)}}>Minority Analysis</div>
          <div className={currPage === "Custom" ? "sidebar-tab activeTab" : "sidebar-tab"} onClick={() => { switchPage('Custom'); navigate(`/state/${stateName}/Custom State Analysis`)}}>Custom Analysis</div>
          <div className={currPage === "Simulation" ? "sidebar-tab activeTab" : "sidebar-tab"} onClick={() => { switchPage('Simulation'); navigate(`/state/${stateName}/Simulation Minority Data`)}}>Simulation Data</div>
        </span>
      </nav>
    </span>)
;}
