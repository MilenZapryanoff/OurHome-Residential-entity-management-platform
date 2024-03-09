package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT COUNT(p) FROM properties p where p.isValidated = false AND p.isRejected = false AND p.residentialEntity.id=:id")
    Long countNotVerifiedPropertiesByResidentialEntity(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM properties p where p.isValidated = true AND p.isRejected = false AND p.residentialEntity.id=:id")
    Long countVerifiedPropertiesByResidentialEntity(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM properties p where p.isRejected = true AND p.residentialEntity.id=:id")
    Long countRejectedPropertiesByResidentialEntity(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM properties p where p.residentialEntity.id=:id")
    Long countAllPropertiesByResidentialEntity(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM properties p where p.number=:number")
    Long countPropertiesByNumber(@Param("number") String number);

    @Query("SELECT COUNT(p) FROM properties p where p.residentialEntity.id=:id and p.monthlyFee is not null")
    Long countAllPropertiesWithSetFeeByResidentialEntity(@Param("id") Long id);

    @Query("SELECT p FROM properties p where p.isValidated = false AND p.isRejected = false AND p.residentialEntity.id=:residentialEntityId ORDER BY p.number")
    List<Property> findNotValidatedProperties(Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.isValidated = true AND p.isRejected =false AND p.residentialEntity.id=:residentialEntityId ORDER BY p.number")
    List<Property> findValidatedProperties(Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.residentialEntity.id=:residentialEntityId and p.monthlyFee is not null ORDER BY p.number")
    List<Property> findAllPropertiesWithSetFee(Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.isRejected = true AND p.residentialEntity.id=:residentialEntityId ORDER BY p.number")
    List<Property> findRejectedProperties(Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.owner.id=:residentId AND p.residentialEntity.id=:residentialEntityId")
    List<Property> findAllUserProperties(Long residentId, Long residentialEntityId);
}
