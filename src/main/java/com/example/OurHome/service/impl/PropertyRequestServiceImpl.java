package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.PropertyRequest;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.repo.PropertyRequestRepository;
import com.example.OurHome.service.PropertyRequestService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PropertyRequestServiceImpl implements PropertyRequestService {

    private final PropertyRequestRepository propertyRequestRepository;
    private final PropertyRepository propertyRepository;

    public PropertyRequestServiceImpl(PropertyRequestRepository propertyRequestRepository, PropertyRepository propertyRepository) {
        this.propertyRequestRepository = propertyRequestRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    @Transactional
    public PropertyRequest saveRequestToDBAndReturnEntity(PropertyRequest propertyRequest) {

        int propertyNumber = propertyRequest.getNumber();
        Long residentialEntityId = propertyRequest.getResidentialEntityId();

        //check again if there is no other active request for this property. Cannot exist more than ONE active request.
        if (checkForNoActivePropertyRequest( propertyNumber, residentialEntityId)) {
            propertyRequestRepository.save(propertyRequest);

            return propertyRequestRepository.findActivePropertyRequestByNumberAndResidentialEntityId(propertyNumber, residentialEntityId);
        }
        return null;
    }

    @Override
    public void update(PropertyRequest propertyRequest) {
        propertyRequestRepository.save(propertyRequest);
    }

    /**
     * Method for invalidation of pending property requests assigned to this property
     */
    @Override
    public void invalidateRequest(PropertyRequest propertyRequest) {
        propertyRequest.setActive(false);
        propertyRequestRepository.save(propertyRequest);
    }

    @Override
    public PropertyRequest findActivePropertyRequestByNumberAndResidentialEntityId(int propertyNumber, Long residentialEntityId) {
        return propertyRequestRepository.findActivePropertyRequestByNumberAndResidentialEntityId(propertyNumber, residentialEntityId);
    }

    @Override
    public boolean checkForNoActivePropertyRequest(int propertyNumber, Long residentialEntityId) {
        return propertyRequestRepository.countActivePropertyRequestByNumberAndResidentialEntityId(propertyNumber, residentialEntityId) == 0;
    }
}