package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Fee;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.Fee.FeeEditBindingModel;
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
     * Default fee creating - 0.00 values set.
     * @param newResidentialEntity Residential Entity of the new fee
     * @return Fee
     */
    @Override
    public Fee createFee(ResidentialEntity newResidentialEntity) {

        Fee fee = new Fee();
        fee.setFixedFeeHabitable(BigDecimal.valueOf(0.00));
        fee.setAdultFee(BigDecimal.valueOf(0.0));
        fee.setChildFee(BigDecimal.valueOf(0.0));
        fee.setPetFee(BigDecimal.valueOf(0.0));
        fee.setAdditionalFeeHabitable(BigDecimal.valueOf(0.0));
        fee.setFixedFeeNonHabitable(BigDecimal.valueOf(0.0));
        fee.setAdditionalFeeNonHabitable(BigDecimal.valueOf(0.0));
        feeRepository.save(fee);

        return fee;
    }

    /**
     * Individual property monthly fee calculation method
     * @param residentialEntity Residential entity
     * @param property Property
     * @return BigDecimal value
     */
    @Override
    public BigDecimal calculateMonthlyFee(ResidentialEntity residentialEntity, Property property) {

        Fee fee = residentialEntity.getFee();
        if (property.isNotHabitable()) {
            BigDecimal fixedFeeNonHabitable = fee.getFixedFeeNonHabitable();
            BigDecimal additionalFeeNonHabitable = fee.getAdditionalFeeNonHabitable();

            return fixedFeeNonHabitable.add(additionalFeeNonHabitable);
        }
        int numberOfAdults = Integer.parseInt(property.getNumberOfAdults());
        int numberOfChildren = Integer.parseInt(property.getNumberOfChildren());
        int numberOfPets = Integer.parseInt(property.getNumberOfPets());

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
     * Residential entity Fee modification method.
     * @param residentialEntity Residential entity
     * @param feeEditBindingModel FeeEditBindingModel carries data form user input
     */
    @Override
    @Transactional
    public void changeFee(ResidentialEntity residentialEntity, FeeEditBindingModel feeEditBindingModel) {
        Fee fee = residentialEntity.getFee();
        if (fee != null) {
            modelMapper.map(feeEditBindingModel, fee);
            feeRepository.save(fee);
            //Applying new fees to all properties in this residential entity
            updatePropertyFees(residentialEntity);
        }
    }

    /**
     * Mapping of Fee to FeeEditBindingModel
     * @param fee Fee
     * @return FeeEditBindingModel
     */
    @Override
    public FeeEditBindingModel mapFeeToBindingModel(Fee fee) {
        return modelMapper.map(fee, FeeEditBindingModel.class);
    }

    /**
     * Private method for applying new set Fee to all properties in this Residential entity
     * @param residentialEntity Residential entity
     */
    private void updatePropertyFees(ResidentialEntity residentialEntity) {
        List<Property> properties = residentialEntity.getProperties();
        for (Property property : properties) {
            if (property.isValidated()) {
                BigDecimal calculatedMonthlyFee = calculateMonthlyFee(residentialEntity, property);
                property.setMonthlyFee(calculatedMonthlyFee);
                propertyRepository.save(property);
            }
        }
    }
}
