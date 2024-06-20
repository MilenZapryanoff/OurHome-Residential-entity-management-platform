package com.example.OurHome.service;

import com.example.OurHome.model.Entity.PropertyChangeRequest;

public interface PropertyChangeRequestService {

    PropertyChangeRequest saveRequestToDBAndReturnEntity(PropertyChangeRequest propertyChangeRequest);

    boolean checkForNoActivePropertyChangeRequest(int propertyNumber, Long residentialEntityId);

    void save(PropertyChangeRequest existingPropertyChangeRequest);

    void invalidateRequest(PropertyChangeRequest propertyChangeRequest);

    void detachPropertyType(Long propertyTypeId);

    void deleteAllRegistrationRequests(Long residentialEntityId);

    void markChangeRequestAsRejected(PropertyChangeRequest propertyChangeRequest);
}
