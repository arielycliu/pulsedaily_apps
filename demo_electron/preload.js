const { app, BrowserWindow, ipcRenderer } = require('electron');

window.onload = () => {
  const submitButton = document.getElementById('submit');
  const confirmationMessage = document.getElementById('confirmation');
  
  submitButton.addEventListener('click', () => {
    confirmationMessage.style.color = '#000000';
    setTimeout(function(){  
      ipcRenderer.send('close-app');
    }, 1000);
  });
};