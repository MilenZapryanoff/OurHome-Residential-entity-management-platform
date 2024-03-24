package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyTypeRepository extends JpaRepository<PropertyType, Long> {

    @Query("SELECT COUNT(pt) FROM property_types pt where pt.residentialEntity.id=:id")
    Long countPropertyTypes(@Param("id") Long id);

    @Query("SELECT pt FROM property_types pt where pt.residentialEntity.id=:id")
    List<PropertyType> findPropertyTypesByRE(@Param("id") Long id);

    @Query("SELECT count(pt) FROM property_types pt where pt.residentialEntity.id=:id")
    Long countPropertyTypesByRE(@Param("id") Long id);

    @Query("SELECT pt.residentialEntity FROM property_types pt WHERE pt.id=:id")
    ResidentialEntity findResidentialEntityByPropertyTypeId(Long id);

}