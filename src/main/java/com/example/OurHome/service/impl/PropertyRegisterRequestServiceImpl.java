package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.PropertyRegisterRequest;
import com.example.OurHome.repo.PropertyRegisterRequestRepository;
import com.example.OurHome.service.PropertyRegisterRequestService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyRegisterRequestServiceImpl implements PropertyRegisterRequestService {

    private final PropertyRegisterRequestRepository propertyRegisterRequestRepository;

    public PropertyRegisterRequestServiceImpl(PropertyRegisterRequestRepository propertyRegisterRequestRepository) {
        this.propertyRegisterRequestRepository = propertyRegisterRequestRepository;
    }

    @Override
    @Transactional
    public PropertyRegisterRequest saveRequestToDBAndReturnEntity(PropertyRegisterRequest propertyRegisterRequest) {

        int propertyNumber = propertyRegisterRequest.getNumber();
        Long residentialEntityId = propertyRegisterRequest.getResidentialEntityId();

        //check again if there is no other active request for this property. Cannot exist more than ONE active request!
        if (checkForNoActivePropertyRegisterRequest(propertyNumber, residentialEntityId)) {
            propertyRegisterRequestRepository.save(propertyRegisterRequest);

            return propertyRegisterRequestRepository.findActivePropertyRequestByNumberAndResidentialEntityId(propertyNumber, residentialEntityId);
        }
        return null;
    }

    @Override
    public void update(PropertyRegisterRequest propertyRegisterRequest) {
        propertyRegisterRequestRepository.save(propertyRegisterRequest);
    }

    /**
     * Method for invalidation of pending property requests assigned to this property
     */
    @Override
    public void invalidateRequest(PropertyRegisterRequest propertyRegisterRequest) {
        propertyRegisterRequest.setActive(false);
        propertyRegisterRequestRepository.save(propertyRegisterRequest);
    }

    @Override
    public PropertyRegisterRequest findActivePropertyRequestByNumberAndResidentialEntityId(int propertyNumber, Long residentialEntityId) {
        return propertyRegisterRequestRepository.findActivePropertyRequestByNumberAndResidentialEntityId(propertyNumber, residentialEntityId);
    }

    @Override
    public boolean checkForNoActivePropertyRegisterRequest(int propertyNumber, Long residentialEntityId) {
        return propertyRegisterRequestRepository.countActivePropertyRequestByNumberAndResidentialEntityId(propertyNumber, residentialEntityId) == 0;
    }

    @Override
    public void save(PropertyRegisterRequest propertyRegisterRequest) {
        propertyRegisterRequestRepository.save(propertyRegisterRequest);
    }

    @Override
    public void detachPropertyType(Long propertyTypeId) {
        List<PropertyRegisterRequest> allRequestsByPropertyType = propertyRegisterRequestRepository.findAllRequestsByPropertyType(propertyTypeId);
        allRequestsByPropertyType.forEach(propertyRegisterRequest -> propertyRegisterRequest.setPropertyType(null));
        propertyRegisterRequestRepository.saveAll(allRequestsByPropertyType);
    }

    @Override
    public void deleteAllRegistrationRequests(Long residentialEntityId) {
        List<PropertyRegisterRequest> allRequests = propertyRegisterRequestRepository.findAllByResidentialEntity(residentialEntityId);
        propertyRegisterRequestRepository.deleteAll(allRequests);
    }
}