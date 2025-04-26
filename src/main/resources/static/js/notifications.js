    jQuery(document).ready(function () {
        let hiddenNotifications = []; // Списък със скрити нотификации, ако има повече от 5

        // 🔹 Функция за ограничаване броя на видимите нотификации до 5
        function limitNotifications() {
            let notifications = jQuery(".notification");

            if (notifications.length > 5) {
                notifications.slice(5).each(function () {
                    hiddenNotifications.push(jQuery(this).detach()); // Запазваме излишните нотификации в масив и ги премахваме от DOM
                });
            }
            updateNotificationIcon(); // Обновяваме иконата на камбаната
            updateClearButton(); // Обновяваме бутона "Изчисти всички"
        }

        // 🔹 Функция за обновяване на бутона "Изчисти всички"
        function updateClearButton() {
            let clearBtn = jQuery("#clear-notifications");
            if (jQuery(".notification").length > 0 || hiddenNotifications.length > 0) {
                clearBtn.addClass("visible"); // Показваме бутона, ако има нотификации
            } else {
                clearBtn.removeClass("visible"); // Скриваме бутона, ако няма нотификации
            }
        }

        // 🔹 Функция за обновяване на иконата на камбаната
        function updateNotificationIcon() {
            let bellIcon = jQuery("#toggle-notifications i");
            if (jQuery(".notification").length > 0 || hiddenNotifications.length > 0) {
                bellIcon.removeClass("fa-regular fa-bell text-dark").addClass("fa-regular fa-bell text-danger"); // Променяме камбаната в червена
            } else {
                bellIcon.removeClass("fa-regular fa-bell text-danger").addClass("fa-regular fa-bell text-dark"); // Променяме камбаната в черна
            }
        }

        // 🔹 Функция за затваряне на конкретна нотификация при клик върху "X"
        // Уверяваме се, че обработчикът не се регистрира повече от веднъж
        jQuery(document).off("click", ".notification__close").on("click", ".notification__close", function (e) {
            e.preventDefault();

            let parent = jQuery(this).closest(".notification"); // Намираме родителския елемент (самата нотификация)
            let notificationId = parent.data("notification-id"); // Взимаме ID-то на нотификацията от data атрибут

            // 🔹 Проверяваме дали вече е била изпратена заявка за тази нотификация
            if (!notificationId || jQuery(this).data("processing")) {
                return false; // Предотвратява повторно изпращане
            }

            jQuery(this).data("processing", true); // 🔹 Маркираме заявката като изпратена

            console.log(`🔹 Sending request to delete notification with id ${notificationId}...`);

            markNotificationAsRead(notificationId, () => {
                // 🔹 Когато заявката завърши успешно, изпълняваме анимация и премахване
                parent.animate({ opacity: 0 }, 500, function () {
                    jQuery(this).remove(); // Премахваме нотификацията от DOM

                    // Ако има скрити нотификации, показваме първата от тях
                    if (hiddenNotifications.length > 0) {
                        let nextNotification = hiddenNotifications.shift();
                        jQuery(".notification-container").append(nextNotification.hide().fadeIn(500));
                    }

                    updateNotificationIcon(); // Обновяваме на "Камбанката"
                    updateClearButton(); // Обновяваме бутона "Изчисти всички"
                 // toggleNoNotificationsMessage(); // Показваме съобщението "Няма нови известия"
                });
            });
            return false; // 🔹 Предотвратява допълнителни заявки
        });

    // 🔹 Функция за изпращане на AJAX заявка за маркиране като прочетена
        function markNotificationAsRead(notificationId, callback) {
            if (notificationId) {
                jQuery.ajax({
                    url: `/delete-notification/${notificationId}`,
                    type: "POST",
                    contentType: "application/json",
                    dataType: "text", // 🔹 Очакваме текст, а не JSON
                    success: function (response) {
                        console.log(`✅ Notification ${notificationId} successfully deleted. Response: ${response}`);
                        if (typeof callback === "function") callback(); // Изпълняваме callback само ако има успех
                    },
                    error: function (xhr, status, error) {
                        console.error(`❌ Failed to delete notification ${notificationId}! Status: ${xhr.status}, Response: ${xhr.responseText}`);
                    },
                    complete: function () {
                        jQuery(`.notification__close[data-notification-id='${notificationId}']`).removeData("processing"); // 🔹 Премахваме "processing" маркера след завършване
                    }
                });
            }
        }


        // 🔹 Функция за показване на съобщението "Няма нови известия"
        function toggleNoNotificationsMessage() {
            let noNotificationsMessage = jQuery("#no-notifications-message");

            if (jQuery(".notification").length === 0 && hiddenNotifications.length === 0) {
                noNotificationsMessage.removeClass("hidden").addClass("visible"); // Показваме съобщението
            } else {
                noNotificationsMessage.removeClass("visible").addClass("hidden"); // Скриваме съобщението
            }
        }

        jQuery(document).ready(function () {
            let userId = jQuery("#clear-notifications").data("user-id"); // Взимаме ID-то на потребителя от бутона

            // 🔹 Функция за изчистване на всички нотификации със заявка към контролера
            jQuery("#clear-notifications").click(function () {
                if (!userId) {
                    console.error("❌ User ID is missing!");
                    return;
                }

                jQuery.ajax({
                    url: `/delete-all-notifications/${userId}`, // Изпращаме ID-то на потребителя
                    type: "POST",
                    contentType: "application/json",
                    dataType: "text",
                    success: function (response) {
                        console.log(`✅ All notifications for user ${userId} successfully deleted. Response: ${response}`);

                        // 🔹 Анимираме премахването на всички нотификации
                        jQuery(".notification").fadeOut(500).promise().done(function () {
                            jQuery(".notification").remove(); // Премахваме всички нотификации от DOM
                            hiddenNotifications = []; // Изчистваме скритите нотификации

                            updateNotificationIcon(); // Обновяваме иконата на камбаната
                            updateClearButton(); // Обновяваме бутона "Изчисти всички"
                        //  toggleNoNotificationsMessage(); // Показваме съобщението "Няма нови известия"
                        });
                    },
                    error: function (xhr, status, error) {
                        console.error(`❌ Failed to delete all notifications for user ${userId}! Status: ${xhr.status}, Response: ${xhr.responseText}`);
                    }
                });
            });
        });



        // 🔹 Анимация за плавно показване на нотификациите при зареждане
        jQuery(".notification").each(function (index) {
            jQuery(this).css("opacity", 0).delay(index * 80).animate({opacity: 1}, 400);
        });

        limitNotifications(); // Ограничава броя на видимите нотификации до 5 при зареждане
        updateNotificationIcon(); // Обновява иконата на камбаната при зареждане
        updateClearButton(); // Обновява бутона "Изчисти всички" при зареждане

    // 🔹 Функция за показване/скриване на контейнера с нотификации при натискане на камбаната
        jQuery("#toggle-notifications").click(function (event) {
            let container = jQuery(".notification-container");

            if (container.hasClass("visible")) {
                container.removeClass("visible").addClass("hidden"); // Скриваме контейнера
            } else {
                container.removeClass("hidden").addClass("visible"); // Показваме контейнера
                toggleNoNotificationsMessage(); // Проверяваме дали има нотификации
            }

            event.stopPropagation(); // Спираме разпространението на събитието
        });

        // 🔹 Функция за скриване на контейнера при клик извън него
        jQuery(document).click(function (event) {
            let container = jQuery(".notification-container");
            let toggleButton = jQuery("#toggle-notifications");

            // Проверяваме дали кликът е извън контейнера и бутона
            if (!container.is(event.target) && container.has(event.target).length === 0 &&
                !toggleButton.is(event.target) && toggleButton.has(event.target).length === 0) {
                container.removeClass("visible").addClass("hidden"); // Скриваме контейнера
            }
        });
    });