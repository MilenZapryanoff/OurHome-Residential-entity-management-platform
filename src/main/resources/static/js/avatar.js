document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('uploadButton').addEventListener('click', function() {
        document.getElementById('avatarInput').click();
    });

    document.getElementById('avatarInput').addEventListener('change', function() {
        document.getElementById('avatarForm').submit();
    });
});