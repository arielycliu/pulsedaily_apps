$version: "2.0"

namespace survey

// use alloy#simpleRestJson

// @simpleRestJson
service SurveyService {
    operations: [GetQuestion]
}

// @http(method: "GET", uri: "/getquestion")
@readonly
operation GetQuestion {
    output := {
        @required
        question: String
    }
}
