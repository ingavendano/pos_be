package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Invoice;
import com.restaurante.backend.domain.entity.InvoiceTax;
import com.restaurante.backend.domain.entity.Order;
import com.restaurante.backend.domain.entity.Tax;
import com.restaurante.backend.repository.InvoiceRepository;
import com.restaurante.backend.repository.InvoiceTaxRepository;
import com.restaurante.backend.repository.OrderRepository;
import com.restaurante.backend.repository.TaxRepository;
import com.restaurante.backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceTaxRepository invoiceTaxRepository;
    private final OrderRepository orderRepository;
    private final TaxRepository taxRepository;

    @Override
    @Transactional
    public Invoice generateInvoiceForOrder(Long orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Order not found"));

        if ("PAID".equals(order.getStatus())) {
            throw new com.restaurante.backend.exception.BusinessLogicException("Order is already paid");
        }

        // Fetch active taxes for the tenant of the order
        Long tenantId = order.getBranch().getTenant().getId();
        List<Tax> activeTaxes = taxRepository.findByTenantIdAndIsActiveTrue(tenantId);

        BigDecimal subtotal = order.getTotal();
        BigDecimal totalCalculatedTax = BigDecimal.ZERO;

        // Calculate the total based on Subtotal + all active taxes (like IVA + Propina)
        for (Tax tax : activeTaxes) {
            BigDecimal taxAmount = subtotal.multiply(tax.getPercentage()).divide(new BigDecimal("100"), 2,
                    RoundingMode.HALF_UP);
            totalCalculatedTax = totalCalculatedTax.add(taxAmount);
        }

        BigDecimal grandTotal = subtotal.add(totalCalculatedTax);

        // FE Logic Placeholder (El Salvador)
        String generationCode = UUID.randomUUID().toString().toUpperCase();
        String controlNumber = String.format("DTE-01-S001-%015d", order.getId());
        String receptionSello = "20241020101010101010101010101010";

        Invoice invoice = Invoice.builder()
                .order(order)
                .tenant(order.getTenant())
                .subtotal(subtotal)
                .tax(totalCalculatedTax)
                .total(grandTotal)
                .paymentMethod(paymentMethod)
                .customer(order.getCustomer())
                .dteType("01")
                .generationCode(generationCode)
                .controlNumber(controlNumber)
                .receptionSello(receptionSello)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        order.setInvoice(savedInvoice);

        // Save historic tax details for this invoice
        for (Tax tax : activeTaxes) {
            BigDecimal taxAmount = subtotal.multiply(tax.getPercentage()).divide(new BigDecimal("100"), 2,
                    RoundingMode.HALF_UP);

            InvoiceTax invoiceTax = InvoiceTax.builder()
                    .invoice(savedInvoice)
                    .tax(tax)
                    .appliedPercentage(tax.getPercentage())
                    .amountCalculated(taxAmount)
                    .build();
            invoiceTaxRepository.save(invoiceTax);
        }

        // Mark order as PAID directly via repository (avoids circular dependency with
        // InvoiceService)
        order.setStatus("PAID");
        orderRepository.save(order);

        return savedInvoice;
    }

    @Override
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public Optional<Invoice> getInvoiceByOrderId(Long orderId) {
        return invoiceRepository.findByOrderId(orderId);
    }
}
