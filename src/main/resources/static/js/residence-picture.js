    document.addEventListener('DOMContentLoaded', function() {
        // Use a unique identifier for each entity form
        const forms = document.querySelectorAll('form[id^="pictureForm-"]');
        
        forms.forEach(form => {
            const formId = form.id.split('-')[1];
            const uploadButtonId = `uploadButton-${formId}`;
            const pictureInputId = `pictureInput-${formId}`;
            const pictureFormId = `pictureForm-${formId}`;

            document.getElementById(uploadButtonId).addEventListener('click', function() {
                document.getElementById(pictureInputId).click();
            });

            document.getElementById(pictureInputId).addEventListener('change', function() {
                document.getElementById(pictureFormId).submit();
            });
        });
    });