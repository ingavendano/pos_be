package com.restaurante.backend.controller;

import com.restaurante.backend.dto.CashRegisterDto;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.CashRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cash-registers")
@RequiredArgsConstructor
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;
    private final TenantSecurityService tenantSecurity;

    /** GET current open register for a branch (null if none open) */
    @GetMapping("/branch/{branchId}/current")
    public ResponseEntity<CashRegisterDto.Response> getCurrent(@PathVariable Long branchId) {
        tenantSecurity.verifyBranchAccess(branchId);
        CashRegisterDto.Response current = cashRegisterService.getCurrentRegister(branchId);
        return current != null ? ResponseEntity.ok(current) : ResponseEntity.noContent().build();
    }

    /** GET history for a branch */
    @GetMapping("/branch/{branchId}/history")
    public ResponseEntity<List<CashRegisterDto.Response>> getHistory(@PathVariable Long branchId) {
        tenantSecurity.verifyBranchAccess(branchId);
        return ResponseEntity.ok(cashRegisterService.getHistory(branchId));
    }

    /** POST open a new register */
    @PostMapping("/branch/{branchId}/open")
    public ResponseEntity<CashRegisterDto.Response> openRegister(
            @PathVariable Long branchId,
            @Valid @RequestBody CashRegisterDto.OpenRequest request) {
        tenantSecurity.verifyBranchAccess(branchId);
        return ResponseEntity.ok(cashRegisterService.openRegister(branchId, request));
    }

    /** PATCH close the open register */
    @PatchMapping("/branch/{branchId}/close")
    public ResponseEntity<CashRegisterDto.Response> closeRegister(
            @PathVariable Long branchId,
            @Valid @RequestBody CashRegisterDto.CloseRequest request) {
        tenantSecurity.verifyBranchAccess(branchId);
        return ResponseEntity.ok(cashRegisterService.closeRegister(branchId, request));
    }
}
