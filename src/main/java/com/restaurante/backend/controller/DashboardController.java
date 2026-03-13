package com.restaurante.backend.controller;

import com.restaurante.backend.dto.DashboardSummaryResponse;
import com.restaurante.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary/{tenantId}/branch/{branchId}")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @PathVariable Long tenantId,
            @PathVariable Long branchId) {
        return ResponseEntity.ok(dashboardService.getDashboardSummary(branchId, tenantId));
    }
}
