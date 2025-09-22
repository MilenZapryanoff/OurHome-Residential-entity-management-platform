package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.Resident;
import com.example.OurHome.model.dto.BindingModels.AddressBook.AdultBindingModel;
import com.example.OurHome.model.dto.BindingModels.AddressBook.ChildBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.repo.ResidentsRepository;
import com.example.OurHome.service.ResidentsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidentsServiceImpl implements ResidentsService {

    private final ResidentsRepository residentsRepository;

    public ResidentsServiceImpl(ResidentsRepository residentsRepository, PropertyRepository propertyRepository) {
        this.residentsRepository = residentsRepository;
    }

    /**
     * Address book update main method
     *
     * @param property                 property entity
     * @param ownerFullName            owners full name
     * @param ownerPhone               owners phone
     * @param ownerEmail               owners email
     * @param propertyEditBindingModel contains data from submit form
     */

    @Override
    public void updateAddressBook(Property property, String ownerFullName, String ownerPhone, String ownerEmail, PropertyEditBindingModel propertyEditBindingModel) {

        //processing address book owners data
        processOwnerData(property, ownerFullName, ownerPhone, ownerEmail);
        //processing address book residents data
        processResidentsData(property, propertyEditBindingModel);
    }

    private void processOwnerData(Property property, String ownerFullName, String ownerPhone, String ownerEmail) {

        if (ownerFullName.isEmpty() && ownerPhone.isEmpty() && ownerEmail.isEmpty()) {
            // deleting owner if blank fields submitted
            deleteOwner(property);
        } else {
            // updating/inserting owner
            updateOwner(property, ownerFullName, ownerPhone, ownerEmail);
        }
    }

    private void deleteOwner(Property property) {
        property.getResidents().stream().filter(Resident::isOwner).findFirst().ifPresent(resident -> property.getResidents().removeIf(Resident::isOwner));
    }

    private void updateOwner(Property property, String ownerFullName, String ownerPhone, String ownerEmail) {

        List<Resident> residents = property.getResidents();

        Resident currentOwner = residents.stream().filter(Resident::isOwner).findFirst().orElse(null);

        if (currentOwner != null) {
            currentOwner.setName(ownerFullName);
            currentOwner.setPhone(ownerPhone);
            currentOwner.setEmail(ownerEmail);
        } else {
            Resident newOwner = new Resident();
            newOwner.setName(ownerFullName);
            newOwner.setPhone(ownerPhone);
            newOwner.setEmail(ownerEmail);
            newOwner.setAdult(true);
            newOwner.setOwner(true);
            newOwner.setProperty(property);

            residents.add(newOwner);
        }
    }

    private void processResidentsData(Property property, PropertyEditBindingModel propertyEditBindingModel) {

        // Remove all existing residents
        property.getResidents().removeIf(resident -> !resident.isOwner());

        if (!property.isNotHabitable()) {
            //Insert new adult's data
            updateAdultResidents(property, propertyEditBindingModel.getAdults());
            //Insert new children's data
            updateChildrenResidents(property, propertyEditBindingModel.getChildren());
        }
        residentsRepository.saveAll(property.getResidents());
    }

    private void updateAdultResidents(Property property, List<AdultBindingModel> adults) {

        // Adding new adults
        adults.forEach(adult -> {
            Resident resident = new Resident();
            resident.setName(adult.getName() != null && !adult.getName().isBlank() ? adult.getName() : "Неизвестно");
            resident.setPhone(adult.getPhone());
            resident.setEmail(adult.getEmail());
            resident.setAdult(true);
            resident.setOwner(false);
            resident.setProperty(property);
            property.getResidents().add(resident);
        });
    }

    private void updateChildrenResidents(Property property, List<ChildBindingModel> children) {

        // Adding new adults
        children.forEach(adult -> {
            Resident resident = new Resident();
            resident.setName(adult.getName() != null && !adult.getName().isBlank() ? adult.getName() : "Неизвестно");
            resident.setAge(adult.getAge());
            resident.setAdult(false);
            resident.setOwner(false);
            resident.setProperty(property);
            property.getResidents().add(resident);
        });
    }
}
