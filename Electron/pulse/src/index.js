const { app, BrowserWindow } = require('electron');
const path = require('node:path');
const { ipcMain } = require('electron');

global.question_id = 0;

// Handle creating/removing shortcuts on Windows when installing/uninstalling.
if (require('electron-squirrel-startup')) {
    app.quit();
}

const createWindow = () => {
    // Create the browser window.
    const mainWindow = new BrowserWindow({
        width: 400,
        height: 600,
        webPreferences: {
            preload: path.join(__dirname, 'preload.js')
        },
    });

    // and load the index.html of the app.
    mainWindow.loadFile(path.join(__dirname, 'index.html'));

    // Open the DevTools.
    // mainWindow.webContents.openDevTools();
};

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.whenReady().then(() => {
    createWindow();

    // On OS X it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) {
            createWindow();
        }
    });
});

// Quit when all windows are closed, except on macOS. There, it's common
// for applications and their menu bar to stay active until the user quits
// explicitly with Cmd + Q.
app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and import them here.

// call api in main since we have access to nodejs apis here
ipcMain.handle("getQuestionID", () => {
    return global.question_id;
});

ipcMain.handle("updateQuestionID", (event, newId) => {
    global.question_id = newId;
});

ipcMain.handle("callQuoteApi", async () => {
    const response = await fetch('https://zenquotes.io/api/random');
    const data = await response.json();
    console.log(data[0]);
    return data[0];
});

ipcMain.handle("callGetQuestionApi", async () => {
    const requestBody = {
        email: "alice@techinnovators.com" 
    };

    const response = await fetch('https://xzrnwqkv35.execute-api.us-east-1.amazonaws.com/questions', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    });
    const data = await response.json();
    console.log(data);
    return data;
});

ipcMain.handle("callPostResponseApi", async (event, ratings, details) => {
    const requestBody = {
        email: "alice@techinnovators.com",
        question_id: global.question_id,
        rating: ratings,
        details: details
    };

    const response = await fetch('https://xzrnwqkv35.execute-api.us-east-1.amazonaws.com/respond', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
    });
    const data = await response.json();
    console.log(data);
    return data;
})