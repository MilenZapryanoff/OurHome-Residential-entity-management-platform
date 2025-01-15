//DELETE BUTTON handler
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

//other modal buttons handler
document.querySelectorAll('.modal-button').forEach(button => {
    button.addEventListener('click', function() {
        const form = button.closest('form'); // Намира формата, към която принадлежи бутонът
        showCustomConfirm(() => {
            form.submit(); // Изпраща формата при потвърждение
        });
    });
});