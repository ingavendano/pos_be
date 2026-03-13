package com.restaurante.backend.service;

import com.restaurante.backend.dto.DashboardSummaryResponse;

public interface DashboardService {
    DashboardSummaryResponse getDashboardSummary(Long branchId, Long tenantId);
}
