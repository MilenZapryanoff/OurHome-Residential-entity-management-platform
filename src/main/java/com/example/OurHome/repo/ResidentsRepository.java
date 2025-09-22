package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Resident;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentsRepository extends JpaRepository<Resident, Long> {

//    @Query("SELECT COUNT(r) FROM residents r where r.property.id=:id and r.isAdult = true")
//    Long countAdultResidentsByPropertyId(@Param("id") Long id);

    @Query("SELECT r FROM residents r where r.property.id=:id and r.isAdult = true and r.isOwner = false")
    List<Resident> findAdultResidentsByPropertyId(@Param("id") Long id);

//    @Query("SELECT COUNT(r) FROM residents r where r.property.id=:id and r.isAdult = false")
//    Long countChildrenResidentsByPropertyId(@Param("id") Long id);

    @Query("SELECT r FROM residents r where r.property.id=:id and r.isAdult = false and r.isOwner = false")
    List<Resident> findChildrenResidentsByPropertyId(@Param("id") Long id);

    @Query("SELECT r FROM residents r where r.property.id=:id and r.isOwner = true")
    Resident findOwnerResidentByPropertyId(Long id);
}



