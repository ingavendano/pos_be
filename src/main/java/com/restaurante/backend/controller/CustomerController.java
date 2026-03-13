package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Customer;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final TenantSecurityService tenantSecurity;

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Customer>> getAll(
            @PathVariable Long tenantId,
            @RequestParam(required = false) String search) {
        tenantSecurity.verifyTenantAccess(tenantId);
        List<Customer> customers = (search != null && !search.isBlank())
                ? customerService.search(tenantId, search)
                : customerService.getAll(tenantId);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        return customerService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<Customer> create(
            @PathVariable Long tenantId,
            @RequestBody Customer customer) {
        tenantSecurity.verifyTenantAccess(tenantId);
        return new ResponseEntity<>(customerService.create(tenantId, customer), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.update(id, customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
