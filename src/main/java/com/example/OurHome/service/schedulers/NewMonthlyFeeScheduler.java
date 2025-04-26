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

    // This expression triggers the method on the first day of every month at 06:00:00 (intended for PROD)
    @Scheduled(cron = "0 0 6 1 * *")

    // This expression triggers the method on every 50 sec. (intended for TEST)
    //@Scheduled(cron = "*/50 * * * * *")
    public void newMonthlyFee() {

        try {
            List<ResidentialEntity> allResidentialEntities = residentialEntityService.findAllResidentialEntities();

            if (allResidentialEntities.isEmpty()) {
                logService.warn("⚠️[NewMonthlyFeeScheduler] No Condominiums found. Skipping new monthly fees email notification.");
                return;
            }

            List<String> bccEmails = new ArrayList<>();

            for (ResidentialEntity residentialEntity : allResidentialEntities) {

                List<Property> allProperties = residentialEntity.getProperties();
                if (!allProperties.isEmpty()) {

                    for (Property property : allProperties) {
                        if (property.isValidated() && property.isAutoFee()) {
                            propertyFeeService.createPeriodicalMonthlyFee(property);

                            //collecting all property owners emails
                            if (property.getOwner() != null && property.isObtained() && property.getOwner().isEventEmail()) {
                                bccEmails.add(property.getOwner().getEmail());
                            }

                        }
                    }
                }

                //send new fee email to property owner if email list is not empty
                if (!bccEmails.isEmpty()) {
                    emailService.newFeeEmailNotification(bccEmails, residentialEntity);
                }
            }

            logService.info("\uD83D\uDD04[NewMonthlyFeeScheduler] AUTOMATIC FEE CYCLE SUCCESSFULLY EXECUTED!");
        } catch (Exception e) {
            logService.error("❌[NewMonthlyFeeScheduler] FAILED TO EXECUTE AUTOMATIC FEE CYCLE! {}", e);
        }


    }
}
