package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.PropertyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRequestRepository extends JpaRepository<PropertyRequest, Long> {

    @Query("SELECT pr FROM property_requests pr where pr.number=:number AND pr.residentialEntityId=:residentialEntityId and pr.active = true")
    PropertyRequest findActivePropertyRequestByNumberAndResidentialEntityId(int number, Long residentialEntityId);

    @Query("SELECT COUNT(pr) FROM property_requests pr where pr.number=:number AND pr.residentialEntityId=:residentialEntityId and pr.active = true")
    int countActivePropertyRequestByNumberAndResidentialEntityId(int number, Long residentialEntityId);
}
