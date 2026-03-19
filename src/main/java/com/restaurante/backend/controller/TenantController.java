package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.dto.PublicTenantDto;
import com.restaurante.backend.security.TenantContext;
import com.restaurante.backend.service.TenantService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * GET /api/tenants/public/info
     * Público — no requiere autenticación.
     * Retorna nombre, moneda y tema del tenant identificado por el dominio.
     */
    @GetMapping("/public/info")
    public ResponseEntity<PublicTenantDto> getPublicTenantInfo() {
        Tenant tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tenantService.getPublicInfo(tenant.getId()));
    }

    /**
     * PATCH /api/tenants/theme
     * Solo ADMIN — actualiza el tema visual del tenant actual.
     * Body: { "theme": "restaurant" }
     * Temas válidos: indigo | restaurant | retail | premium
     */
    @PatchMapping("/theme")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublicTenantDto> updateTheme(
            @RequestBody Map<String, @Pattern(regexp = "indigo|restaurant|retail|premium",
                    message = "Tema inválido. Use: indigo, restaurant, retail o premium") String> body) {
        String theme = body.get("theme");
        if (theme == null) {
            return ResponseEntity.badRequest().build();
        }
        Tenant tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tenantService.updateTheme(tenant.getId(), theme));
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
