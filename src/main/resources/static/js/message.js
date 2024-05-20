function openForm() {
    document.getElementById("messageText").value = "";
    document.getElementById("contactForm").style.display = "block";
}

function closeForm() {
    document.getElementById("contactForm").style.display = "none";
}


//Send message icon management
document.addEventListener('DOMContentLoaded', function () {
    const textAreas = document.querySelectorAll('.messageText');
    textAreas.forEach((inputField) => {
        const button = inputField.closest('form').querySelector('.send-message-button');
        const icon = document.createElement('i');
        icon.classList.add('fa-solid', 'fa-paper-plane');
        icon.style.fontSize = '20px';
        icon.style.display = 'flex';

        inputField.addEventListener("input", function () {
            if (inputField.value !== '') {
                if (!button.querySelector('i.fa-solid.fa-paper-plane')) {
                    button.appendChild(icon);
                }
                button.hidden = false;
            } else {
                if (button.querySelector('i.fa-solid.fa-paper-plane')) {
                    button.removeChild(icon);
                }
                button.hidden = true;
            }
        });
    });
});
