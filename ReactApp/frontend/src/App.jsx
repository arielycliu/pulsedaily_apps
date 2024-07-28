import { useState, useEffect } from "react";
import "./App.css";
import DateDashboard from "./DateDashboard";
import QuestionDashboard from "./QuestionDashboard";
import ResponseRateDashboard from "./ResponseRateDashboard";

function App() {
	const [question_id, setQuestionID] = useState(5);
	const [org_id, setOrgID] = useState(1);
	const [date, setDate] = useState(new Date());

	return (
		<>
			<DateDashboard date={date} setDate={setDate} org_id={org_id} setOrgID={setOrgID} />
			<QuestionDashboard question_id={question_id} setQuestionID={setQuestionID} org_id={org_id} setOrgID={setOrgID} />
			<ResponseRateDashboard org_id={org_id} setOrgID={setOrgID} />
		</>
	);
}

export default App;
