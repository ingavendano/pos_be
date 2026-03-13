package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.InvoiceTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceTaxRepository extends JpaRepository<InvoiceTax, Long> {
    List<InvoiceTax> findByInvoiceId(Long invoiceId);
}
