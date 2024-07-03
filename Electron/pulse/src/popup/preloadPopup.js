const { contextBridge, ipcRenderer } = require('electron')

// methods that will be exposed to renderer (frontend) process
let indexBridge = {
    callRegisterEmployee: callRegisterEmployee
}
async function startMain() {
    await ipcRenderer.invoke("startMain");
}

async function callRegisterEmployee(email) {
    const status = await ipcRenderer.invoke("registerEmployee", email);
    if (status === 404) {
        msg = "Organization not found. Please confirm that your email suffix is correct, and contact support if you still have further issues."
        const message = document.getElementById("message");
        message.innerText = msg;
        message.style.display = "inline";
    } else {
        const message = document.getElementById("message");
        message.innerText = "Email received. Thank you!";
        message.style.display = "inline";
        setTimeout(() => {
            window.close();
            startMain();
        }, 1000);
    }
}
contextBridge.exposeInMainWorld("indexBridge", indexBridge)