// /js/feeAmountDynamicCalculation.js
(function () {
    // Скриптът е в края на <body>, елементите вече са в DOM
    const mmEl     = document.getElementById('fundMmAmount');
    const repEl    = document.getElementById('fundRepairAmount');
    const totalEl  = document.getElementById('feeAmount');
    const submitEl = document.getElementById('form-submit');

    if (!mmEl || !repEl || !totalEl) {
        console.warn('[feeAmount] Missing required inputs: fundMmAmount, fundRepairAmount or feeAmount.');
        return;
    }

    function toNum(v) {
        if (v === null || v === undefined || v === '') return 0;
        const n = parseFloat(String(v).replace(',', '.'));
        return Number.isFinite(n) ? n : 0;
    }

    function round2(x) { return Math.round(x * 100) / 100; }

    // глобално име само ако го викаш отвън; не е задължително
    window.calcTotalSum = function calcTotalSum() {
        const mm  = toNum(mmEl.value);
        const rep = toNum(repEl.value);
        const sum = round2(mm + rep);

        totalEl.value = sum;

        // по желание: блокирай submit ако сумата е 0
        if (submitEl) submitEl.disabled = sum <= 0;

        try {
            totalEl.setCustomValidity(sum > 0 ? '' : 'Total monthly fee must be greater than 0.');
        } catch (_) {}
    };

    ['input', 'change', 'keyup', 'blur'].forEach(ev => {
        mmEl.addEventListener(ev, window.calcTotalSum);
        repEl.addEventListener(ev, window.calcTotalSum);
    });

    // първоначално изчисление
    window.calcTotalSum();
})();
