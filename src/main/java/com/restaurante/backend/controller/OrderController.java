package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Order;
import com.restaurante.backend.domain.entity.OrderItem;
import com.restaurante.backend.dto.DtoMapper;
import com.restaurante.backend.dto.OrderResponseDto;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import com.restaurante.backend.dto.UpdateOrderStatusDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final TenantSecurityService tenantSecurity;

    @PostMapping("/branch/{branchId}/table/{tableId}/user/{userId}")
    public ResponseEntity<OrderResponseDto> createOrder(
            @PathVariable Long branchId,
            @PathVariable Long tableId,
            @PathVariable Long userId,
            @RequestBody Order order) {
        tenantSecurity.verifyBranchAccess(branchId);
        Order savedOrder = orderService.createOrder(tableId, userId, branchId, order);
        return new ResponseEntity<>(DtoMapper.toOrderResponseDto(savedOrder), HttpStatus.CREATED);
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByBranchId(@PathVariable Long branchId) {
        tenantSecurity.verifyBranchAccess(branchId);
        List<OrderResponseDto> dtos = orderService.getOrdersByBranchId(branchId).stream()
                .map(DtoMapper::toOrderResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/branch/{branchId}/active")
    public ResponseEntity<List<OrderResponseDto>> getActiveOrdersByBranchId(@PathVariable Long branchId) {
        tenantSecurity.verifyBranchAccess(branchId);
        List<OrderResponseDto> dtos = orderService.getActiveOrdersByBranchId(branchId).stream()
                .map(DtoMapper::toOrderResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/table/{tableId}/active")
    public ResponseEntity<OrderResponseDto> getActiveOrderByTableId(@PathVariable Long tableId) {
        return orderService.getActiveOrderByTableId(tableId)
                .map(DtoMapper::toOrderResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(DtoMapper::toOrderResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusDto dto) {
        Order updatedOrder = orderService.updateOrderStatus(id, dto.getStatus(), dto.getPaymentMethod());
        return ResponseEntity.ok(DtoMapper.toOrderResponseDto(updatedOrder));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponseDto> addItemsToOrder(@PathVariable Long id, @RequestBody List<OrderItem> items) {
        Order updatedOrder = orderService.addItemsToOrder(id, items);
        return ResponseEntity.ok(DtoMapper.toOrderResponseDto(updatedOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        Order modifiedOrder = orderService.updateOrder(id, updatedOrder);
        return ResponseEntity.ok(DtoMapper.toOrderResponseDto(modifiedOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
