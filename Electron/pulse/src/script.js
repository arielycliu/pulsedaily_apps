function callQuoteApi() {
    window.indexBridge.callQuoteApi();
}

function callGetQuestionApi() {
    window.indexBridge.callGetQuestionApi();
}

function callPostResponseApi() {
    // Get rating value
    let rating = document.querySelector('input[name="rating"]:checked');
    rating = rating ? rating.value : null;
    if (!rating) {
        return;
    }

    // Get details value
    const details = document.getElementById('details').value;

    console.log('Rating:', rating);
    console.log('Details:', details);

    window.indexBridge.callPostResponseApi(rating, details);
}

window.onload = function() {
    callQuoteApi();
    callGetQuestionApi();
    const confirmation = document.getElementById("confirmation");
    confirmation.style.display = "none";
};