$version: "2"

namespace hello

use alloy#simpleRestJson

@simpleRestJson
service SurveyService {
  version: "1.0.0",
  operations: [GetQuestion]
}

@http(method: "GET", uri: "/survey/{questionType}/getquestion", code: 200)
@readonly
operation GetQuestion {
  input := {
    @required
    @httpLabel
    questionType: String
  }
  output := {
    @required
    question: String
  }
}
