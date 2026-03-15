package com.restaurante.backend.controller;

import com.restaurante.backend.dto.ProfitabilityReportResponse;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;
    private final TenantSecurityService tenantSecurity;

    @GetMapping("/profitability/branch/{branchId}")
    public ResponseEntity<ProfitabilityReportResponse> getProfitabilityReport(
            @PathVariable Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        tenantSecurity.verifyBranchAccess(branchId);
        return ResponseEntity.ok(reportingService.getProfitabilityReport(branchId, start, end));
    }
}
