$version: "2"

namespace hello

use alloy#simpleRestJson

@simpleRestJson
service SurveyService {
  version: "1.0.0",
  operations: [GetQuestion]
}

@readonly
@http(method: "GET", uri: "/getquestion", code: 200)
operation GetQuestion {
  output := {
    @required
    question: String
  }
}