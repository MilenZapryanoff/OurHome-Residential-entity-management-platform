package com.example.OurHome.repo;

import com.example.OurHome.model.Entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensesRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT COUNT(e.id) FROM Expense e WHERE e.residentialEntity.id =:id")
    Long countExpenses(@Param("id") Long id);

}
