package com.restaurante.backend.dto;

import com.restaurante.backend.domain.entity.Customer;
import com.restaurante.backend.domain.entity.Invoice;
import com.restaurante.backend.domain.entity.Order;
import com.restaurante.backend.domain.entity.OrderItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    public static OrderResponseDto toOrderResponseDto(Order order) {
        if (order == null)
            return null;

        return OrderResponseDto.builder()
                .id(order.getId())
                .consecutiveNumber(order.getConsecutiveNumber())
                .status(order.getStatus())
                .total(order.getTotal())
                .discountType(order.getDiscountType())
                .discountValue(order.getDiscountValue())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .tableId(order.getRestaurantTable() != null ? order.getRestaurantTable().getId() : null)
                .tableNumber(order.getRestaurantTable() != null ? order.getRestaurantTable().getNumber() : null)
                .branchId(order.getBranch() != null ? order.getBranch().getId() : null)
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .waiterName(order.getUser() != null ? order.getUser().getName() : null)
                .customer(toCustomerDto(order.getCustomer()))
                .invoice(toInvoiceDto(order.getInvoice()))
                .items(toOrderItemDtoList(order.getItems()))
                .build();
    }

    public static CustomerDto toCustomerDto(Customer customer) {
        if (customer == null)
            return null;
        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .nit(customer.getNit())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .nrc(customer.getNrc())
                .giro(customer.getGiro())
                .documentType(customer.getDocumentType())
                .documentNumber(customer.getDocumentNumber())
                .departamento(customer.getDepartamento())
                .municipio(customer.getMunicipio())
                .complemento(customer.getComplemento())
                .build();
    }

    public static InvoiceDto toInvoiceDto(Invoice invoice) {
        if (invoice == null)
            return null;
        return InvoiceDto.builder()
                .id(invoice.getId())
                .consecutiveNumber(invoice.getConsecutiveNumber())
                .subtotal(invoice.getSubtotal())
                .tax(invoice.getTax())
                .total(invoice.getTotal())
                .paymentMethod(invoice.getPaymentMethod())
                .issuedAt(invoice.getIssuedAt())
                .dteType(invoice.getDteType())
                .generationCode(invoice.getGenerationCode())
                .controlNumber(invoice.getControlNumber())
                .receptionSello(invoice.getReceptionSello())
                .dteStatus(invoice.getDteStatus())
                .rejectionReason(invoice.getRejectionReason())
                .build();
    }

    public static OrderItemDto toOrderItemDto(OrderItem item) {
        if (item == null)
            return null;
        return OrderItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .status(item.getStatus())
                .notes(item.getNotes())
                .build();
    }

    public static List<OrderItemDto> toOrderItemDtoList(List<OrderItem> items) {
        if (items == null)
            return Collections.emptyList();
        return items.stream()
                .map(DtoMapper::toOrderItemDto)
                .collect(Collectors.toList());
    }

    public static ExpenseDto toExpenseDto(com.restaurante.backend.domain.entity.Expense expense) {
        if (expense == null)
            return null;
        return ExpenseDto.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .expenseDate(expense.getExpenseDate())
                .build();
    }
}
