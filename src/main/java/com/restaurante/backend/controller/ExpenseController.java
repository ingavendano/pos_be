package com.restaurante.backend.controller;

import com.restaurante.backend.dto.ExpenseDto;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final TenantSecurityService tenantSecurity;

    @PostMapping("/branch/{branchId}")
    public ResponseEntity<ExpenseDto> createExpense(
            @PathVariable Long branchId,
            @RequestBody ExpenseDto expenseDto) {
        tenantSecurity.verifyBranchAccess(branchId);
        return ResponseEntity.ok(expenseService.createExpense(expenseDto, branchId));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<ExpenseDto>> getExpenses(
            @PathVariable Long branchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        tenantSecurity.verifyBranchAccess(branchId);
        
        if (start != null && end != null) {
            return ResponseEntity.ok(expenseService.getExpensesByBranchAndPeriod(branchId, start, end));
        }
        return ResponseEntity.ok(expenseService.getExpensesByBranch(branchId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        // Simple delete for now, in a real app we'd verify ownership of the expense
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
