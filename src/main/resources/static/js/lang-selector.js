    // Show or hide the dropdown menu
    document.getElementById('dropdown').addEventListener('click', function () {
    const menu = document.getElementById('dropdown-menu');
    menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
});

    // Handle language selection
    function selectLanguage(lang) {
    document.getElementById('selected-lang').value = lang; // Set the selected language
    document.getElementById('langSelector').submit(); // Automatically submit the form
}

    // Hide the dropdown if clicked outside
    window.addEventListener('click', function (e) {
    const dropdown = document.getElementById('dropdown');
    const menu = document.getElementById('dropdown-menu');
    if (!dropdown.contains(e.target)) {
    menu.style.display = 'none';
}
});
