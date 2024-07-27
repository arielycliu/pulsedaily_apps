import React from "react";
import { useState, useEffect } from "react";
import PieChart from "./PieChart";
import BigNumber from "./BigNumber";

export default function QuestionDashboard({ question_id, setQuestionID, org_id, setOrgID }) {

	const [response, setResponse] = useState({});

	useEffect(() => {
		fetchData();
	}, [question_id]);

    const fetchData = async () => {
		const r = await fetch(
			`https://xzrnwqkv35.execute-api.us-east-1.amazonaws.com/data/questions/${question_id}/${org_id}`
		);
		const response_json = await r.json();
		const response = JSON.parse(response_json.body);
		setResponse(response);
	};

	const handlePreviousQuestion = async () => {
        if (question_id != 0) {
            setQuestionID(question_id - 1);
        }
	};

	const handleNextQuestion = async () => {
		setQuestionID(question_id + 1);
	};
    return (
        <>
            <div className="dashboard">
				<div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
					<h1>Question ID: {question_id}</h1>
					<div className="controls">
						<button onClick={handlePreviousQuestion}>{"<"}</button>
						<button onClick={handleNextQuestion}>{">"}</button>
					</div>
				</div>
				{(!response || !response["ratings"] || response["ratings"].length === 0) && (<h2>No data for this question</h2>)}
				{(response["question"] && response["question"][0]) && (<h2>Question: {response["question"][0]["content"]}</h2>)}
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