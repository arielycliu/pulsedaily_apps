function callQuoteApi() {
    window.indexBridge.callQuoteApi();
}

function callGetQuestionApi() {
    window.indexBridge.callGetQuestionApi();
}

window.onload = function() {
    callQuoteApi();
    callGetQuestionApi();
};
