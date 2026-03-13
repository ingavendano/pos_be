package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Branch;

import java.util.List;
import java.util.Optional;

public interface BranchService {
    Branch createBranch(Long tenantId, Branch branch);

    Branch updateBranch(Long id, Branch branchDetails);

    Optional<Branch> getBranchById(Long id);

    List<Branch> getBranchesByTenantId(Long tenantId);

    void deleteBranch(Long id);
}
