package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddGlobalFeeBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.model.events.PropertyApprovalEvent;
import com.example.OurHome.repo.PropertyFeeRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.PropertyFeeService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
     * Creation of first fee. It is by default set to paid and fee amount set to 0.0
     *
     * @param propertyApprovalEvent property approval event
     */
    @Override
    @EventListener(PropertyApprovalEvent.class)
    @Transactional
    public void createFirstFee(PropertyApprovalEvent propertyApprovalEvent) {

        if (propertyApprovalEvent.getProperty().getPropertyFees().isEmpty()) {
            PropertyFee newPropertyFee = new PropertyFee();
            LocalDate now = LocalDate.now();

            newPropertyFee.setFeeAmount(BigDecimal.valueOf(0.0));
            newPropertyFee.setPaid(true);
            newPropertyFee.setPeriodStart(now.withDayOfMonth(1));
            newPropertyFee.setPeriodEnd(now.withDayOfMonth(now.lengthOfMonth()));
            newPropertyFee.setProperty(propertyApprovalEvent.getProperty());
            newPropertyFee.setDescription("Modify this record if old duties available");
            newPropertyFee.setOverpaidAmountStart(BigDecimal.ZERO);
            newPropertyFee.setOverpaidAmountEnd(BigDecimal.ZERO);
            propertyFeeRepository.save(newPropertyFee);
        }
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
        BigDecimal additionalPropertyFee = property.getAdditionalPropertyFee();
        BigDecimal totalMonthlyFee = monthlyFee.add(additionalPropertyFee);

        //if monthly fees in Residential entity are not set, no new fees will be created for the property
        if (totalMonthlyFee.compareTo(BigDecimal.ZERO) > 0) {

            //Calculations of monthly fee in case of overpayment
            if (overpayment.compareTo(BigDecimal.ZERO) > 0) {

                //Creating new monthly fee when overpaid amount > monthly fee
                if (overpayment.compareTo(totalMonthlyFee) > 0) {
                    newPropertyFee.setFeeAmount(totalMonthlyFee);
                    newPropertyFee.setPaid(true);
                    newPropertyFee.setOverpaidAmountStart(overpayment);

                    property.setOverpayment(overpayment.subtract(totalMonthlyFee));
                    propertyRepository.save(property);
                    newPropertyFee.setOverpaidAmountEnd(overpayment.subtract(totalMonthlyFee));
                }

                //Creating new propertyFee when overpaid amount == monthly fee
                if (overpayment.compareTo(totalMonthlyFee) == 0) {
                    newPropertyFee.setFeeAmount(totalMonthlyFee);
                    newPropertyFee.setPaid(true);
                    newPropertyFee.setOverpaidAmountStart(overpayment);

                    property.setOverpayment(BigDecimal.valueOf(0));
                    propertyRepository.save(property);
                    newPropertyFee.setOverpaidAmountEnd(BigDecimal.valueOf(0));
                }

                //Creating new propertyFee when overpaid amount < monthly fee
                if (overpayment.compareTo(totalMonthlyFee) < 0) {
                    newPropertyFee.setFeeAmount(totalMonthlyFee.subtract(overpayment));
                    newPropertyFee.setOverpaidAmountStart(overpayment);

                    property.setOverpayment(BigDecimal.valueOf(0));
                    propertyRepository.save(property);
                    newPropertyFee.setOverpaidAmountEnd(BigDecimal.valueOf(0));
                }
            } else {
                newPropertyFee.setFeeAmount(totalMonthlyFee);
                newPropertyFee.setPaid(false);
                newPropertyFee.setOverpaidAmountStart(BigDecimal.valueOf(0));
                newPropertyFee.setOverpaidAmountEnd(BigDecimal.valueOf(0));
            }

            newPropertyFee.setPeriodStart(now.withDayOfMonth(1));
            newPropertyFee.setPeriodEnd(now.withDayOfMonth(now.lengthOfMonth()));
            newPropertyFee.setProperty(property);
            newPropertyFee.setDescription(now.getMonth() + " " + now.getYear());
            propertyFeeRepository.save(newPropertyFee);

            //send message to property owner
            messageService.newFeeMessageToPropertyOwner(property, property.getTotalMonthlyFee(), checkTotalDueAmount(property.getId()));
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
            propertyFee.setPeriodStart(propertyFeeEditBindingModel.getPeriodStart());
            propertyFee.setPeriodEnd(propertyFeeEditBindingModel.getPeriodEnd());
            propertyFee.setFeeAmount(propertyFeeEditBindingModel.getFeeAmount());
            propertyFee.setPaid(propertyFeeEditBindingModel.isPaid());
            propertyFee.setDescription(propertyFeeEditBindingModel.getDescription());

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
        propertyFee.setManual(true);
        propertyFee.setProperty(property);
        propertyFee.setId(null);
        propertyFee.setOverpaidAmountStart(property.getOverpayment());
        propertyFee.setOverpaidAmountEnd(property.getOverpayment());

        propertyFeeRepository.save(propertyFee);
    }

    @Override
    public OverpaymentBindingModel mapOverPaymentBindingModel(Property property) {
        return modelMapper.map(property, OverpaymentBindingModel.class);
    }

    /**
     * Update overpayment method
     */
    @Override
    public void updateOverpayment(Property property, BigDecimal overPayment) {
        property.setOverpayment(Objects.requireNonNullElseGet(overPayment, () -> BigDecimal.valueOf(0)));
        propertyRepository.save(property);
    }

    /**
     * Update additional property fee method
     */
    @Override
    public void setAdditionalPropertyFee(Property property, BigDecimal additionalPropertyFee) {
        property.setAdditionalPropertyFee(Objects.requireNonNullElseGet(additionalPropertyFee, () -> BigDecimal.valueOf(0)));
        propertyRepository.save(property);
    }

    /**
     * Mark as paid/unpaid
     */
    @Override
    public void changePaymentStatus(PropertyFee propertyFee) {
        propertyFee.setPaid(!propertyFee.isPaid());
        propertyFeeRepository.save(propertyFee);
    }

    /**
     * Global fee add - Adds a fee to every single property in the common Residential entity.
     *
     * @param residentialEntity Residential entity data
     * @param propertyFeeAddGlobalFeeBindingModel add global fee input data
     * @return boolean
     */
    @Override
    public boolean addGlobalFee(ResidentialEntity residentialEntity, PropertyFeeAddGlobalFeeBindingModel propertyFeeAddGlobalFeeBindingModel) {

        if (residentialEntity == null) {
            return false;
        }

        List<Property> properties = residentialEntity.getProperties();
        if (properties.isEmpty()) {
            return false;
        }

        for (Property property : properties) {
            PropertyFee propertyFee = modelMapper.map(propertyFeeAddGlobalFeeBindingModel, PropertyFee.class);
            propertyFee.setManual(true);
            propertyFee.setProperty(property);
            propertyFee.setId(null);
            propertyFee.setOverpaidAmountStart(property.getOverpayment());
            propertyFee.setOverpaidAmountEnd(property.getOverpayment());
            propertyFeeRepository.save(propertyFee);
        }
        return true;
    }

    /**
     * Unpaid property fees sum
     *
     * @param id Property id
     * @return BigDecimal value of all unpaid fees
     */
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

