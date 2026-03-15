package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Order;
import com.restaurante.backend.domain.entity.OrderItem;
import com.restaurante.backend.dto.KitchenOrderResponse;
import com.restaurante.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KitchenService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<KitchenOrderResponse> getKitchenOrders(Long branchId, List<Long> categoryIds) {
        List<Order> orders;
        if (categoryIds == null || categoryIds.isEmpty()) {
            orders = orderRepository.findKitchenOrdersByBranchId(branchId);
        } else {
            orders = orderRepository.findKitchenOrdersByBranchIdAndCategories(branchId, categoryIds);
        }

        return orders.stream()
                .map(order -> mapToKitchenResponse(order, categoryIds))
                .collect(Collectors.toList());
    }

    @Transactional
    public KitchenOrderResponse advanceOrderStatus(Long orderId) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Orden no encontrada"));

        String nextStatus = switch (order.getStatus()) {
            case "PENDING" -> "PREPARING";
            case "PREPARING" -> "READY";
            default -> order.getStatus();
        };
        order.setStatus(nextStatus);

        // Also advance all PENDING/PREPARING items to the same status
        if (order.getItems() != null) {
            order.getItems().forEach(item -> {
                if ("PENDING".equals(item.getStatus()) || "PREPARING".equals(item.getStatus())) {
                    item.setStatus(nextStatus);
                }
            });
        }

        return mapToKitchenResponse(orderRepository.save(order), null);
    }

    private KitchenOrderResponse mapToKitchenResponse(Order order, List<Long> categoryIds) {
        long minutesElapsed = order.getCreatedAt() != null
                ? Duration.between(order.getCreatedAt(), LocalDateTime.now()).toMinutes()
                : 0;

        List<KitchenOrderResponse.KitchenItemResponse> items = order.getItems() == null
                ? List.of()
                : order.getItems().stream()
                        .filter(item -> categoryIds == null || categoryIds.isEmpty() ||
                                (item.getProduct() != null && item.getProduct().getCategory() != null &&
                                        categoryIds.contains(item.getProduct().getCategory().getId())))
                        .map(this::mapItem)
                        .collect(Collectors.toList());

        return KitchenOrderResponse.builder()
                .id(order.getId())
                .consecutiveNumber(order.getConsecutiveNumber())
                .tableNumber(order.getRestaurantTable() != null
                        ? "Mesa " + order.getRestaurantTable().getNumber()
                        : "Mesa ?")
                .waiterName(order.getUser() != null ? order.getUser().getName() : "")
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .minutesElapsed(minutesElapsed)
                .items(items)
                .build();
    }

    private KitchenOrderResponse.KitchenItemResponse mapItem(OrderItem item) {
        return KitchenOrderResponse.KitchenItemResponse.builder()
                .id(item.getId())
                .productName(item.getProduct() != null ? item.getProduct().getName() : "?")
                .quantity(item.getQuantity())
                .notes(item.getNotes())
                .status(item.getStatus())
                .build();
    }
}
