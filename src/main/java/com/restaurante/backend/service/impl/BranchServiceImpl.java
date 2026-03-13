package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Branch;
import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.repository.BranchRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public Branch createBranch(Long tenantId, Branch branch) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Tenant not found"));
        branch.setTenant(tenant);
        return branchRepository.save(branch);
    }

    @Override
    @Transactional
    public Branch updateBranch(Long id, Branch branchDetails) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Branch not found"));

        branch.setName(branchDetails.getName());
        branch.setAddress(branchDetails.getAddress());
        branch.setPhone(branchDetails.getPhone());

        return branchRepository.save(branch);
    }

    @Override
    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    @Override
    public List<Branch> getBranchesByTenantId(Long tenantId) {
        return branchRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional
    public void deleteBranch(Long id) {
        branchRepository.deleteById(id);
    }
}
