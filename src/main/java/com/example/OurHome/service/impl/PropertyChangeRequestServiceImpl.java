package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.PropertyChangeRequest;
import com.example.OurHome.repo.PropertyChangeRequestRepository;
import com.example.OurHome.service.PropertyChangeRequestService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyChangeRequestServiceImpl implements PropertyChangeRequestService {

    private final PropertyChangeRequestRepository propertyChangeRequestRepository;

    public PropertyChangeRequestServiceImpl(PropertyChangeRequestRepository propertyChangeRequestRepository) {
        this.propertyChangeRequestRepository = propertyChangeRequestRepository;
    }


    @Override
    @Transactional
    public PropertyChangeRequest saveRequestToDBAndReturnEntity(PropertyChangeRequest propertyChangeRequest) {

        int propertyNumber = propertyChangeRequest.getNumber();
        Long residentialEntityId = propertyChangeRequest.getResidentialEntityId();

        //check again if there is no other active request for this property. Cannot exist more than ONE active request.
        if (checkForNoActivePropertyChangeRequest( propertyNumber, residentialEntityId)) {
            propertyChangeRequestRepository.save(propertyChangeRequest);

            return propertyChangeRequestRepository.findActivePropertyRequestByNumberAndResidentialEntityId(propertyNumber, residentialEntityId);
        }
        return null;
    }

    @Override
    public boolean checkForNoActivePropertyChangeRequest(int propertyNumber, Long residentialEntityId) {
        return propertyChangeRequestRepository.countActivePropertyRequestByNumberAndResidentialEntityId(propertyNumber, residentialEntityId) == 0;
    }

    @Override
    public void save(PropertyChangeRequest existingPropertyChangeRequest) {
        propertyChangeRequestRepository.save(existingPropertyChangeRequest);
    }

    /**
     * Method for invalidation of pending property change requests assigned to this property
     */
    @Override
    public void invalidateRequest(PropertyChangeRequest propertyChangeRequest) {
        propertyChangeRequest.setActive(false);
        propertyChangeRequestRepository.save(propertyChangeRequest);
    }

    @Override
    public void detachPropertyType(Long propertyTypeId) {
        List<PropertyChangeRequest> allRequestsByPropertyType = propertyChangeRequestRepository.findAllRequestsByPropertyType(propertyTypeId);
        allRequestsByPropertyType.forEach(propertyChangeRequest -> propertyChangeRequest.setPropertyType(null));
        propertyChangeRequestRepository.saveAll(allRequestsByPropertyType);
    }
}
