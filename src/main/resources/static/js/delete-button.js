document.querySelectorAll('.delete-button').forEach(button => {
    let text = button.querySelector('.text');
    text.innerHTML = '<span>' + text.textContent.trim().split('').join('</span><span>') + '</span>';

    button.addEventListener('click', e => {
        if (confirmPropertyFeeDelete()) {
            if (!button.classList.contains('delete')) {
                button.classList.add('delete');
                setTimeout(() => button.classList.remove('delete'), 2400);
                setTimeout(() => button.closest('form').submit(), 1500);
            }
        }
        e.preventDefault();
    });
});

function confirmPropertyFeeDelete() {
    return confirm("Do you really want to delete this record?");
}
