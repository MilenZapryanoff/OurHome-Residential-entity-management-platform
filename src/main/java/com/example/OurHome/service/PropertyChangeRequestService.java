package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyChangeRequest;
import com.example.OurHome.model.Entity.ResidentialEntity;

public interface PropertyChangeRequestService {

    PropertyChangeRequest saveRequestToDBAndReturnEntity(PropertyChangeRequest propertyChangeRequest);

    boolean checkForNoActivePropertyChangeRequest(int propertyNumber, Long residentialEntityId);

    void save(PropertyChangeRequest existingPropertyChangeRequest);

    void invalidateRequest(PropertyChangeRequest propertyChangeRequest);

    void detachPropertyType(Long propertyTypeId);

    void deleteAllRegistrationRequests(Long residentialEntityId);
}
