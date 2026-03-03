import React, { useState } from 'react'
import SplashPage from './components/SplashPage'
import StatePage from './components/StatePage'
import { CountryHeaderBar } from './components/CountryHeaderBar'
import '../styles/main.css'
import { Routes, Route } from 'react-router-dom'
import { StateHeaderBar } from './components/StateHeaderBar'
import MinorityAnalysis from './components/MinorityAnalysis'
import CrossStateAnalysis from './components/CrossStateAnalysis'
import VRAAnalysis from './components/VRAAnalysis'
import CustomStateAnalysis from './components/CustomStateAnalysis'

export default function App() {
  const [currPage, switchPage] = useState('Country');
  const countryTabs = ['Cross State Analysis'];
  const stateTabs = ['Voting Rights Analysis', 'Minority Analysis', 'Custom State Analysis'];

  return (
    <>
      <Routes>
        <Route path='/' element={
          <>
            <CountryHeaderBar currPage={currPage} switchPage={switchPage} siteName='VRA Repeal Analysis' tabs={countryTabs}/>
            <SplashPage currPage={currPage} switchPage={switchPage}/>
          </>
        } />
        <Route path='/Cross State Analysis' element={
          <>
            <CountryHeaderBar currPage={currPage} switchPage={switchPage} siteName='VRA Repeal Analysis' tabs={countryTabs}/>
            <CrossStateAnalysis currPage={currPage} switchPage={switchPage}/>
          </>
        } />
        <Route path={'/state/:stateName'} element={
          <>
            <CountryHeaderBar currPage={currPage} switchPage={switchPage} siteName='VRA Repeal Analysis' tabs={countryTabs}/>
            <StateHeaderBar currPage={currPage} switchPage={switchPage} tabs={stateTabs} />
            <StatePage currPage={currPage} switchPage={switchPage}/>
          </>
        } />
        <Route path={'/state/:stateName/Voting Rights Analysis'} element={
          <>
            <CountryHeaderBar currPage={currPage} switchPage={switchPage} siteName='VRA Repeal Analysis' tabs={countryTabs}/>
            <StateHeaderBar currPage={currPage} switchPage={switchPage} tabs={stateTabs} />
            <VRAAnalysis currPage={currPage} switchPage={switchPage} />
          </>
        } />
        <Route path={'/state/:stateName/Minority Analysis'} element={
          <>
            <CountryHeaderBar currPage={currPage} switchPage={switchPage} siteName='VRA Repeal Analysis' tabs={countryTabs}/>
            <StateHeaderBar currPage={currPage} switchPage={switchPage} tabs={stateTabs} />
            <MinorityAnalysis currPage={currPage} switchPage={switchPage} />
          </>
        } />
        <Route path={'/state/:stateName/Custom State Analysis'} element={
          <>
            <CountryHeaderBar currPage={currPage} switchPage={switchPage} siteName='VRA Repeal Analysis' tabs={countryTabs}/>
            <StateHeaderBar currPage={currPage} switchPage={switchPage} tabs={stateTabs} />
            <CustomStateAnalysis currPage={currPage} switchPage={switchPage} />
          </>
        } />
      </Routes>
    </>
  )
}
