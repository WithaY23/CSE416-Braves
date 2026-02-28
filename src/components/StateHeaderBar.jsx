import React from "react";
import '../../styles/state-header.css'
import { useParams } from "react-router-dom";


/**
 * Props:
 * stateName(1, change) 
 * tabs: Voting Rights Analysis:str, Minority Analysis:str, Custom State Analysis:str (may change with use cases, but is likely to remain constant across page views (perhaps just store here))
 *  Path params:
 * {stateName:str}
 * */ 
export function StateHeaderBar(props)
{
  const stateName= useParams().stateName;
  let tabArr = []
  if(props.hasOwnProperty('tabs'))
  {
    // Make each tab formatted as to tabs
    for(let i = 0; i< props.tabs.length; i++)
    {
      const tabName= props.tabs[i];
      tabArr.push(<span key={i} className="headerBarState_tab">{tabName}</span>)
    }
  }
  return (
  <nav className="headerBarState">
    <span className="headerBarState_stateName">{stateName}</span>
    {tabArr.length == 0 ? null : tabArr}
  </nav>)
;}