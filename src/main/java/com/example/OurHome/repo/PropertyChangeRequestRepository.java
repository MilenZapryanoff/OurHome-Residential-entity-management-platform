package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.PropertyChangeRequest;
import com.example.OurHome.model.Entity.PropertyRegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyChangeRequestRepository extends JpaRepository<PropertyChangeRequest, Long> {

    @Query("SELECT pcr FROM property_change_requests pcr where pcr.number=:number AND pcr.residentialEntityId=:residentialEntityId and pcr.active = true")
    PropertyChangeRequest findActivePropertyRequestByNumberAndResidentialEntityId(int number, Long residentialEntityId);

    @Query("SELECT COUNT(pcr) FROM property_change_requests pcr where pcr.number=:number AND pcr.residentialEntityId=:residentialEntityId and pcr.active = true")
    int countActivePropertyRequestByNumberAndResidentialEntityId(int number, Long residentialEntityId);

    @Query("SELECT pcr FROM property_change_requests pcr where pcr.propertyType.id=:propertyTypeId")
    List<PropertyChangeRequest> findAllRequestsByPropertyType(Long propertyTypeId);

    @Query("SELECT pcr FROM property_change_requests pcr where pcr.residentialEntityId=:residentialEntityId")
    List<PropertyChangeRequest> findAllByResidentialEntity(Long residentialEntityId);
}
