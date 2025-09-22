//Common DELETE BUTTON handler
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


//Administration/Property specific DELETE button handler
(function () {
    const button = document.getElementById('deletePropertyBtn');
    if (!button) return;

    // украса на текста (буквички)
    const text = button.querySelector('.text');
    if (text) {
        text.innerHTML = '<span>' + text.textContent.trim().split('').join('</span><span>') + '</span>';
    }

    button.addEventListener('click', e => {
        e.preventDefault();

        const targetForm = document.getElementById('deletePropertyForm'); // <- вместо closest('form')
        if (!targetForm) {
            console.error('Delete form #deletePropertyForm не е намерена.');
            return;
        }

        showCustomConfirm(() => {
            if (!button.classList.contains('delete')) {
                button.classList.add('delete');
                setTimeout(() => button.classList.remove('delete'), 2400);
                setTimeout(() => {
                    if (typeof targetForm.requestSubmit === 'function') {
                        targetForm.requestSubmit();
                    } else {
                        targetForm.submit();
                    }
                }, 1500);
            }
        });
    });
})();

//other modal buttons handler
document.querySelectorAll('.modal-button').forEach(button => {
    button.addEventListener('click', function() {
        const form = button.closest('form'); // Намира формата, към която принадлежи бутонът
        showCustomConfirm(() => {
            form.submit(); // Изпраща формата при потвърждение
        });
    });
});