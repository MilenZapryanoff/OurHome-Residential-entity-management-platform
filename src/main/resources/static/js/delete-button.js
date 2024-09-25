//Animated buttons with CUSTOM pop-up confirmation window
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

// Button click handler
document.querySelectorAll('.delete-button').forEach(button => {
    let text = button.querySelector('.text');
    text.innerHTML = '<span>' + text.textContent.trim().split('').join('</span><span>') + '</span>';

    button.addEventListener('click', e => {
        e.preventDefault();
        showCustomConfirm(() => {
            if (!button.classList.contains('delete')) {
                button.classList.add('delete');
                setTimeout(() => button.classList.remove('delete'), 2400);
                setTimeout(() => button.closest('form').submit(), 1500);
            }
        });
    });
});