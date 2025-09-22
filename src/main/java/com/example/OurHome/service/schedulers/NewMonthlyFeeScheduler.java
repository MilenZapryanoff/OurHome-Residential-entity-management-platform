package com.example.OurHome.service.schedulers;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.service.LogService;
import com.example.OurHome.service.PropertyFeeService;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.email.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewMonthlyFeeScheduler {

    private final PropertyService propertyService;
    private final PropertyFeeService propertyFeeService;
    private final ResidentialEntityService residentialEntityService;
    private final EmailService emailService;
    private final LogService logService;

    public NewMonthlyFeeScheduler(PropertyService propertyService, PropertyFeeService propertyFeeService, ResidentialEntityService residentialEntityService, EmailService emailService, LogService logService) {
        this.propertyService = propertyService;
        this.propertyFeeService = propertyFeeService;
        this.residentialEntityService = residentialEntityService;
        this.emailService = emailService;
        this.logService = logService;
    }

    // This expression triggers the method every day at 04:00:00 (intended for PROD)
    //@Scheduled(cron = "0 55 23 * * *", zone = "Europe/Sofia")

    // This expression triggers the method on every 50 sec. (intended for TEST)
    @Scheduled(cron = "*/50 * * * * *")
    public void newMonthlyFee() {
        try {
            List<ResidentialEntity> allResidentialEntities = residentialEntityService.findAllResidentialEntities();

            if (allResidentialEntities.isEmpty()) {
                logService.warn("⚠️[NewMonthlyFeeScheduler] No Condominiums found! Skipping monthly fees generation!");
                return;
            }

            LocalDate today = LocalDate.now();

            for (ResidentialEntity residentialEntity : allResidentialEntities) {

                Integer scheduledDay = null;
                try {
                    scheduledDay = residentialEntity.getFee() != null
                            ? residentialEntity.getFee().getMonthlyFeeDate()
                            : null;
                } catch (Exception e) {
                    logService.error("❌[NewMonthlyFeeScheduler] FAILED TO DETERMINATE scheduledDay FOR CONDOMINIUM ID: " + residentialEntity.getId() + " !", e);
                }

                //пропускаме генерирането на месечни такси за имотите в тази етажна собственост ако датата не съвпада.
                if (!shouldRunToday(today, scheduledDay)) {
                    continue;
                }

                List<String> bccEmails = new ArrayList<>();
                List<Property> allProperties = residentialEntity.getProperties();

                if (allProperties != null && !allProperties.isEmpty()) {
                    for (Property property : allProperties) {
                        if (property.isValidated() && property.isAutoFee()) {
                            propertyFeeService.createPeriodicalMonthlyFee(property);

                            if (property.getOwner() != null
                                    && property.isObtained()
                                    && property.getOwner().isEventEmail()) {
                                bccEmails.add(property.getOwner().getEmail());
                            }
                        }
                    }
                }

                if (!bccEmails.isEmpty()) {
                    emailService.newFeeEmailNotification(bccEmails, residentialEntity);
                }

                logService.info("🔄[NewMonthlyFeeScheduler] AUTOMATIC MONTHLY FEE GENERATION SUCCESSFULLY EXECUTED FOR CONDOMINIUM ID: {} ON {}!",
                        residentialEntity.getId(), today);
            }

        } catch (Exception e) {
            logService.error("❌[NewMonthlyFeeScheduler] FAILED TO EXECUTE AUTOMATIC FEE CYCLE!", e);
        }
    }

    private boolean shouldRunToday(LocalDate today, Integer scheduledDay) {

        if (scheduledDay == null) return false;
        // Нормализиране, ако по някаква причина в БД е попаднало невалидно число
        int safeDay = Math.max(1, Math.min(scheduledDay, 31));
        int lastDay = YearMonth.from(today).lengthOfMonth();
        int effective = Math.min(safeDay, lastDay); // ако е 31, а месецът има 30 → 30; ако февруари има 28 → 28/29
        return today.getDayOfMonth() == effective;
    }

}
