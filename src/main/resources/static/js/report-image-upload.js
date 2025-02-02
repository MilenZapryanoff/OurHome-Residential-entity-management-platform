
    // Отваря input полето за качване при клик върху изображение
    function triggerFileInput(imageIndex) {
    document.getElementById(`file-input-${imageIndex}`).click();
}

    // Актуализира визуализацията на каченото изображение
    function updateImagePreview(imageIndex) {
    const fileInput = document.getElementById(`file-input-${imageIndex}`);
    const file = fileInput.files[0]; // Избира първия файл
    const previewImage = document.getElementById(`image-preview-${imageIndex}`);

    if (file) {
    const reader = new FileReader();

    reader.onload = function (e) {
    previewImage.src = e.target.result; // Сменя източника на изображението
    previewImage.alt = "Uploaded Image";
    previewImage.title = "Uploaded Image";
};

    reader.readAsDataURL(file);
}
}

    // Връща изображението към дефолтното
    function resetToDefault(imageIndex, event) {
    event.stopPropagation(); // Спира клик събитието от дефолтната функционалност
    const previewImage = document.getElementById(`image-preview-${imageIndex}`);
    const fileInput = document.getElementById(`file-input-${imageIndex}`);

    // Връща дефолтното изображение
    previewImage.src = "/images/upload-image.jpg";
    previewImage.alt = "Default Image";
    previewImage.title = "Click to upload";

    // Изчиства избора на файл
    fileInput.value = "";
}
