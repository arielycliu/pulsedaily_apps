const cron = require('node-cron');
const { app } = require('electron');

function startAppAtScheduledTime() {
    // (7:50 PM daily)
    cron.schedule('50 19 * * *', () => {
        console.log('Starting Electron app at 7:50 PM');
        
        app.relaunch();
        app.exit();
    }, {
        timezone: 'America/New_York' 
    });
}

module.exports = startAppAtScheduledTime;
