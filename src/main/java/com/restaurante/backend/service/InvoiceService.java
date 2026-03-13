package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Invoice;

import java.util.Optional;

public interface InvoiceService {
    Invoice generateInvoiceForOrder(Long orderId, String paymentMethod);

    Optional<Invoice> getInvoiceById(Long id);

    Optional<Invoice> getInvoiceByOrderId(Long orderId);
}
