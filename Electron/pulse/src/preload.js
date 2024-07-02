// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts
const { contextBridge, ipcMain, ipcRenderer } = require('electron')

// methods that will be exposed to renderer (frontend) process
let indexBridge = {
    getQuestionID: getQuestionID,
    updateQuestionID: updateQuestionID,
    callQuoteApi: callQuoteApi,
    callGetQuestionApi: callGetQuestionApi
}
async function getQuestionID() {
    const question_id = await ipcRenderer.invoke("getQuestionID");
    return question_id;
}

async function updateQuestionID(newId) {
    await ipcRenderer.invoke("updateQuestionID", newId);
}

async function callQuoteApi() {
    const result = await ipcRenderer.invoke("callQuoteApi"); // send ipc to main process
    const quote = document.getElementById("quote");
    quote.innerText = result.q;
    const author = document.getElementById("quote-author");
    author.innerText = "- " + result.a;
}

async function callGetQuestionApi() {
    const result = await ipcRenderer.invoke("callGetQuestionApi"); 
    const question_id = result.question_id;
    const question = document.getElementById("question");
    question.innerText = result.content;
    await updateQuestionID(question_id);
}

// expose indexBridge to render process
contextBridge.exposeInMainWorld("indexBridge", indexBridge)