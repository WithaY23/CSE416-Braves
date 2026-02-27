import React from 'react'
import "../../styles/country-header.css";



// Props: siteName(1) {VRA Repeal Analysis}, tabs(many) (likely will not change given our format)
export function CountryHeaderBar(props)
{
  const siteName= props.siteName
  let tabArr = []
  if(props.hasOwnProperty('tabs'))
  {
    // Make each tab formatted as to tabs
    for(let i = 0; i< props.tabs.length; i++)
    {
      const tabName= props.tabs[i];
      tabArr.push(<span key={i} className="headerBarCountry_tab">{tabName}</span>)
    }
  }
  return (
  <nav className="headerBarCountry">
    <span className="headerBarCountry_siteName">{siteName}</span>
    {tabArr}
  </nav>)
;}
