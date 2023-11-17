package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyRegisterBindingModel;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ResidentialEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final ModelMapper modelMapper;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyRepository propertyRepository;
    private final MessageService messageService;


    public PropertyServiceImpl(ModelMapper modelMapper, ResidentialEntityService residentialEntityService, PropertyRepository propertyRepository, MessageService messageService) {
        this.modelMapper = modelMapper;
        this.residentialEntityService = residentialEntityService;
        this.propertyRepository = propertyRepository;
        this.messageService = messageService;
    }

    /**
     * Property creation method
     * Performed by USER
     */
    @Override
    public void newProperty(PropertyRegisterBindingModel propertyRegisterBindingModel, UserEntity loggedUser) {

        Property newProperty = modelMapper.map(propertyRegisterBindingModel, Property.class);
        Long residentialEntityId = propertyRegisterBindingModel.getResidentialEntity();

        ResidentialEntity residentialEntity = residentialEntityService
                .findResidentialEntityById(residentialEntityId)
                .orElse(null);

        newProperty.setResidentialEntity(residentialEntity);
        newProperty.setOwner(loggedUser);
        newProperty.setValidated(false);

        propertyRepository.save(newProperty);

        //sending message to residential entity manager
        messageService.propertyRegistrationMessageToManager(residentialEntity);
    }


    /**
     * Property deletion method
     * Performed by USER or MANAGER
     */
    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {
            propertyRepository.delete(property);

            messageService.propertyDeletedMessage(property, property.getOwner(), property.getResidentialEntity());
        }
    }

    /**
     * Property remove from RE method
     * Performed by MANAGER
     */
    @Override
    public void deletePropertiesWhenResidentRemoved(Long residentId, Long residentialEntityId) {
        List<Property> allUserProperties = propertyRepository.findAllUserProperties(residentId, residentialEntityId);
        propertyRepository.deleteAll(allUserProperties);
    }


    /**
     * Property approval method.
     * Performed by Residential entity MANAGER
     */
    @Override
    public void approveProperty(Long id) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {
            property.setValidated(true);
            propertyRepository.save(property);

            messageService.propertyApprovedMessage(property, property.getOwner(), property.getResidentialEntity());
        }
    }

    /**
     * Property reject method.
     * Performed by Residential entity MANAGER
     */
    @Override
    public void rejectProperty(Long id) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {
            property.setRejected(true);
            propertyRepository.save(property);

            messageService.propertyRejectedMessage(property, property.getOwner(), property.getResidentialEntity());
        }
    }

    @Override
    public Property findPropertyById(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }


    /**
     * Method maps Property to PropertyEditBindingModel used for edit of property data.
     *
     * @param property
     * @return PropertyEditBindingModel
     */
    @Override
    public PropertyEditBindingModel mapPropertyToEditBindingModel(Property property) {

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        if (property != null) {
            propertyEditBindingModel = modelMapper.map(property, PropertyEditBindingModel.class);
        }
        return propertyEditBindingModel;
    }

    /**
     * Method for edit of property data.
     *
     * @param id, propertyEditBindingModel
     */
    @Override
    public void editProperty(Long id, PropertyEditBindingModel propertyEditBindingModel) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {
            property.setNumber(propertyEditBindingModel.getNumber());
            property.setFloor(propertyEditBindingModel.getFloor());
            property.setNumberOfAdults(propertyEditBindingModel.getNumberOfAdults());
            property.setNumberOfChildren(propertyEditBindingModel.getNumberOfChildren());
            property.setNumberOfPets(propertyEditBindingModel.getNumberOfPets());
            property.setNotHabitable(propertyEditBindingModel.isNotHabitable());

            property.setValidated(false);
            property.setRejected(false);
            propertyRepository.save(property);

            //sending message (notification) to manager
            messageService.propertyModificationMessageToManager(property, property.getResidentialEntity());
        }
    }
}
