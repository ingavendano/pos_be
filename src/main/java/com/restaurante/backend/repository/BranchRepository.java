package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByTenantId(Long tenantId);
}
