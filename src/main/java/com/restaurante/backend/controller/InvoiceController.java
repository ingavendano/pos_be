package com.restaurante.backend.controller;

import com.restaurante.backend.dto.DtoMapper;
import com.restaurante.backend.dto.InvoiceDto;
import com.restaurante.backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<InvoiceDto> generateInvoiceForOrder(
            @PathVariable Long orderId,
            @RequestParam String paymentMethod) {
        return new ResponseEntity<>(
                DtoMapper.toInvoiceDto(invoiceService.generateInvoiceForOrder(orderId, paymentMethod)),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
                .map(DtoMapper::toInvoiceDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<InvoiceDto> getInvoiceByOrderId(@PathVariable Long orderId) {
        return invoiceService.getInvoiceByOrderId(orderId)
                .map(DtoMapper::toInvoiceDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
