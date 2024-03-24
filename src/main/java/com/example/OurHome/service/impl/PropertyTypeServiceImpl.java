package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.PropertyType.PropertyTypeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyType.PropertyTypeEditBindingModel;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.repo.PropertyTypeRepository;
import com.example.OurHome.service.FeeService;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.PropertyTypeService;
import com.example.OurHome.service.ResidentialEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PropertyTypeServiceImpl implements PropertyTypeService {

    private final ModelMapper modelMapper;
    private final PropertyTypeRepository propertyTypeRepository;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;
    private final FeeService feeService;

    public PropertyTypeServiceImpl(ModelMapper modelMapper, PropertyTypeRepository propertyTypeRepository, ResidentialEntityService residentialEntityService, PropertyService propertyService, PropertyRepository propertyRepository, FeeService feeService) {
        this.modelMapper = modelMapper;
        this.propertyTypeRepository = propertyTypeRepository;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
        this.propertyRepository = propertyRepository;
        this.feeService = feeService;
    }

    /**
     * Property type add method.
     * Performed by Residential entity MANAGER.
     * @param id                           property type id
     * @param propertyTypeAddBindingModel data input from user (manager)
     */
    @Override
    public boolean addPropertyType(Long id, PropertyTypeAddBindingModel propertyTypeAddBindingModel) {

        ResidentialEntity residentialEntity = residentialEntityService.findResidentialEntityById(id).orElse(null);

        if (residentialEntity != null) {
            PropertyType newPropertyType = new PropertyType();

            newPropertyType.setDescription(propertyTypeAddBindingModel.getDescription());
            newPropertyType.setTotalFlatSpace(propertyTypeAddBindingModel.getTotalFlatSpace() == null ? BigDecimal.ZERO : propertyTypeAddBindingModel.getTotalFlatSpace());
            newPropertyType.setCommonPartsPercentage(propertyTypeAddBindingModel.getCommonPartsPercentage() == null ? BigDecimal.ZERO : propertyTypeAddBindingModel.getCommonPartsPercentage());
            newPropertyType.setFundRepairHabitable(BigDecimal.ZERO);
            newPropertyType.setFundRepairNotHabitable(BigDecimal.ZERO);
            newPropertyType.setResidentialEntity(residentialEntity);
            propertyTypeRepository.save(newPropertyType);

            return true;
        } else return false;
    }

    /**
     * Property type edit method.
     * Performed by Residential entity MANAGER.
     *
     * @param id                           property type id
     * @param propertyTypeEditBindingModel data input from user (manager)
     */
    @Override
    public boolean editPropertyType(Long id, PropertyTypeEditBindingModel propertyTypeEditBindingModel) {

        PropertyType propertyType = propertyTypeRepository.findById(id).orElse(null);

        if (propertyType != null) {
            propertyType.setDescription(propertyTypeEditBindingModel.getDescription());
            propertyType.setCommonPartsPercentage(propertyTypeEditBindingModel.getCommonPartsPercentage());
            propertyType.setTotalFlatSpace(propertyTypeEditBindingModel.getTotalFlatSpace());
            propertyType.setFundRepairHabitable(propertyTypeEditBindingModel.getFundRepairHabitable() == null ? BigDecimal.ZERO : propertyTypeEditBindingModel.getFundRepairHabitable());
            propertyType.setFundRepairNotHabitable(propertyTypeEditBindingModel.getFundRepairNotHabitable() == null ? BigDecimal.ZERO : propertyTypeEditBindingModel.getFundRepairNotHabitable());
            propertyTypeRepository.save(propertyType);

            recalculatePropertyFeeComponents(propertyType.getResidentialEntity());
            return true;
        }
        return false;
    }

    /**
     * Property type delete method.
     * Performed by Residential entity MANAGER.
     *
     * @param id property type id
     */
    @Override
    public void deletePropertyType(Long id) {

        PropertyType propertyType = propertyTypeRepository.findById(id).orElse(null);
        List<Property> properties = propertyService.findAllPropertiesByPropertyType(propertyType);

        if (!properties.isEmpty()) {
            for (Property property : properties) {
                property.setPropertyType(null);
                propertyRepository.save(property);
            }
        }

        assert propertyType != null;
        propertyTypeRepository.delete(propertyType);

        recalculatePropertyFeeComponents(propertyType.getResidentialEntity());
    }

    @Override
    public ResidentialEntity findResidentialEntityByPropertyType(Long id) {
        return propertyTypeRepository.findResidentialEntityByPropertyTypeId(id);
    }

    @Override
    public PropertyTypeEditBindingModel mapPropertyTypeToEditBindingModel(Long id) {
        PropertyType propertyType = propertyTypeRepository.findById(id).orElse(null);
        return modelMapper.map(propertyType, PropertyTypeEditBindingModel.class);
    }

    @Override
    public PropertyType findById(Long propertyTypeId) {
        return propertyTypeRepository.findById(propertyTypeId).orElse(null);
    }

    /**
     * Private class method for recalculation of fee components (Fund MM, Fund Repair, Total monthly fee)
     * of all properties in the RE
     * Method used when changes to property type is edited or deleted.
     *
     * @param residentialEntity     RE entity
     */
    private void recalculatePropertyFeeComponents(ResidentialEntity residentialEntity) {

        List<Property> allPropertiesByResidentialEntity = propertyRepository.findAllPropertiesByResidentialEntity(residentialEntity.getId());
        for (Property property : allPropertiesByResidentialEntity) {

            BigDecimal fundRepairComponent = feeService.calculateFundRepair(property.getResidentialEntity(), property);
            BigDecimal fundMmComponent = feeService.calculateFundMm(property.getResidentialEntity(), property);

            property.setMonthlyFeeFundRepair(fundRepairComponent);
            property.setMonthlyFeeFundMm(fundMmComponent);
            property.setTotalMonthlyFee(fundMmComponent.add(fundRepairComponent).add(property.getAdditionalPropertyFee()));

            propertyRepository.save(property);
        }
    }
}
