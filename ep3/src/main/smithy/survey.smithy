$version: "2"

namespace hello

use alloy#simpleRestJson

@simpleRestJson
service SurveyService {
    operations: [GetQuestion]
}

@http(method: "GET", uri: "/getquestion/{questionType}")
@readonly
operation GetQuestion {
    input := {
        @required
        @httpLabel
        questionType: QuestionType
    }
    output := {
        @required
        message: String
    }
}

string QuestionType
