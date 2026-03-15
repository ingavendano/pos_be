package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTenantId(Long tenantId);
    List<Expense> findByBranchId(Long branchId);
    List<Expense> findByBranchIdAndExpenseDateBetween(Long branchId, LocalDate start, LocalDate end);
    List<Expense> findByTenantIdAndExpenseDateBetween(Long tenantId, LocalDate start, LocalDate end);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(e.amount) FROM Expense e " +
            "WHERE e.branch.id = :branchId AND e.expenseDate BETWEEN :start AND :end")
    java.util.Optional<java.math.BigDecimal> sumAmountByBranchAndDateRange(
            @org.springframework.data.repository.query.Param("branchId") Long branchId, 
            @org.springframework.data.repository.query.Param("start") java.time.LocalDate start, 
            @org.springframework.data.repository.query.Param("end") java.time.LocalDate end);

    @org.springframework.data.jpa.repository.Query("SELECT e.category, SUM(e.amount) FROM Expense e " +
            "WHERE e.branch.id = :branchId AND e.expenseDate BETWEEN :start AND :end " +
            "GROUP BY e.category")
    List<Object[]> sumAmountByCategoryAndBranchAndDateRange(
            @org.springframework.data.repository.query.Param("branchId") Long branchId, 
            @org.springframework.data.repository.query.Param("start") java.time.LocalDate start, 
            @org.springframework.data.repository.query.Param("end") java.time.LocalDate end);
}
