package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.PropertyFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PropertyFeeRepository extends JpaRepository<PropertyFee, Long> {

    @Query("SELECT SUM(p.feeAmount) FROM property_fees p where p.isPaid = false AND p.property.id=:id")
    BigDecimal sumOfUnpaidFees(@Param("id") Long id);

}
