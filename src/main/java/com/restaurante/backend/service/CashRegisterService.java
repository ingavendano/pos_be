package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Branch;
import com.restaurante.backend.domain.entity.CashRegister;
import com.restaurante.backend.domain.entity.User;
import com.restaurante.backend.dto.CashRegisterDto;
import com.restaurante.backend.repository.BranchRepository;
import com.restaurante.backend.repository.CashRegisterRepository;
import com.restaurante.backend.repository.InvoiceRepository;
import com.restaurante.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final InvoiceRepository invoiceRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Open ──────────────────────────────────────────────────

    @Transactional
    public CashRegisterDto.Response openRegister(Long branchId, CashRegisterDto.OpenRequest req) {
        // Verify no open register exists
        cashRegisterRepository.findByBranchIdAndStatus(branchId, "OPEN").ifPresent(cr -> {
            throw new com.restaurante.backend.exception.BusinessLogicException(
                    "Ya hay una caja abierta para esta sucursal.");
        });

        Branch branch = branchRepository.getReferenceById(branchId);
        User currentUser = getCurrentUser();

        CashRegister register = CashRegister.builder()
                .branch(branch)
                .openedBy(currentUser)
                .openingAmount(req.getOpeningAmount())
                .notes(req.getNotes())
                .status("OPEN")
                .build();

        return mapToResponse(cashRegisterRepository.save(register));
    }

    // ── Close ─────────────────────────────────────────────────

    @Transactional
    public CashRegisterDto.Response closeRegister(Long branchId, CashRegisterDto.CloseRequest req) {
        CashRegister register = cashRegisterRepository.findByBranchIdAndStatus(branchId, "OPEN")
                .orElseThrow(() -> new com.restaurante.backend.exception.BusinessLogicException(
                        "No hay ninguna caja abierta para cerrar."));

        User currentUser = getCurrentUser();

        // Calculate sales totals from invoices issued since opening
        LocalDateTime from = register.getOpenedAt();
        LocalDateTime to = LocalDateTime.now();
        BigDecimal totalCash = sumByPaymentMethod(branchId, "CASH", from, to);
        BigDecimal totalCard = sumByPaymentMethod(branchId, "CARD", from, to);
        BigDecimal totalTransfer = sumByPaymentMethod(branchId, "TRANSFER", from, to);
        BigDecimal totalSales = totalCash.add(totalCard).add(totalTransfer);

        // Difference: money counted - (initial fund + expected cash sales)
        BigDecimal expectedCash = register.getOpeningAmount().add(totalCash);
        BigDecimal difference = req.getClosingAmount().subtract(expectedCash).setScale(2, RoundingMode.HALF_UP);

        register.setStatus("CLOSED");
        register.setClosedAt(to);
        register.setClosedBy(currentUser);
        register.setClosingAmount(req.getClosingAmount());
        register.setTotalCash(totalCash);
        register.setTotalCard(totalCard);
        register.setTotalTransfer(totalTransfer);
        register.setTotalSales(totalSales);
        register.setDifference(difference);
        if (req.getNotes() != null)
            register.setNotes(req.getNotes());

        return mapToResponse(cashRegisterRepository.save(register));
    }

    // ── Read ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CashRegisterDto.Response getCurrentRegister(Long branchId) {
        return cashRegisterRepository.findByBranchIdAndStatus(branchId, "OPEN")
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CashRegisterDto.Response> getHistory(Long branchId) {
        return cashRegisterRepository.findByBranchIdOrderByOpenedAtDesc(branchId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────

    private BigDecimal sumByPaymentMethod(Long branchId, String method, LocalDateTime from, LocalDateTime to) {
        BigDecimal result = invoiceRepository.sumByBranchAndPaymentMethodAndDateRange(branchId, method, from, to);
        return result != null ? result : BigDecimal.ZERO;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Usuario no encontrado"));
    }

    private CashRegisterDto.Response mapToResponse(CashRegister cr) {
        CashRegisterDto.Response r = new CashRegisterDto.Response();
        r.setId(cr.getId());
        r.setStatus(cr.getStatus());
        r.setOpeningAmount(cr.getOpeningAmount());
        r.setOpenedAt(cr.getOpenedAt() != null ? cr.getOpenedAt().format(DISPLAY_FMT) : null);
        r.setOpenedByName(cr.getOpenedBy() != null ? cr.getOpenedBy().getName() : "");
        r.setClosingAmount(cr.getClosingAmount());
        r.setClosedAt(cr.getClosedAt() != null ? cr.getClosedAt().format(DISPLAY_FMT) : null);
        r.setClosedByName(cr.getClosedBy() != null ? cr.getClosedBy().getName() : null);
        r.setTotalCash(cr.getTotalCash());
        r.setTotalCard(cr.getTotalCard());
        r.setTotalTransfer(cr.getTotalTransfer());
        r.setTotalSales(cr.getTotalSales());
        r.setDifference(cr.getDifference());
        r.setNotes(cr.getNotes());
        r.setBranchName(cr.getBranch() != null ? cr.getBranch().getName() : "");
        return r;
    }
}
