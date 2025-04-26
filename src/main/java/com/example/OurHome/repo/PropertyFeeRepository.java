package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.PropertyFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyFeeRepository extends JpaRepository<PropertyFee, Long> {

    @Query("SELECT SUM(f.dueAmount) FROM property_fees f WHERE f.isPaid = false AND f.nonFinancial = false AND f.property.id=:id")
    BigDecimal sumOfUnpaidFees(@Param("id") Long id);

    @Query("SELECT COUNT(f.id) FROM property_fees f WHERE f.property.id=:id")
    Long countAllFeesByProperty(@Param("id") Long id);

    @Query("SELECT COUNT(f.id) FROM property_fees f WHERE f.isPaid = false AND f.nonFinancial = false AND f.property.id=:id")
    Long countUnpaidFeesByProperty(@Param("id") Long id);

    @Query("SELECT f FROM property_fees f WHERE f.property.id=:id ORDER BY f.id DESC, f.periodStart DESC, f.periodEnd DESC")
    List<PropertyFee> findAllFees(@Param("id") Long id);

    @Query("SELECT f FROM property_fees f WHERE f.property.id=:id AND f.nonFinancial = false ORDER BY f.id DESC, f.periodStart DESC, f.periodEnd DESC")
    List<PropertyFee> findAllFinancialFees(@Param("id") Long id);

    @Query("SELECT COUNT(f.id) FROM property_fees f WHERE f.isPaid = true AND f.id=:id")
    Long checkIfFeePaid(Long id);

    @Query("SELECT sum(f.overpaidAmountStart - f.overpaidAmountEnd) FROM property_fees f where f.isPaid = false and f.property.residentialEntity.id=:residentialEntityId")
    BigDecimal sumOfAllBlockedOverpayments(Long residentialEntityId);

    @Query("SELECT f FROM property_fees f where f.isPaid = false and (f.overpaidAmountStart - f.overpaidAmountEnd) > 0 and f.property.residentialEntity.id=:residentialEntityId")
    List<PropertyFee> findFeesWithBlockedOverpayments(Long residentialEntityId);

    @Query("SELECT SUM(f.dueAmount) FROM property_fees f where f.isPaid = false and f.property.residentialEntity.id=:residentialEntityId")
    BigDecimal findAllUnpaidFeesByResidentialEntityID(Long residentialEntityId);
}
