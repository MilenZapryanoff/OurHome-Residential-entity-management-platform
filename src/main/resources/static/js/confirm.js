function confirmSendMessage() {
    const result = confirm("Are you sure you want to send this message?");
    if (result) {
        return true;
    } else {
        return false;
    }
}

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
        dialogElement.style.display = 'block';
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