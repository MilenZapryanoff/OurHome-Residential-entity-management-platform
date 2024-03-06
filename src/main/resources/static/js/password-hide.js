const togglePassword = document.querySelector('#togglePassword');
const password = document.querySelector('#password');

let isMouseDown = false;

togglePassword.addEventListener('mousedown', function (e) {
    isMouseDown = true;
    // Show the password
    password.setAttribute('type', 'text');
    // Change the eye icon to hide
    togglePassword.classList.remove('fa-eye-slash');
    togglePassword.classList.add('fa-eye');
    togglePassword.classList.remove('text-secondary');
    togglePassword.classList.add('text-dark');

});

// When mouse leaves the icon area, hide the password if the left button is not held down
togglePassword.addEventListener('mouseleave', function (e) {
    if (!e.buttons) {
        // Hide the password
        password.setAttribute('type', 'password');
        isMouseDown = false;
        // Change the eye icon to show
        togglePassword.classList.remove('fa-eye');
        togglePassword.classList.add('fa-eye-slash');
    }
});

// When the mouse is released, hide the password
document.addEventListener('mouseup', function (e) {
    if (isMouseDown) {
        // Hide the password
        password.setAttribute('type', 'password');
        isMouseDown = false;
        // Change the eye icon to show
        togglePassword.classList.remove('fa-eye');
        togglePassword.classList.add('fa-eye-slash');
        togglePassword.classList.remove('text-dark');
        togglePassword.classList.add('text-secondary');

    }
});
