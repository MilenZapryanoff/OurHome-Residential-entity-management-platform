package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Fee;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Fee.FeeEditBindingModel;
import com.example.OurHome.repo.FeeRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.FeeService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FeeServiceImpl implements FeeService {
    private final FeeRepository feeRepository;
    private final PropertyRepository propertyRepository;
    private final ModelMapper modelMapper;
    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(0.00);

    public FeeServiceImpl(FeeRepository feeRepository, PropertyRepository propertyRepository, ModelMapper modelMapper) {
        this.feeRepository = feeRepository;
        this.propertyRepository = propertyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Fee findById(Long id) {
        return feeRepository.findById(id).orElse(null);
    }

    /**
     * New fee creation method.
     * Default values set to - 0.00.
     *
     * @param newResidentialEntity Condominium of the new fee
     * @return Fee
     */
    @Override
    public Fee createFee(ResidentialEntity newResidentialEntity) {

        Fee fee = new Fee();
        fee.setFixedFeeHabitable(DEFAULT_AMOUNT);
        fee.setAdultFee(DEFAULT_AMOUNT);
        fee.setChildFee(DEFAULT_AMOUNT);
        fee.setPetFee(DEFAULT_AMOUNT);
        fee.setAdditionalFeeHabitable(DEFAULT_AMOUNT);
        fee.setFixedFeeNonHabitable(DEFAULT_AMOUNT);
        fee.setAdditionalFeeNonHabitable(DEFAULT_AMOUNT);
        fee.setFundRepairHabitable(DEFAULT_AMOUNT);
        fee.setFundRepairNonHabitable(DEFAULT_AMOUNT);
        fee.setMonthlyFeeDate(01);
        feeRepository.save(fee);

        return fee;
    }

    /**
     * Calculation of Fund Management and Maintenance amount.
     *
     * @param residentialEntity Condominium
     * @param property          Property
     * @return BigDecimal value
     */
    @Override
    public BigDecimal calculateFundMm(ResidentialEntity residentialEntity, Property property) {

        Fee fee = residentialEntity.getFee();

        //in case of non-habitable property
        if (property.isNotHabitable()) {
            BigDecimal fixedFeeNonHabitable = fee.getFixedFeeNonHabitable();
            BigDecimal additionalFeeNonHabitable = fee.getAdditionalFeeNonHabitable();

            return fixedFeeNonHabitable.add(additionalFeeNonHabitable);
        }

        //in case of habitable property
        int numberOfAdults = property.getNumberOfAdults();
        int numberOfChildren = property.getNumberOfChildren();
        int numberOfPets = property.getNumberOfPets();

        BigDecimal fixedFeeHabitable = fee.getFixedFeeHabitable();
        BigDecimal adultFee = fee.getAdultFee();
        BigDecimal childFee = fee.getChildFee();
        BigDecimal petFee = fee.getPetFee();
        BigDecimal additionalFeeHabitable = fee.getAdditionalFeeHabitable();

        return fixedFeeHabitable
                .add(adultFee.multiply(BigDecimal.valueOf(numberOfAdults)))
                .add(childFee.multiply(BigDecimal.valueOf(numberOfChildren)))
                .add(petFee.multiply(BigDecimal.valueOf(numberOfPets)))
                .add(additionalFeeHabitable);
    }

    /**
     * Calculation of Fund Repair amount.
     * This method is used to calculate/recalculate the fund Repair amount after changes of
     * entities that causes need of recalculation of property fees.
     * Method returns the fund Repaid amount calculated according to the current settings.
     *
     * @param residentialEntity RE entity
     * @param property          Property entity
     * @return BigDecimal
     */
    @Override
    public BigDecimal calculateFundRepair(ResidentialEntity residentialEntity, Property property) {

        Fee fee = residentialEntity.getFee();
        //In NO propertyType set for this property (default state)
        if (property.getPropertyType() == null) {
            return property.isNotHabitable() ? fee.getFundRepairNonHabitable() : fee.getFundRepairHabitable();
            //In case of propertyType set for this property (different from default)
        } else {
            return property.isNotHabitable() ? property.getPropertyType().getFundRepairNotHabitable() : property.getPropertyType().getFundRepairHabitable();
        }
    }

    /**
     * Calculation of Fund Repair amount.
     * This method is used in cases of property edit -> changing property type.
     * Method returns the new fund Repaid amount according to property state and new property type set.
     *
     * @param property        Property entity
     * @param newPropertyType This is the new propertyType that will be set for this property
     * @return BigDecimal
     */
    @Override
    public BigDecimal calculateNewFundRepair(Property property, PropertyType newPropertyType) {

        Fee fee = property.getResidentialEntity().getFee();
        //In case of setting new PropertyType to null (removing property type)
        if (newPropertyType == null) {
            return property.isNotHabitable() ? fee.getFundRepairNonHabitable() : fee.getFundRepairHabitable();
            //In case of setting new PropertyType to an existing property type (adding/changing property type)
        } else {
            return property.isNotHabitable() ? newPropertyType.getFundRepairNotHabitable() : newPropertyType.getFundRepairHabitable();
        }
    }

    /**
     * Condominium Fee modification method.
     *
     * @param residentialEntity   Condominium
     * @param feeEditBindingModel FeeEditBindingModel carries data form user input
     */
    @Override
    @Transactional
    public void changeFee(ResidentialEntity residentialEntity, FeeEditBindingModel feeEditBindingModel) {
        Fee fee = residentialEntity.getFee();
        if (fee != null) {
            modelMapper.map(feeEditBindingModel, fee);
            feeRepository.save(fee);

            applyNewFeesToAllProperties(residentialEntity);
        }
    }

    /**
     * Mapping of Fee to FeeEditBindingModel
     *
     * @param fee Fee
     * @return FeeEditBindingModel
     */
    @Override
    public FeeEditBindingModel mapFeeToBindingModel(Fee fee) {
        return modelMapper.map(fee, FeeEditBindingModel.class);
    }

    /**
     * Private method for applying new set Fees to all properties in RE.
     * This method is used when changing Condominium monthly fees settings.
     *
     * @param residentialEntity Condominium
     */
    private void applyNewFeesToAllProperties(ResidentialEntity residentialEntity) {

        List<Property> properties = residentialEntity.getProperties();

        for (Property property : properties) {
            if (property.isValidated()) {

                BigDecimal FundMm = calculateFundMm(residentialEntity, property);
                BigDecimal FundRepair = calculateFundRepair(residentialEntity, property);

                property.setMonthlyFeeFundMm(FundMm);
                property.setMonthlyFeeFundRepair(FundRepair);
                property.setTotalMonthlyFee(FundMm.add(FundRepair).add(property.getAdditionalPropertyFee()));

                propertyRepository.save(property);
            }
        }
    }
}
