const parkingAvailable = document.getElementById('parkingAvailable');
const parkingAvailableAnimation = document.getElementById('parkingAvailableAnimation');




parkingAvailableAnimation.addEventListener('change', function (e) {

    if (parkingAvailableAnimation.checked) {
        parkingAvailable.checked = true;
    }
    else if (!parkingAvailableAnimation.checked) {
        parkingAvailable.checked = false;
    }
});