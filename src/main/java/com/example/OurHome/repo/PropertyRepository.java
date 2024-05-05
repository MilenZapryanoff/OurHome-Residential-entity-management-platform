package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT COUNT(p) FROM properties p where p.isValidated = false AND p.isRejected = false AND p.residentialEntity.id=:id")
    Long countNotVerifiedPropertiesByResidentialEntity(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM properties p where p.isObtained = false AND p.isRejected = false AND p.residentialEntity.id=:id AND p.propertyRequest is not null AND p.propertyRequest.obtainRequest = true")
    Long countPendingPropertyObtainsByResidentialEntity(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM properties p where p.isRejected = true AND p.residentialEntity.id=:id AND p.isObtained = false")
    Long countRejectedPropertiesByResidentialEntity(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM properties p where p.residentialEntity.id=:id")
    Long countAllPropertiesByResidentialEntity(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM properties p where p.number=:number")
    Long countPropertiesByNumber(@Param("number") int number);

    @Query("SELECT COUNT(p) FROM properties p where p.residentialEntity.id=:id and p.monthlyFeeFundMm is not null")
    Long countAllPropertiesWithSetFeeByResidentialEntity(@Param("id") Long id);

    @Query("SELECT p FROM properties p where p.isObtained = false AND p.isRejected = false AND p.residentialEntity.id=:residentialEntityId AND p.propertyRequest is not null AND p.propertyRequest.obtainRequest = true ORDER BY p.number")
    List<Property> findPendingPropertyObtainsByResidentialEntity(Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.residentialEntity.id=:residentialEntityId and p.monthlyFeeFundMm is not null ORDER BY p.number")
    List<Property> findAllPropertiesWithSetFee(Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.isRejected = true AND p.residentialEntity.id=:residentialEntityId AND p.isObtained = false ORDER BY p.number")
    List<Property> findRejectedObtainProperties(Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.owner.id=:residentId AND p.residentialEntity.id=:residentialEntityId AND p.isObtained = true")
    List<Property> findAllUserObtainedProperties(Long residentId, Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.owner.id=:residentId AND p.residentialEntity.id=:residentialEntityId")
    List<Property> findAllUserProperties(Long residentId, Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.number=:propertyNumber AND p.residentialEntity.id=:residentialEntityId")
    Property findByPropertyNumberAndResidentialEntityId(int propertyNumber, Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.propertyType=:propertyType")
    List<Property> findAllPropertiesByPropertyType(PropertyType propertyType);

    @Query("SELECT count(p) FROM properties p where p.propertyType=:propertyType and p.notHabitable = false and p.isRejected = false and p.isValidated = true")
    Long countHabitablePropertiesByPropertyType(PropertyType propertyType);

    @Query("SELECT count(p) FROM properties p where p.propertyType=:propertyType and p.notHabitable = true and p.isRejected = false and p.isValidated = true")
    Long countNotHabitablePropertiesByPropertyType(PropertyType propertyType);

    @Query("SELECT count(p) FROM properties p where p.isValidated = false and p.residentialEntity.id =:residentialEntityId")
    Long countNotParametrizedPropertiesByResidentialEntity(Long residentialEntityId);

    @Query("SELECT count(p) FROM properties p where p.propertyType is null AND p.notHabitable = false AND p.residentialEntity.id=:residentialEntityId")
    Long countHabitablePropertiesWithoutPropertyTypeByREId(Long residentialEntityId);

    @Query("SELECT count(p) FROM properties p where p.propertyType is null AND p.notHabitable = true AND p.residentialEntity.id=:residentialEntityId")
    Long countNonHabitablePropertiesWithoutPropertyTypeByREId(Long residentialEntityId);

    @Query("SELECT p FROM properties p where p.residentialEntity.id=:id")
    List<Property> findAllPropertiesByResidentialEntity(Long id);

    @Query("SELECT count(p) FROM properties p WHERE p.residentialEntity.id =:residentialEntityId AND p.isObtained = true")
    Long checkPropertiesForOwnersSet(Long residentialEntityId);
}
