const { app, BrowserWindow, ipcMain } = require('electron/main')
const path = require('node:path')

function createWindow () {
  const win = new BrowserWindow({
    width: 400,
    height: 320,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js')
    }
  })

  win.loadFile('index.html');
}

app.whenReady().then(() => {
  createWindow()

  ipcMain.on('close-app', () => {
    setTimeout(function() {
      app.quit();
    }, 5);
  });
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

