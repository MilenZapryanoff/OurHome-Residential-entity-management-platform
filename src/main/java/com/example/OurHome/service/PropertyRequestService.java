package com.example.OurHome.service;

import com.example.OurHome.model.Entity.PropertyRequest;

public interface PropertyRequestService {

    PropertyRequest saveRequestToDBAndReturnEntity(PropertyRequest propertyRequest);

    void update(PropertyRequest propertyRequest);

    void invalidateRequest(PropertyRequest propertyRequest);

    PropertyRequest findActivePropertyRequestByNumberAndResidentialEntityId(int propertyNumber, Long residentialEntityId);

    boolean checkForNoActivePropertyRequest(int propertyNumber, Long residentialEntityId);
}
