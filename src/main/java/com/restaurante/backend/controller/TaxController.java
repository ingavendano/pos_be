package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Tax;
import com.restaurante.backend.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxes")
@RequiredArgsConstructor
public class TaxController {

    private final TaxService taxService;

    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<Tax> createTax(@PathVariable Long tenantId, @RequestBody Tax tax) {
        return new ResponseEntity<>(taxService.createTax(tenantId, tax), HttpStatus.CREATED);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Tax>> getTaxesByTenantId(@PathVariable Long tenantId) {
        return ResponseEntity.ok(taxService.getTaxesByTenantId(tenantId));
    }

    @GetMapping("/tenant/{tenantId}/active")
    public ResponseEntity<List<Tax>> getActiveTaxesByTenantId(@PathVariable Long tenantId) {
        return ResponseEntity.ok(taxService.getActiveTaxesByTenantId(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tax> getTaxById(@PathVariable Long id) {
        return taxService.getTaxById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tax> updateTax(@PathVariable Long id, @RequestBody Tax tax) {
        return ResponseEntity.ok(taxService.updateTax(id, tax));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTax(@PathVariable Long id) {
        taxService.deleteTax(id);
        return ResponseEntity.noContent().build();
    }
}
