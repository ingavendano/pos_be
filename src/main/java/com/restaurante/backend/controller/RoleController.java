package com.restaurante.backend.controller;

import com.restaurante.backend.dto.RoleRequest;
import com.restaurante.backend.dto.RoleResponse;
import com.restaurante.backend.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RoleResponse>> getRoles(@PathVariable Long tenantId) {
        return ResponseEntity.ok(roleService.getRolesByTenant(tenantId));
    }

    @GetMapping("/tenant/{tenantId}/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long tenantId, @PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id, tenantId));
    }

    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<RoleResponse> createRole(@PathVariable Long tenantId,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(tenantId, request));
    }

    @PutMapping("/tenant/{tenantId}/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long tenantId, @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, tenantId, request));
    }

    @DeleteMapping("/tenant/{tenantId}/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long tenantId, @PathVariable Long id) {
        roleService.deleteRole(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}
