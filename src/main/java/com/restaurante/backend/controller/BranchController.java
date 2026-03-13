package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Branch;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;
    private final TenantSecurityService tenantSecurity;

    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<Branch> createBranch(@PathVariable Long tenantId, @RequestBody Branch branch) {
        tenantSecurity.verifyTenantAccess(tenantId);
        return new ResponseEntity<>(branchService.createBranch(tenantId, branch), HttpStatus.CREATED);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Branch>> getBranchesByTenantId(@PathVariable Long tenantId) {
        tenantSecurity.verifyTenantAccess(tenantId);
        return ResponseEntity.ok(branchService.getBranchesByTenantId(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable Long id) {
        return branchService.getBranchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Branch> updateBranch(@PathVariable Long id, @RequestBody Branch branch) {
        return ResponseEntity.ok(branchService.updateBranch(id, branch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }
}
