package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Branch;
import com.restaurante.backend.domain.entity.Expense;
import com.restaurante.backend.dto.DtoMapper;
import com.restaurante.backend.dto.ExpenseDto;
import com.restaurante.backend.repository.BranchRepository;
import com.restaurante.backend.repository.ExpenseRepository;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BranchRepository branchRepository;
    private final TenantSecurityService tenantSecurityService;

    @Override
    @Transactional
    public ExpenseDto createExpense(ExpenseDto expenseDto, Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Branch not found"));

        Expense expense = Expense.builder()
                .description(expenseDto.getDescription())
                .amount(expenseDto.getAmount())
                .category(expenseDto.getCategory())
                .expenseDate(expenseDto.getExpenseDate() != null ? expenseDto.getExpenseDate() : LocalDate.now())
                .branch(branch)
                .tenant(branch.getTenant())
                .build();

        return DtoMapper.toExpenseDto(expenseRepository.save(expense));
    }

    @Override
    public List<ExpenseDto> getExpensesByBranch(Long branchId) {
        return expenseRepository.findByBranchId(branchId).stream()
                .map(DtoMapper::toExpenseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDto> getExpensesByBranchAndPeriod(Long branchId, LocalDate start, LocalDate end) {
        return expenseRepository.findByBranchIdAndExpenseDateBetween(branchId, start, end).stream()
                .map(DtoMapper::toExpenseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteExpense(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        expenseRepository.deleteByIdAndTenantId(id, tenantId);
    }
}
