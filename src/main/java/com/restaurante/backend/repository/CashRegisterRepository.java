package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.CashRegister;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {

    /**
     * Returns the currently open register for a branch, if any.
     */
    @EntityGraph(attributePaths = { "openedBy", "closedBy", "branch" })
    Optional<CashRegister> findByBranchIdAndStatus(Long branchId, String status);

    /**
     * Full history for the branch, newest first.
     */
    @EntityGraph(attributePaths = { "openedBy", "closedBy", "branch" })
    List<CashRegister> findByBranchIdOrderByOpenedAtDesc(Long branchId);

    /**
     * History for a date range (for reporting).
     */
    @EntityGraph(attributePaths = { "openedBy", "closedBy", "branch" })
    @Query("SELECT cr FROM CashRegister cr WHERE cr.branch.tenant.id = :tenantId " +
            "AND cr.openedAt BETWEEN :from AND :to ORDER BY cr.openedAt DESC")
    List<CashRegister> findByTenantAndDateRange(
            @Param("tenantId") Long tenantId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
