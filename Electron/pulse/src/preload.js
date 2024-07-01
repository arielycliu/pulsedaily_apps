// See the Electron documentation for details on how to use preload scripts:
// https://www.electronjs.org/docs/latest/tutorial/process-model#preload-scripts
const { contextBridge, ipcMain, ipcRenderer } = require('electron')

// methods that will be exposed to renderer (frontend) process
let indexBridge = {
    callQuoteApi: async () => {
        const result = await ipcRenderer.invoke("callQuoteApi"); // send ipc to main process
        const quote = document.getElementById("quote");
        quote.innerText = result.q;
        const author = document.getElementById("quote-author");
        author.innerText = "- " + result.a;
    }
}
// expose indexBridge to render process
contextBridge.exposeInMainWorld("indexBridge", indexBridge)