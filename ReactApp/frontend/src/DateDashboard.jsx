import React from "react";
import { useState, useEffect } from "react";
import PieChart from "./PieChart";
import BigNumber from "./BigNumber";

function formatDate(date) {
	const months = [
	  'January', 'February', 'March', 'April', 'May', 'June',
	  'July', 'August', 'September', 'October', 'November', 'December'
	];
  
	const day = date.getDate();
	const month = months[date.getMonth()];
	const year = date.getFullYear();
  
	// Determine the correct suffix for the day
	let suffix;
	if (day > 3 && day < 21) {
	  suffix = 'th';
	} else {
	  switch (day % 10) {
		case 1: suffix = 'st'; break;
		case 2: suffix = 'nd'; break;
		case 3: suffix = 'rd'; break;
		default: suffix = 'th'; break;
	  }
	}
  
	return `${month} ${day}${suffix}, ${year}`;
}

export default function DateDashboard({ date, setDate, org_id, setOrgID }) {

	const [response, setResponse] = useState({});

	useEffect(() => {
		fetchData();
	}, [date]);

    const fetchData = async () => {
		const date_str = date.toISOString().slice(0, 10)
		const response = await fetch(
			`https://xzrnwqkv35.execute-api.us-east-1.amazonaws.com/data/question/${date_str}/${org_id}`
		);
		const response_data = await response.json();
		setResponse(response_data);
	};

	const handlePreviousDay = async () => {
		const newDate = new Date(date);
		newDate.setDate(date.getDate() - 1);
		setDate(newDate);
	};

	const handleNextDay = async () => {
		const newDate = new Date(date);
		newDate.setDate(date.getDate() + 1);
		setDate(newDate);
	};
    return (
        <>
            <div className="dashboard">
				<div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
					<h1>{formatDate(date)}</h1>
					<div className="controls">
						<button onClick={handlePreviousDay}>{"<"}</button>
						<button onClick={handleNextDay}>{">"}</button>
					</div>
				</div>
				{(!response || !response["ratings"] || response["ratings"].length === 0) && (<h2>No data for this day</h2>)}
				{response["question"] && (<h2>Question: {response["question"][0]["content"]}</h2>)}
				<div style={{ display: 'flex', alignItems: 'left', justifyContent: 'center', gap: '10%' }}>
					{
						response["ratings"] && 
						(<PieChart data={response["ratings"]} />)
					}
					{
						(response["rating_average"] &&
						response["rating_average"][0]["rating_avg"]) && 
						(<BigNumber number={response["rating_average"][0]["rating_avg"].toFixed(2)} message="Average rating:" color="#dbcec1" />)
					}
					{
						(response["response_rate"] &&
						response["response_rate"][0]["response_rate"]) && 
						(<BigNumber number={response["response_rate"][0]["response_rate"].toFixed(2)} message="Response rate:" color="#AADBD2" />)
					}
					{
						(
							response["response_rate"] &&
							response["response_rate"][0]["instance_count"] && 
							response["response_rate"][0]["response_count"]
						) &&
						(
							<div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center'}} >
								<h3>Out of {response["response_rate"][0]["instance_count"]} employees, {response["response_rate"][0]["response_count"]} responded.</h3>
							</div>
						)
					}
				</div>
            </div>
        </>
    )
}