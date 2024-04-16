function feeComponentArrowChange() {
   let arrowsElement = document.getElementById('fee-component');
    changeArrowsDirection(arrowsElement);
}

function unpaidFeesArrowChange() {
    let arrowsElement = document.getElementById('unpaid-fees');
    changeArrowsDirection(arrowsElement);
}

function changeArrowsDirection(arrowsElement) {
    if (arrowsElement.textContent === '>>>') {
        arrowsElement.textContent = '<<<'
    } else {
        arrowsElement.textContent = '>>>'
    }
}
