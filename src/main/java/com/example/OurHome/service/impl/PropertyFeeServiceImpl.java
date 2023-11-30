package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyFee.PropertyFeeAddBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.repo.PropertyFeeRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.PropertyFeeService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PropertyFeeServiceImpl implements PropertyFeeService {

    private final PropertyFeeRepository propertyFeeRepository;
    private final PropertyRepository propertyRepository;
    private final ModelMapper modelMapper;
    private final MessageService messageService;

    public PropertyFeeServiceImpl(PropertyFeeRepository propertyFeeRepository, PropertyRepository propertyRepository, ModelMapper modelMapper, MessageService messageService) {
        this.propertyFeeRepository = propertyFeeRepository;
        this.propertyRepository = propertyRepository;
        this.modelMapper = modelMapper;
        this.messageService = messageService;
    }

    /**
     * Find PropertyFee by id
     *
     * @param propertyFeeId property Fee id
     * @return PropertyFee
     */
    @Override
    public PropertyFee findPropertyFeeById(Long propertyFeeId) {
        return propertyFeeRepository.findById(propertyFeeId).orElse(null);
    }

    /**
     * Creation of first fee. It is by default set to paid and fee amount set to 0.
     *
     * @param property Property entity
     */
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

    /**
     * Creation of new fee.
     *
     * @param property a property entity
     */
    @Override
    @Transactional
    public void createMonthlyFee(Property property) {

        PropertyFee newPropertyFee = new PropertyFee();
        LocalDate now = LocalDate.now();

        BigDecimal overpayment = property.getOverpayment();
        BigDecimal monthlyFee = property.getMonthlyFee();

        //if monthly fees in Residential entity are not set, no new fees will be created for the property
        if (monthlyFee.compareTo(BigDecimal.ZERO) > 0) {

            //Calculations of monthly fee in case of overpayment
            if (overpayment.compareTo(BigDecimal.ZERO) > 0) {

                if (overpayment.compareTo(monthlyFee) > 0) {
                    newPropertyFee.setFeeAmount(BigDecimal.valueOf(0));
                    newPropertyFee.setPaid(true);

                    property.setOverpayment(overpayment.subtract(monthlyFee));
                    propertyRepository.save(property);
                }
                if (overpayment.compareTo(monthlyFee) == 0) {
                    newPropertyFee.setFeeAmount(BigDecimal.valueOf(0));
                    newPropertyFee.setPaid(true);

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
                newPropertyFee.setPaid(false);
            }

            newPropertyFee.setPeriodStart(now.withDayOfMonth(1));
            newPropertyFee.setPeriodEnd(now.withDayOfMonth(now.lengthOfMonth()));
            newPropertyFee.setProperty(property);
            newPropertyFee.setDescription(now.getMonth() + " " + now.getYear());
            propertyFeeRepository.save(newPropertyFee);

            //send message to property owner
            messageService.newFeeMessageToPropertyOwner(property, property.getMonthlyFee(), checkTotalDueAmount(property.getId()));
        }
    }

    /**
     * Modification of property fee
     *
     * @param propertyFeeId               property fee id
     * @param propertyFeeEditBindingModel data from frontend
     */
    @Override
    public void modifyFee(Long propertyFeeId, PropertyFeeEditBindingModel propertyFeeEditBindingModel) {

        PropertyFee propertyFee = propertyFeeRepository.findById(propertyFeeId).orElse(null);

        if (propertyFee != null) {
            modelMapper.map(propertyFeeEditBindingModel, propertyFee);

            propertyFeeRepository.save(propertyFee);
        }
    }

    /**
     * @param propertyFee a property fee
     */
    @Override
    public void deleteFee(PropertyFee propertyFee) {
        propertyFeeRepository.delete(propertyFee);
    }

    @Override
    public void addFee(Property property, PropertyFeeAddBindingModel propertyFeeAddBindingModel) {
        PropertyFee propertyFee = modelMapper.map(propertyFeeAddBindingModel, PropertyFee.class);
        propertyFee.setProperty(property);
        propertyFeeRepository.save(propertyFee);
    }

    @Override
    public BigDecimal checkTotalDueAmount(Long id) {
        return propertyFeeRepository.sumOfUnpaidFees(id);
    }

    /**
     * Mapping of PropertyFee to PropertyFeeEditBindingModel
     *
     * @param id property Fee id
     * @return PropertyFeeEditBindingModel
     */
    @Override
    public PropertyFeeEditBindingModel mapPropertyFeeToBindingModel(Long id) {
        PropertyFee propertyFee = propertyFeeRepository.findById(id).orElse(null);

        if (propertyFee != null) {
            return modelMapper.map(propertyFee, PropertyFeeEditBindingModel.class);
        }
        return null;
    }
}

