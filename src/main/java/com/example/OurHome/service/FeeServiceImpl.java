package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Fee;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.repo.FeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;

    public FeeServiceImpl(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

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
}
