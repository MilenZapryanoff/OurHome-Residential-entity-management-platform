package com.example.OurHome.service.schedulers;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.service.PropertyFeeService;
import com.example.OurHome.service.PropertyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewMonthlyFeeScheduler {

    private final PropertyService propertyService;
    private final PropertyFeeService propertyFeeService;

    public NewMonthlyFeeScheduler(PropertyService propertyService, PropertyFeeService propertyFeeService) {
        this.propertyService = propertyService;
        this.propertyFeeService = propertyFeeService;
    }

    // This expression triggers the method on the first day of every month at 06:00:00 (intended for PROD)
    //@Scheduled(cron = "0 0 6 1 * *")

    // This expression triggers the method on every 50 sec. (intended for TEST)
    @Scheduled(cron = "*/50 * * * * *")
    public void newMonthlyFee() {

        List<Property> allProperties = propertyService.findAllProperties();

        if (!allProperties.isEmpty()) {

            allProperties.forEach(property -> {
                if (property.isValidated() && property.isAutoFee()) {
                    propertyFeeService.createPeriodicalMonthlyFee(property);
                }
            });
        }
    }
}
