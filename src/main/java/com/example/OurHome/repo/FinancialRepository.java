package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT COUNT(e.id) FROM Expense e WHERE e.residentialEntity.id =:id")
    Long countExpenses(@Param("id") Long id);
    @Query("SELECT e FROM Expense e WHERE e.residentialEntity.id =:id AND e.expenseDate >= :startPeriod AND e.expenseDate <= :endPeriod")
    List<Expense> findExpensesByDatesAndResidentialEntityId(LocalDate startPeriod, LocalDate endPeriod, Long id);
}
