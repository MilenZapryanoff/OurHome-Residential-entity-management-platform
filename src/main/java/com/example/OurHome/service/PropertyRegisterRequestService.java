package com.example.OurHome.service;

import com.example.OurHome.model.Entity.PropertyRegisterRequest;
import com.example.OurHome.model.Entity.ResidentialEntity;

public interface PropertyRegisterRequestService {

    PropertyRegisterRequest saveRequestToDBAndReturnEntity(PropertyRegisterRequest propertyRegisterRequest);

    void update(PropertyRegisterRequest propertyRegisterRequest);

    void invalidateRequest(PropertyRegisterRequest propertyRegisterRequest);

    PropertyRegisterRequest findActivePropertyRequestByNumberAndResidentialEntityId(int propertyNumber, Long residentialEntityId);

    boolean checkForNoActivePropertyRegisterRequest(int propertyNumber, Long residentialEntityId);

    void save(PropertyRegisterRequest propertyRegisterRequest);

    void detachPropertyType(Long propertyTypeId);
}
