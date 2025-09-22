
    (function () {
    const chk      = document.getElementById('isNotHabitable');
    const adults   = document.getElementById('numberOfAdults');
    const children = document.getElementById('numberOfChildren');
    const pets     = document.getElementById('numberOfPets');

    if (!chk || !adults || !children || !pets) return;

    const fields = [adults, children, pets];

    // Запази "нормалните" стойности при стартиране
    fields.forEach(f => {
    // пазим последната нормална стойност (преди да стане необитаем)
    f.dataset.prev = f.value ?? '';
});

    // Създава/обновява скрито огледало със същото име (за да се прати '0' при disabled)
    function ensureHiddenMirror(input, value) {
    const hiddenId = input.id + 'HiddenMirror';
    let hidden = document.getElementById(hiddenId);
    if (!hidden) {
    hidden = document.createElement('input');
    hidden.type = 'hidden';
    hidden.id = hiddenId;
    // важно: използваме същото име като оригиналното поле, за да се върже към същото свойство в BindingModel
    hidden.name = input.name;
    // поставяме го веднага след оригиналния input
    input.insertAdjacentElement('afterend', hidden);
}
    hidden.value = value;
}

    // Премахва скритото огледало (когато вече не е нужно)
    function removeHiddenMirror(input) {
    const hiddenId = input.id + 'HiddenMirror';
    const hidden = document.getElementById(hiddenId);
    if (hidden) hidden.remove();
}

    function applyNotHabitable() {
    fields.forEach(f => {
    // запомни последната нормална стойност преди зануляване
    f.dataset.prev = f.value ?? f.dataset.prev ?? '';
    // показваме 0 в UI (по избор), но ключово е да DISABLE-нем
    f.value = '0';
    f.disabled = true;           // прави полето неактивно
    f.setAttribute('aria-disabled', 'true');

    // осигуряваме скрито огледало: name съвпада, value = 0
    ensureHiddenMirror(f, '0');
});
}

    function clearNotHabitable() {
    fields.forEach(f => {
    // връщаме нормалната стойност
    const prev = (f.dataset.prev !== undefined) ? f.dataset.prev : '';
    f.disabled = false;
    f.removeAttribute('aria-disabled');
    f.value = prev;

    // махаме скритото огледало, за да не дублираме стойности
    removeHiddenMirror(f);
});
}

    // Ако чекът е сложен при зареждане – приложи режим "необитаем"
    if (chk.checked) {
    applyNotHabitable();
}

    // Когато се променя състоянието на чекбокса
    chk.addEventListener('change', () => {
    if (chk.checked) {
    applyNotHabitable();
} else {
    clearNotHabitable();
}
});

    // Докато е необитаем, ако по някаква причина стойностите се пипнат (напр. от друг JS),
    // гарантираме 0 точно преди submit:
    const form = document.getElementById('input-form');
    if (form) {
    form.addEventListener('submit', () => {
    if (chk.checked) {
    fields.forEach(f => {
    // нули към бекенда
    ensureHiddenMirror(f, '0');
});
}
});
}
})();

