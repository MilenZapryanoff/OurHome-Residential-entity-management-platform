document.addEventListener("DOMContentLoaded", function() {
    const textarea = document.querySelector('.messageText');
    const button = document.getElementById('sendMessageButton');

    textarea.addEventListener('input', function() {
        if (textarea.value.trim().length > 0) {
            button.classList.remove('text-bg-secondary');
            button.classList.add('text-bg-success');
        } else {
            button.classList.remove('text-bg-success');
            button.classList.add('text-bg-secondary');
        }
    });
});

//function for displaying message dialog
function openForm() {
    document.getElementById("messageText").value = "";
    document.getElementById("contactForm").style.display = "block";
}

function closeForm() {
    document.getElementById("contactForm").style.display = "none";
}