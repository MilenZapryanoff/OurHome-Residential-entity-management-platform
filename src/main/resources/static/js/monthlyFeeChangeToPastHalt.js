(function () {
    const form = document.getElementById('input-form');
    const slider = document.getElementById('monthlyFeeDate');
    const originalInput = document.getElementById('originalMonthlyFeeDate');

    function daysInMonth(d) {
        const y = d.getFullYear(), m = d.getMonth();
        return new Date(y, m + 1, 0).getDate();
    }

    function effectiveDay(day) {
        const n = Math.max(1, Math.min(31, Number(day) || 1));
        return Math.min(n, daysInMonth(new Date()));
    }

    function pad2(n) {
        return String(n).padStart(2, '0');
    }

    form.addEventListener('submit', function (e) {
        const today = new Date();
        const todayDay = today.getDate();
        const todayDateStrBG = today.toLocaleDateString('bg-BG', { day: '2-digit', month: 'long', year: 'numeric' });
        const todayDateStrEN = today.toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' });

        const original = Number(originalInput.value || 1);
        const current = Number(slider.value || 1);

        const originalEff = effectiveDay(original);
        const currentEff = effectiveDay(current);

        const wasFuture = originalEff >= todayDay;
        const nowPast = currentEff < todayDay;

        if (wasFuture && nowPast) {
            e.preventDefault();

            // Определяне на езика от <html lang="">
            const lang = document.documentElement.lang || 'bg';
            let msg;

            if (lang.startsWith('bg')) {
                msg =
                    `Внимание!\n\n` +
                    `На път сте да промените датата за формиране на месечните такси от ${pad2(originalEff)} на ${pad2(currentEff)}.\n\n` +
                    `Новоизбраната дата е по-стара от текущата дата (${todayDateStrBG}) и е възможно това да доведе до пропуск в генерирането на такси за този месец.\n\n` +
                    `Моля, уверете се, че таксите за месеца са вече генерирани, или след промяната генерирайте ръчно такива.\n\n` +
                    `Желаете ли да потвърдите промяната?`;
            } else {
                msg =
                    `Warning!\n\n` +
                    `You are about to change the fee calculation date from ${pad2(originalEff)} to ${pad2(currentEff)}.\n\n` +
                    `The newly selected date is earlier than today's date (${todayDateStrEN}), which may result in missing the automatic fee generation for this month.\n\n` +
                    `Please make sure the fees for this month have already been generated, or generate them manually after the change.\n\n` +
                    `Do you want to confirm this change?`;
            }

            if (window.confirm(msg)) {
                form.submit();
            }
        }
    });
})();
