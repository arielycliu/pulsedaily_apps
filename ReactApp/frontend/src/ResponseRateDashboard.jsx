import React from "react";
import { useState, useEffect } from "react";
import BigNumber from "./BigNumber";
import LineGraph from "./LineGraph";

export default function ResponseRateDashboard({ org_id, setOrgID }) {
    const [response, setResponse] = useState({});

	useEffect(() => {
		fetchData();
	});

    const fetchData = async () => {
		const r = await fetch(
			`https://xzrnwqkv35.execute-api.us-east-1.amazonaws.com/data/response_rate/${org_id}`
		);
		const response_json = await r.json();
		const response = JSON.parse(response_json.body)
		setResponse(response);
	};
    
    return (
        <>
            <div className="dashboard">
				<div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
					<h1>Response rates</h1>
				</div>

				<div style={{ display: 'flex', alignItems: 'left', justifyContent: 'center', gap: '10%' }}>
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
								<h3>Out of {response["response_rate"][0]["instance_count"]} employees, {response["response_rate"][0]["response_count"]} total responses have been recorded.</h3>
							</div>
						)
					}
                    {
                        (
                            response["response_rate_per_day"] &&
                            response["response_rate_per_day"][0]
                        ) && 
                        (
                            <LineGraph data={response["response_rate_per_day"]} />
                        )
                    }
				</div>
            </div>
        </>
    )
}