const { MSICreator } = require('electron-wix-msi');

const APP_DIR = 'C:/Users/ariel/!Github/pulsedailydemo/Electron/pulse/out/pulse-win32-x64';
const OUT_DIR = 'C:/Users/ariel/!Github/pulsedailydemo/Electron/pulse/out/msi/pulse';

// Step 1: Instantiate the MSICreator
const msiCreator = new MSICreator({
    appDirectory: APP_DIR,
    description: 'PulseDaily survery application',
    exe: 'pulse',
    name: 'PulseDaily',
    manufacturer: 'Ariel Liu',
    version: '1.0.0',
    outputDirectory: OUT_DIR,
    iconPath: 'C:/Users/ariel/!Github/pulsedailydemo/Electron/pulse/pulsedaily.ico',
    ui: {
        chooseDirectory: false
    },
    installDirectory: 'C:/Program Files/PulseDaily'
});

msiCreator.create().then(function () {
    msiCreator.compile();
});

// Step 2: Create a .wxs template file
// const supportBinaries = await msiCreator.create();

// 🆕 Step 2a: optionally sign support binaries if you
// sign you binaries as part of of your packaging script
// supportBinaries.forEach(async (binary) => {
    // Binaries are the new stub executable and optionally
    // the Squirrel auto updater.
    // await signFile(binary);
// });

// Step 3: Compile the template to a .msi file
// await msiCreator.compile();