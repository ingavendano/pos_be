package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.dto.PublicTenantDto;
import com.restaurante.backend.security.TenantContext;
import com.restaurante.backend.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping("/public/info")
    public ResponseEntity<PublicTenantDto> getPublicTenantInfo() {
        Tenant tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(PublicTenantDto.builder()
                .name(tenant.getName())
                .currency(tenant.getCurrency())
                .currencySymbol(tenant.getCurrencySymbol())
                .build());
    }

    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody Tenant tenant) {
        return new ResponseEntity<>(tenantService.createTenant(tenant), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        return tenantService.getTenantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> updateTenant(@PathVariable Long id, @RequestBody Tenant tenant) {
        return ResponseEntity.ok(tenantService.updateTenant(id, tenant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
