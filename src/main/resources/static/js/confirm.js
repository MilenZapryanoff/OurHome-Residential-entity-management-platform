//CUSTOM pop-up confirmation window
function showCustomConfirm(callback) {
    document.querySelector('.modal').style.display = 'block';
    // Store the callback to execute on confirmation
    window.customConfirmCallback = callback;
}

// Handle 'Yes' and 'No' button clicks in the modal
document.getElementById('yes-btn').addEventListener('click', function() {
    if (window.customConfirmCallback) {
        window.customConfirmCallback();
    }
    document.querySelector('.modal').style.display = 'none';
});

document.getElementById('no-btn').addEventListener('click', function() {
    document.querySelector('.modal').style.display = 'none';
    window.customConfirmCallback = null;
});


//Monthly fees ALERTS custom pop-up notification
function openCustomDialog(id, message) {
    const dialogTextId = 'customDialogText_' + id;
    const dialogId = 'customDialog_' + id;

    // Set the message in the dialog box
    const dialogTextElement = document.getElementById(dialogTextId);
    if (dialogTextElement) {
        dialogTextElement.innerText = message;
    } else {
        console.error('Dialog text element not found:', dialogTextId);
    }

    // Show the custom dialog
    const dialogElement = document.getElementById(dialogId);
    if (dialogElement) {
        dialogElement.style.display = 'flex';
        dialogElement.style.flexDirection = 'column'
        dialogElement.style.alignItems='center';
    } else {
        console.error('Dialog element not found:', dialogId);
    }

    // Add event listener to OK button
    const okButton = dialogElement.querySelector('.custom-dialog-ok');
    if (okButton) {
        okButton.addEventListener('click', function () {
            closeCustomDialog(id);
        });
    } else {
        console.error('OK button not found in dialog:', dialogId);
    }
}

/* Function to close a custom dialog */
function closeCustomDialog(id) {
    const dialogId = 'customDialog_' + id;

    // Hide the custom dialog
    const dialogElement = document.getElementById(dialogId);
    if (dialogElement) {
        dialogElement.style.display = 'none';
    } else {
        console.error('Dialog element not found:', dialogId);
    }
}