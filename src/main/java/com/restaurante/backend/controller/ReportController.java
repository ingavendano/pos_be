package com.restaurante.backend.controller;

import com.restaurante.backend.dto.SalesReportResponse;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final TenantSecurityService tenantSecurity;

    /**
     * GET /api/reports/tenant/{tenantId}/sales?from=2025-01-01&to=2025-01-31
     */
    @GetMapping("/tenant/{tenantId}/sales")
    public ResponseEntity<SalesReportResponse> getSalesReport(
            @PathVariable Long tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        tenantSecurity.verifyTenantAccess(tenantId);
        return ResponseEntity.ok(reportService.getSalesReport(tenantId, from, to));
    }
}
