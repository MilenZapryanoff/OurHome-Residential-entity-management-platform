function manageInputFields() {

    const notHabitableFlag = document.getElementById('isNotHabitable');
    const numberOfAdults = document.getElementById('numberOfAdults');
    const numberOfChildren = document.getElementById('numberOfChildren');
    const numberOfPets = document.getElementById('numberOfPets');
    const propertyNumber = document.getElementById('number')
    const button = document.getElementById('form-submit')


// Call the function when the page loads
    window.addEventListener('load', () => {
        if (propertyNumber.value === '0') {
            propertyNumber.value = '';
        }
        if (numberOfAdults.value === '0') {
            numberOfAdults.value = '';
            adults = '';
        }
        if (numberOfChildren.value === '0') {
            numberOfChildren.value = '';
            children = ''
        }
        if (numberOfPets.value === '0') {
            numberOfPets.value = '';
            pets = '';
        }
        if (notHabitableFlag.checked) {
            disableInputFields();
        }
    });

    let adults = numberOfAdults.value;
    let children = numberOfChildren.value;
    let pets = numberOfPets.value;

    numberOfAdults.addEventListener('change', function () {
        adults = numberOfAdults.value;
    })

    numberOfChildren.addEventListener('change', function () {
        children = numberOfChildren.value;
    })

    numberOfPets.addEventListener('change', function () {
        pets = numberOfPets.value;
    })

    notHabitableFlag.addEventListener('change', function () {
        if (this.checked) {
            disableInputFields();
        } else {
            enableInputFields();
        }
    });

    button.addEventListener('click', function (event) {
        if (numberOfChildren.value === '') {
            numberOfChildren.value = '0';
        }
        if (numberOfPets.value === '') {
            numberOfPets.value = '0'
        }
    })


    function disableInputFields() {
        numberOfAdults.value = '0';
        numberOfChildren.value = '0';
        numberOfPets.value = '0';

        numberOfAdults.disabled = true;
        numberOfPets.disabled = true;
        numberOfChildren.disabled = true

        numberOfAdults.title = "Adults number can be set only for habitable properties!"
        numberOfChildren.title = "Children number can be set only for habitable properties!"
        numberOfPets.title = "Pets number can be set only for habitable properties!"
    }

    function enableInputFields() {
        numberOfAdults.value = adults;
        numberOfChildren.value = children;
        numberOfPets.value = pets;

        numberOfAdults.disabled = false;
        numberOfPets.disabled = false;
        numberOfChildren.disabled = false;

        numberOfAdults.title = ""
        numberOfChildren.title = ""
        numberOfPets.title = ""
    }
}

manageInputFields();

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