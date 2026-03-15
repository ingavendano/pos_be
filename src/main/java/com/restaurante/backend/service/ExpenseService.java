package com.restaurante.backend.service;

import com.restaurante.backend.dto.ExpenseDto;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseDto createExpense(ExpenseDto expenseDto, Long branchId);
    List<ExpenseDto> getExpensesByBranch(Long branchId);
    List<ExpenseDto> getExpensesByBranchAndPeriod(Long branchId, LocalDate start, LocalDate end);
    void deleteExpense(Long id);
}
