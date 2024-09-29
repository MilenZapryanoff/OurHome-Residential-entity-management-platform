//non-animated buttons with DEFAULT browser pop-up confirmation window
function confirmSendMessage() {
    return confirm("Are you sure you want to send this message?");
}

function confirmUnlinkOwner() {
    return confirm("Are you sure you want to remove property owner?\n" +
        "This action is not reversible!\n" +
        "Property owner will have to make a new registration request to obtain access of his property data");
}


//non-animated buttons with CUSTOM pop-up confirmation window
document.getElementById('delete-btn').addEventListener('click', function() {
    // Show the custom modal
    document.querySelector('.modal').style.display = 'block';
});

document.getElementById('yes-btn').addEventListener('click', function() {
    // Submit the form when 'Yes' is clicked
    document.getElementById('form-confirm').submit();
    // Close the modal
    document.querySelector('.modal').style.display = 'none';
});

document.getElementById('no-btn').addEventListener('click', function() {
    // Simply close the modal when 'No' is clicked
    document.querySelector('.modal').style.display = 'none';
});



//html code
<!--custom pop-up confirmation window before <form>-->
/*
<div class="modal">
    <div class="modal-content">
        <strong class="text-danger-emphasis">Attention!</strong>
        <p class="text-dark">Do you really want to remove property owner?</p>
        <small class="text-secondary">Please note, that this action is not reversible!Property owner will have to make a new registration request to obtain access of his property data.</small>
        <div class="confirm-buttons-container">
            <button id="yes-btn">Confirm</button>
            <button id="no-btn">Cancel</button>
        </div>
    </div>
</div>


//form has to be with id="form-confirm"
//button has to be with id="delete-btn"

*/



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