import React from 'react'
import "../../styles/cross-state-analysis.css";
import { useState } from 'react';

function Gingles() {
	return (
		<div className="ginglesChart">
			Gingles Chart
		</div>
	)
}

function Dropdown() {
	let minorityList = ['Asian', 'Black', 'Latino'];
	const [currentGroup, changeGroup] = useState(minorityList[0]); // Record the option chosen inside

	const minorityOptions = minorityList.map((minority)=> <option key={minority} value={minority}>{minority}</option>)

	// Display the minority content
	return(
	<div className="crossStateDropdownContainer crossStateContainer">
		<div>
				<label htmlFor="racialGroupSelector">Choose a racial group to analyze: </label>
				<select name="racialGroupSelector" id="racialGroupSelector" value={currentGroup} onChange={(e) => changeGroup(e.target.value)}>
				{minorityOptions}
				</select>
		</div>
	</div>)
}

function TableRow(props) {
	return (
		<tr>
			<td className="crossStateTableCell">P1</td>
			<td className="crossStateTableCell">500,000</td>
			<td className="crossStateTableCell">200,000</td>
			<td className="crossStateTableCell">250,000</td>
			<td className="crossStateTableCell">200,000</td>
		</tr>
	)
}

function prevPage() {
	// replace data with data that was in previous page
}

function nextPage() {
	// replace data with data that is in next page
}

export default function CrossStateAnalysis(props) {
	const page = 1;
	const totalPages = 25;

	return (
	<span id="crossStateMain">
		<div id="oregonContainer" className="stateContainer">
			<div className="ginglesContainer crossStateContainer">
				<h1>Oregon</h1>
				<Gingles />
			</div>
			<Dropdown />
			<div className="tableContainer crossStateContainer">
				<h2>Precincts Data</h2>
				<table>
					<tbody>
						<tr>
							<th className="crossStateTableCell">Precinct</th>
							<th className="crossStateTableCell">Total Population</th>
							<th className="crossStateTableCell">Minority Population</th>
							<th className="crossStateTableCell">Republican Votes</th>
							<th className="crossStateTableCell">Democratic Votes</th>
						</tr>
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
					</tbody>
				</table>
				<div className="tablePageContainer">
					<p className="tablePageArrow tablePageText" onClick={prevPage}>&lt;</p>
					<p className="tablePageText">Table {page}/{totalPages}</p>
					<p className="tablePageArrow tablePageText" onClick={nextPage}>&gt;</p>
				</div>
			</div>
		</div>
		<div id="SCContainer" className="stateContainer">
			<div className="ginglesContainer crossStateContainer">
				<h1>South Carolina</h1>
				<Gingles />
			</div>
			<Dropdown />
			<div className="tableContainer crossStateContainer">
				<h2>Precincts Data</h2>
				<table>
					<tbody>
						<tr>
							<th className="crossStateTableCell">Precinct</th>
							<th className="crossStateTableCell">Total Population</th>
							<th className="crossStateTableCell">Minority Population</th>
							<th className="crossStateTableCell">Republican Votes</th>
							<th className="crossStateTableCell">Democratic Votes</th>
						</tr>
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
						<TableRow />
					</tbody>
				</table>
				<div className="tablePageContainer">
					<p className="tablePageArrow tablePageText" onClick={prevPage}>&lt;</p>
					<p className="tablePageText">Table {page}/{totalPages}</p>
					<p className="tablePageArrow tablePageText" onClick={nextPage}>&gt;</p>
				</div>
			</div>
		</div>
	</span>
	)
}