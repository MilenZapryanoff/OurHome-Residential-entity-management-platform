(function () {
    const slider = document.getElementById('monthlyFeeDate');
    const label  = document.getElementById('monthlyFeeDateLabel');

    function pad2(n) { return String(n).padStart(2, '0'); }

    // обновява бейджа при движение
    window.updateMonthlyFeeLabel = function (val) {
        label.textContent = pad2(val);
    };

    // първоначална стойност
    label.textContent = pad2(slider.value || 1);
})();