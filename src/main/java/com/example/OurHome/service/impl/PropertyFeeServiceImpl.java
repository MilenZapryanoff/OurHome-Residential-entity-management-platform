package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.repo.PropertyFeeRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.PropertyFeeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PropertyFeeServiceImpl implements PropertyFeeService {

    private final PropertyFeeRepository propertyFeeRepository;
    private final PropertyRepository propertyRepository;

    public PropertyFeeServiceImpl(PropertyFeeRepository propertyFeeRepository, PropertyRepository propertyRepository) {
        this.propertyFeeRepository = propertyFeeRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    @Transactional
    public void createFirstFee(Property property) {

        PropertyFee newPropertyFee = new PropertyFee();
        LocalDate now = LocalDate.now();

        newPropertyFee.setFeeAmount(BigDecimal.valueOf(0.0));
        newPropertyFee.setPaid(true);
        newPropertyFee.setPeriodStart(now.withDayOfMonth(1));
        newPropertyFee.setPeriodEnd(now.withDayOfMonth(now.lengthOfMonth()));
        newPropertyFee.setProperty(property);
        propertyFeeRepository.save(newPropertyFee);
    }

    @Override
    @Transactional
    public void createNewFee(Property property) {

        PropertyFee newPropertyFee = new PropertyFee();
        LocalDate now = LocalDate.now();

        BigDecimal overpayment = property.getOverpayment();
        BigDecimal monthlyFee = property.getMonthlyFee();

        if (overpayment.compareTo(BigDecimal.ZERO) > 0) {

            if (overpayment.compareTo(monthlyFee) > 0) {
                newPropertyFee.setFeeAmount(BigDecimal.valueOf(0));

                property.setOverpayment(overpayment.subtract(monthlyFee));
                propertyRepository.save(property);
            }
            if (overpayment.compareTo(monthlyFee) == 0) {
                newPropertyFee.setFeeAmount(BigDecimal.valueOf(0));

                property.setOverpayment(BigDecimal.valueOf(0));
                propertyRepository.save(property);
            }
            if (overpayment.compareTo(monthlyFee) < 0) {
                newPropertyFee.setFeeAmount(monthlyFee.subtract(overpayment));

                property.setOverpayment(BigDecimal.valueOf(0));
                propertyRepository.save(property);
            }
        } else {
            newPropertyFee.setFeeAmount(property.getMonthlyFee());
        }

        newPropertyFee.setPaid(false);
        newPropertyFee.setPeriodStart(now.withDayOfMonth(1));
        newPropertyFee.setPeriodEnd(now.withDayOfMonth(now.lengthOfMonth()));
        newPropertyFee.setProperty(property);
        propertyFeeRepository.save(newPropertyFee);
    }
}
