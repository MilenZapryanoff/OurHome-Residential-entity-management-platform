function calcTotalSum() {
    let fundMmAmountElement = document.getElementById('fundMmAmount');
    let fundRepairAmountElement = document.getElementById('fundRepairAmount');
    let totalFeeAmountElement = document.getElementById('feeAmount');

    // Define the function to calculate the total fee amount
    function calculateTotalFee() {
        let fundMmAmount = Number(fundMmAmountElement.value);
        let fundRepairAmount = Number(fundRepairAmountElement.value);
        totalFeeAmountElement.value = fundMmAmount + fundRepairAmount;
    }

    // Call the function when the page loads
    window.addEventListener('load', () => {
        calculateTotalFee();
    });

    // Add the event listeners to recalculate total fee amount when input values change
    fundMmAmountElement.addEventListener('change', () => {
        calculateTotalFee();
    });

    fundRepairAmountElement.addEventListener('change', () => {
        calculateTotalFee();
    });
}

// Call the calcTotalSum function to start the calculation process
calcTotalSum();