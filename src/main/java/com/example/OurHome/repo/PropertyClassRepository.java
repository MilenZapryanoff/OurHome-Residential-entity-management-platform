package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.PropertyClass;
import com.example.OurHome.model.enums.PropertyClassName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyClassRepository extends JpaRepository<PropertyClass, Long> {
    PropertyClass findByName(PropertyClassName name);
}
