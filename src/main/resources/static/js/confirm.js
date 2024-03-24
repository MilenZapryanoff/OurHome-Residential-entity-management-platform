function confirmSendMessage() {
    var result = confirm("Are you sure you want to send this message?");
    if (result) {
        return true;
    } else {
        return false;
    }
}

function confirmOwnerRemove() {
    var result = confirm("Removing owner from Residential entity will also delete his data and properties! Proceed with this action?");
    if (result) {
        return true;
    } else {
        return false;
    }
}

function confirmPropertyDelete() {
    var result = confirm("Deleting property will also erase the associated with this property data! Proceed with this action?");
    if (result) {
        return true;
    } else {
        return false;
    }
}

function confirmPropertyTypeDelete() {
    var result = confirm("Are you sure you want to delete this property type?");
    if (result) {
        return true;
    } else {
        return false;
    }
}

function confirmPropertyFeeDelete() {
    var result = confirm("Do you really want to delete this record?");
    if (result) {
        return true;
    } else {
        return false;
    }
}

function confirmExpenseRemove() {
    var result = confirm("Do you really want to delete this expense?");
    if (result) {
        return true;
    } else {
        return false;
    }
}

function openCustomDialog(id, message) {
    var dialogTextId = 'customDialogText_' + id;
    var dialogId = 'customDialog_' + id;

    // Set the message in the dialog box
    var dialogTextElement = document.getElementById(dialogTextId);
    if (dialogTextElement) {
        dialogTextElement.innerText = message;
    } else {
        console.error('Dialog text element not found:', dialogTextId);
    }

    // Show the custom dialog
    var dialogElement = document.getElementById(dialogId);
    if (dialogElement) {
        dialogElement.style.display = 'block';
    } else {
        console.error('Dialog element not found:', dialogId);
    }

    // Add event listener to OK button
    var okButton = dialogElement.querySelector('.custom-dialog-ok');
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
    var dialogId = 'customDialog_' + id;

    // Hide the custom dialog
    var dialogElement = document.getElementById(dialogId);
    if (dialogElement) {
        dialogElement.style.display = 'none';
    } else {
        console.error('Dialog element not found:', dialogId);
    }
}