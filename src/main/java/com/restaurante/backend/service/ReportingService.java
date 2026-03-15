package com.restaurante.backend.service;

import com.restaurante.backend.dto.ProfitabilityReportResponse;
import java.time.LocalDate;

public interface ReportingService {
    ProfitabilityReportResponse getProfitabilityReport(Long branchId, LocalDate start, LocalDate end);
}
