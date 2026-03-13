package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Order;
import com.restaurante.backend.domain.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Long tableId, Long userId, Long branchId, Order order);

    Order updateOrderStatus(Long id, String status, String paymentMethod);

    Optional<Order> getOrderById(Long id);

    List<Order> getOrdersByBranchId(Long branchId);

    List<Order> getActiveOrdersByBranchId(Long branchId);

    Order addItemsToOrder(Long orderId, List<OrderItem> items);

    Optional<Order> getActiveOrderByTableId(Long tableId);

    Order updateOrder(Long orderId, Order updatedOrder);

    void deleteOrder(Long id);
}
