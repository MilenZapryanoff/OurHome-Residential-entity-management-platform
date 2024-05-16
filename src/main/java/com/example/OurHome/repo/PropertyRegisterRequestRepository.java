package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.PropertyChangeRequest;
import com.example.OurHome.model.Entity.PropertyRegisterRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRegisterRequestRepository extends JpaRepository<PropertyRegisterRequest, Long> {

    @Query("SELECT prr FROM property_register_requests prr where prr.number=:number AND prr.residentialEntityId=:residentialEntityId and prr.active = true")
    PropertyRegisterRequest findActivePropertyRequestByNumberAndResidentialEntityId(int number, Long residentialEntityId);

    @Query("SELECT COUNT(prr) FROM property_register_requests prr where prr.number=:number AND prr.residentialEntityId=:residentialEntityId and prr.active = true")
    int countActivePropertyRequestByNumberAndResidentialEntityId(int number, Long residentialEntityId);

    @Query("SELECT prr FROM property_register_requests prr where prr.propertyType.id=:propertyTypeId")
    List<PropertyRegisterRequest> findAllRequestsByPropertyType(Long propertyTypeId);
}
