package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.ResidentialEntity;
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

            newPropertyFee.setFeeAmount(BigDecimal.ZERO);
            newPropertyFee.setFundRepairAmount(BigDecimal.ZERO);
            newPropertyFee.setFundMmAmount(BigDecimal.ZERO);
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
    public void createPeriodicalMonthlyFee(Property property) {

        //TODO: feeAmount in PropertyFee to be separated to 'totalAmount', 'fundRepair' and 'fundMm'

        PropertyFee newMonthlyFee = new PropertyFee();
        LocalDate now = LocalDate.now();

        BigDecimal overpayment = property.getOverpayment();
        BigDecimal fundMm = property.getMonthlyFeeFundMm();
        BigDecimal fundRepair = property.getMonthlyFeeFundRepair();
        BigDecimal additionalPropertyFee = property.getAdditionalPropertyFee();
        BigDecimal totalMonthlyFee = fundMm.add(fundRepair).add(additionalPropertyFee);

        //if totalMonthlyFee for this property is not 0.00, a new monthly fee will be created
        if (totalMonthlyFee.compareTo(BigDecimal.ZERO) > 0) {

            if (overpayment.compareTo(BigDecimal.ZERO) == 0) {
                //Setting of monthly fee if NO overpayment
                setMonthlyFeeWithoutOverpayment(newMonthlyFee, totalMonthlyFee);
            } else {
                //Setting a monthly fee in case of overpayment
                setMonthlyFeeWithOverpayment(property, overpayment, totalMonthlyFee, newMonthlyFee);
            }

            newMonthlyFee.setPeriodStart(now.withDayOfMonth(1));
            newMonthlyFee.setPeriodEnd(now.withDayOfMonth(now.lengthOfMonth()));
            newMonthlyFee.setProperty(property);
            newMonthlyFee.setDescription(now.getMonth() + " " + now.getYear());
            newMonthlyFee.setFundMmAmount(fundMm);
            newMonthlyFee.setFundRepairAmount(fundRepair);
            propertyFeeRepository.save(newMonthlyFee);

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
    public void editMonthlyFee(Long propertyFeeId, PropertyFeeEditBindingModel propertyFeeEditBindingModel) {

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
    public void deleteMonthlyFee(PropertyFee propertyFee) {
        propertyFeeRepository.delete(propertyFee);
    }

    @Override
    public void createSingleFee(Property property, PropertyFeeAddBindingModel propertyFeeAddBindingModel) {
        PropertyFee propertyFee = modelMapper.map(propertyFeeAddBindingModel, PropertyFee.class);
        propertyFee.setManual(true);
        propertyFee.setProperty(property);
        propertyFee.setId(null);
        propertyFee.setOverpaidAmountStart(property.getOverpayment());
        propertyFee.setOverpaidAmountEnd(property.getOverpayment());

        propertyFeeRepository.save(propertyFee);
    }

    /**
     * Global fee add - Adds a fee to every single property in the common Residential entity.
     *
     * @param residentialEntity                   Residential entity data
     * @param propertyFeeAddGlobalFeeBindingModel add global fee input data
     * @return boolean
     */
    @Override
    public boolean createMassFee(ResidentialEntity residentialEntity, PropertyFeeAddGlobalFeeBindingModel propertyFeeAddGlobalFeeBindingModel) {

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
     * Mark as paid/unpaid
     */
    @Override
    public void changePaymentStatus(PropertyFee propertyFee) {
        propertyFee.setPaid(!propertyFee.isPaid());
        propertyFeeRepository.save(propertyFee);
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

    /**
     * Method for setting monthlyFee params in case of Overpayment.
     *
     * @param totalMonthlyFee Total Monthly fee
     * @param newMonthlyFee   NewMonthlyFee
     */
    private static void setMonthlyFeeWithoutOverpayment(PropertyFee newMonthlyFee, BigDecimal totalMonthlyFee) {
        newMonthlyFee.setFeeAmount(totalMonthlyFee);
        newMonthlyFee.setPaid(false);
        newMonthlyFee.setOverpaidAmountStart(BigDecimal.valueOf(0));
        newMonthlyFee.setOverpaidAmountEnd(BigDecimal.valueOf(0));
    }

    /**
     * Method for setting monthlyFee params in case of Overpayment.
     *
     * @param property        property fee id
     * @param overpayment     Overpaid amount
     * @param totalMonthlyFee Total Monthly fee
     * @param newMonthlyFee   NewMonthlyFee
     */
    private void setMonthlyFeeWithOverpayment(Property property, BigDecimal overpayment,
                                              BigDecimal totalMonthlyFee, PropertyFee newMonthlyFee) {

        //Setting new propertyFee when overpaid amount < total monthly fee
        if (overpayment.compareTo(totalMonthlyFee) < 0) {
            newMonthlyFee.setFeeAmount(totalMonthlyFee.subtract(overpayment));
            newMonthlyFee.setPaid(false);

            property.setOverpayment(BigDecimal.valueOf(0));
            newMonthlyFee.setOverpaidAmountEnd(BigDecimal.valueOf(0));
        } else {
            //Setting new propertyFee when overpaid amount == total monthly fee
            if (overpayment.compareTo(totalMonthlyFee) == 0) {
                property.setOverpayment(BigDecimal.valueOf(0));
                newMonthlyFee.setOverpaidAmountEnd(BigDecimal.valueOf(0));
            }
            //Setting new monthly fee when overpaid amount > total  monthly fee
            else if (overpayment.compareTo(totalMonthlyFee) > 0) {
                property.setOverpayment(overpayment.subtract(totalMonthlyFee));
                newMonthlyFee.setOverpaidAmountEnd(overpayment.subtract(totalMonthlyFee));
            }

            newMonthlyFee.setFeeAmount(totalMonthlyFee);
            newMonthlyFee.setPaid(true);
        }
        newMonthlyFee.setOverpaidAmountStart(overpayment);
        propertyRepository.save(property);
    }
}

