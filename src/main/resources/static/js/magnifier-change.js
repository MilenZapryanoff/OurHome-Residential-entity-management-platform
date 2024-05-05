function feeComponentMagnifierChange() {
   let arrowsElement = document.getElementById('fee-component');
    changeMagnifier(arrowsElement);
}

function feesHistoryMagnifierChange() {
    let arrowsElement = document.getElementById('fees-history');
    changeMagnifier(arrowsElement);
}

function changeMagnifier(arrowsElement) {
    let className = arrowsElement.className;

    if (className.includes('fa-magnifying-glass-plus')) {
        arrowsElement.classList.remove('fa-magnifying-glass-plus');
        arrowsElement.classList.add('fa-magnifying-glass-minus')
        arrowsElement.classList.add('text-dark')
    } else {
        arrowsElement.classList.remove('fa-magnifying-glass-minus');
        arrowsElement.classList.remove('text-dark')
        arrowsElement.classList.add('fa-magnifying-glass-plus')
    }
}
