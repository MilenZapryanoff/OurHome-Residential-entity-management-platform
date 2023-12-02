document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('uploadButton').addEventListener('click', function() {
        document.getElementById('documentInput').click();
    });

    document.getElementById('documentInput').addEventListener('change', function() {
        document.getElementById('documentForm').submit();
    });
});