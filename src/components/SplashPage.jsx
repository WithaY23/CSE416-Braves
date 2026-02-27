import React from 'react'
import '../../styles/splash-page.css'
// ─────────────────────────────────────────────
// SplashPage
// ─────────────────────────────────────────────
function PlaceHolderMap()
{
  return <div> Map Place Holder </div>
}
export const SplashPage = () => (
  <div className="splashPage">
    <div className="splashPage_body">
      {/* <h1 className="splashPage__title">Voting Rights Act Repeal Analysis</h1> */}
      <div className='splashPage_mapHeader'>Country Map</div>
      <PlaceHolderMap />
    </div>
  </div>
);
