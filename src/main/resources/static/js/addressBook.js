document.addEventListener("DOMContentLoaded", function () {
    const htmlLang = document.documentElement.lang || "bg"; // default bg
    const residentsContainer = document.getElementById("resident-container");
    const addResidentBtn = document.getElementById("addResident");
    const residentCountSpan = document.getElementById("resident-count");

    const childrenContainer = document.getElementById("children-container");
    const addChildBtn = document.getElementById("addChild");
    const childCountSpan = document.getElementById("child-count");

    const rememberMe = document.getElementById("rememberMe");
    const rememberMeContainer = document.querySelector('label[for="rememberMe"]').parentElement;
    const ownerName = document.getElementById("ownerFirstName");
    const ownerPhone = document.getElementById("ownerPhone");
    const ownerEmail = document.getElementById("ownerEmail");

    const petCountField = document.getElementById("numberOfPets");
    const petCountSpan = document.getElementById("pet-count");

    const isNotHabitableCheckbox = document.getElementById("isNotHabitable");
    const numberOfPetsField = document.querySelector('[name="numberOfPets"]');
    const residentsAndPetsSection = document.querySelector(".resident-and-pets-section");
    const formFields = document.querySelectorAll('#input-form input, #input-form select, #input-form textarea');

    function updateResidentCounter() {
        const residents = residentsContainer.querySelectorAll(".resident");
        residentCountSpan.textContent = isNotHabitableCheckbox.checked ? "—" : `${residents.length}`;
    }

    function updateChildCounter() {
        const children = childrenContainer.querySelectorAll(".child");
        childCountSpan.textContent = isNotHabitableCheckbox.checked ? "—" : `${children.length}`;
    }

    function updatePetCounter() {
        petCountSpan.textContent = isNotHabitableCheckbox.checked ? "—" : (petCountField.value || "0");
    }

    if (petCountField) {
        petCountField.addEventListener("input", updatePetCounter);
        // При зареждане на страницата
        updatePetCounter();
    }

    function renumberResidents() {
        const residents = residentsContainer.querySelectorAll(".resident");
        residents.forEach((resident, index) => {
            const sup = resident.querySelector("i > sup");
            if (sup) sup.textContent = `${index + 1}`;
            const inputs = resident.querySelectorAll("input");
            if (inputs.length >= 3) {
                inputs[0].setAttribute("name", `adults[${index}].name`);
                inputs[1].setAttribute("name", `adults[${index}].phone`);
                inputs[2].setAttribute("name", `adults[${index}].email`);
            }
        });
    }

    function renumberChildren() {
        const children = childrenContainer.querySelectorAll(".child");
        children.forEach((child, index) => {
            const sup = child.querySelector("i > sup");
            if (sup) sup.textContent = `${index + 1}`;
            const inputs = child.querySelectorAll("input");
            if (inputs.length >= 2) {
                inputs[0].setAttribute("name", `children[${index}].name`);
                inputs[1].setAttribute("name", `children[${index}].age`);
            }
        });
    }

    function attachRemoveEvents() {
        residentsContainer.querySelectorAll(".resident .remove-btn").forEach(btn => {
            btn.removeEventListener("click", removeHandler); // презаписване
            btn.addEventListener("click", removeHandler);
        });

        childrenContainer.querySelectorAll(".child .remove-btn").forEach(btn => {
            btn.removeEventListener("click", removeChildHandler);
            btn.addEventListener("click", removeChildHandler);
        });
    }

    function removeHandler(e) {
        const resident = e.target.closest(".resident");
        if (resident.getAttribute("data-owner-resident") === "true") {
            rememberMe.checked = false;
        }
        resident.remove();
        updateResidentCounter();
        renumberResidents();
    }

    function removeChildHandler(e) {
        const child = e.target.closest(".child");
        child.remove();
        updateChildCounter();
        renumberChildren();
    }

    function addResidentWithData(data) {
        const index = residentsContainer.querySelectorAll(".resident").length;

        const labelName = htmlLang === "en" ? "Full name:" : "Три имена:";
        const labelPhone = htmlLang === "en" ? "Phone number:" : "Тел. номер:";
        const labelEmail = htmlLang === "en" ? "Email:" : "Email:";

        const placeHolderNameLabel = htmlLang === "en" ? "Enter full name..." : "Въведете три имена...";
        const placeHolderPhoneLabel = htmlLang === "en" ? "Enter phone number..." : "Въведете тел. за връзка...";
        const placeHolderEmailLabel = htmlLang === "en" ? "Enter Email..." : "Въведете Email...";

        const html = `
        <div class="resident row" style="display: flex; position: relative;">
            <div style="width: 18px">
                <a class="remove-btn btn btn-sm btn-danger" style="position: absolute; right: 45px; top: 10px;" type="button">✕</a>
            </div>
            <b><i class="fa-solid fa-user text-danger"><sup>${index + 1}</sup></i></b>
            <div class="col-lg-4">
                <fieldset><br>
                    <label><b>${labelName}</b></label>
                    <input name="adults[${index}].name" placeholder="${placeHolderNameLabel}" type="text" value="${data.name || ""}">
                </fieldset>
            </div>
            <div class="col-lg-4">
                <fieldset><br>
                    <label><i class="fa-solid fa-phone"></i> <b>${labelPhone}</b></label>
                    <input name="adults[${index}].phone" placeholder="${placeHolderPhoneLabel}" type="tel" value="${data.phone || ""}">
                </fieldset>
            </div>
            <div class="col-lg-4">
                <fieldset><br>
                    <label><i class="fa-solid fa-envelope"></i> <b>${labelEmail}</b></label>
                    <input name="adults[${index}].email" placeholder="${placeHolderEmailLabel}" type="email" value="${data.email || ""}">
                </fieldset>
            </div>
        </div>
    `;

        residentsContainer.insertAdjacentHTML("beforeend", html);

        if (data.ownerResident) {
            residentsContainer.lastElementChild.setAttribute("data-owner-resident", "true");
        }

        updateResidentCounter();
        renumberResidents();
        attachRemoveEvents();
    }

    function addChildWithData(data) {
        const index = childrenContainer.querySelectorAll(".child").length;

        const labelName = htmlLang === "en" ? "Full name:" : "Три имена:";
        const labelAge = htmlLang === "en" ? "Age:" : "Възраст:";

        const placeHolderNameLabel = htmlLang === "en" ? "Enter name..." : "Въведете имена...";
        const placeHolderAgeLabel = htmlLang === "en" ? "Enter age..." : "Въведете възраст...";

        const html = `
        <div class="child row" style="display: flex; position: relative;">
            <div style="width: 18px">
                <a class="remove-btn btn btn-sm btn-danger" style="position: absolute; right: 45px; top: 10px;" type="button">✕</a>
            </div>
            <b><i class="fa-solid fa-child text-danger"><sup>${index + 1}</sup></i></b>
            <div class="col-lg-6">
                <fieldset><br>
                    <label><b>${labelName}</b></label>
                    <input name="children[${index}].name" placeholder="${placeHolderNameLabel}"  type="text" value="${data.name || ""}">
                </fieldset>
            </div>
            <div class="col-lg-6">
                <fieldset><br>
                    <label><b>${labelAge}</b></label>
                    <input name="children[${index}].age" placeholder="${placeHolderAgeLabel}"  type="text" value="${data.age || ""}">
                </fieldset>
            </div>
        </div>
    `;

        childrenContainer.insertAdjacentHTML("beforeend", html);

        updateChildCounter();
        renumberChildren();
        attachRemoveEvents();
    }

    function matchOwnerWithResident() {
        const ownerNameValue = ownerName.value?.trim();
        const ownerPhoneValue = ownerPhone.value?.trim();
        const ownerEmailValue = ownerEmail.value?.trim();

        const allResidents = residentsContainer.querySelectorAll(".resident");
        for (let resident of allResidents) {
            const inputs = resident.querySelectorAll("input");
            if (inputs.length < 3) continue;
            const rName = inputs[0].value?.trim();
            const rPhone = inputs[1].value?.trim();
            const rEmail = inputs[2].value?.trim();

            if (rName === ownerNameValue && rPhone === ownerPhoneValue && rEmail === ownerEmailValue) {
                resident.setAttribute("data-owner-resident", "true");
                rememberMe.checked = true;
                break;
            }
        }
    }

    if (rememberMe) {
        rememberMe.addEventListener("change", function () {
            if (this.checked) {
                addResidentWithData({
                    name: ownerName.value,
                    phone: ownerPhone.value,
                    email: ownerEmail.value,
                    ownerResident: true
                });
            } else {
                const owner = residentsContainer.querySelector('.resident[data-owner-resident="true"]');
                if (owner) {
                    owner.remove();
                    updateResidentCounter();
                    renumberResidents();
                }
            }
        });

        [ownerName, ownerPhone, ownerEmail].forEach((input) => {
            input.addEventListener("input", () => {
                residentsContainer.querySelectorAll(".resident").forEach(r => r.removeAttribute("data-owner-resident"));
                matchOwnerWithResident();
            });
        });
    }

    if (isNotHabitableCheckbox) {
        isNotHabitableCheckbox.addEventListener("change", function () {
            if (this.checked) {
                rememberMeContainer.style.display = "none";
                residentsAndPetsSection.style.display = "none";
            } else {
                formFields.forEach(field => field.disabled = false);
                addResidentBtn.disabled = false;
                addChildBtn.disabled = false;
                numberOfPetsField.disabled = false;
                rememberMeContainer.style.display = "flex";
                residentsAndPetsSection.style.display = "block";
            }
            updateResidentCounter();
            updateChildCounter();
            updatePetCounter();
        });
    }

    if (addResidentBtn) {
        addResidentBtn.addEventListener("click", () => addResidentWithData({}));
    }

    if (addChildBtn) {
        addChildBtn.addEventListener("click", () => addChildWithData({}));
    }

    // Init
    updateResidentCounter();
    updateChildCounter();
    attachRemoveEvents();
    matchOwnerWithResident();

    if (isNotHabitableCheckbox.checked) {
        rememberMeContainer.style.display = "none";
        residentsAndPetsSection.style.display = "none";
    } else {
        rememberMeContainer.style.display = "flex";
        residentsAndPetsSection.style.display = "block";
    }
});
