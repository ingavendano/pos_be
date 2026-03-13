package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Order;
import com.restaurante.backend.domain.entity.OrderItem;
import com.restaurante.backend.domain.entity.Product;
import com.restaurante.backend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/data-test.sql")
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldCreateOrderSuccessfully() {
        // Arrange: Use Table 1, User 1, Branch 1 (Present from Flyway
        // V2__seed_default_data.sql)
        Long tableId = 1L;
        Long userId = 1L;
        Long branchId = 1L;
        Order newOrder = new Order();

        // Act
        Order savedOrder = orderService.createOrder(tableId, userId, branchId, newOrder);

        // Assert
        assertNotNull(savedOrder.getId());
        assertEquals("PENDING", savedOrder.getStatus());
        assertEquals(BigDecimal.ZERO, savedOrder.getTotal());
        assertNotNull(savedOrder.getCreatedAt());
        assertEquals(tableId, savedOrder.getRestaurantTable().getId());
    }

    @Test
    void shouldAddItemsAndCalculateTotal() {
        // Arrange: Create new order and load existing products (Products 1 and 4 from
        // Flyway V2)
        Long tableId = 1L;
        Long userId = 1L;
        Long branchId = 1L;
        Order savedOrder = orderService.createOrder(tableId, userId, branchId, new Order());

        Product limonada = productRepository.findById(1L).orElseThrow(); // 3.50
        Product hamburguesa = productRepository.findById(4L).orElseThrow(); // 12.00

        OrderItem item1 = new OrderItem();
        item1.setProduct(limonada);
        item1.setQuantity(2); // 2 * 3.50 = 7.00
        item1.setUnitPrice(limonada.getPrice());

        OrderItem item2 = new OrderItem();
        item2.setProduct(hamburguesa);
        item2.setQuantity(1); // 1 * 12.00 = 12.00
        item2.setUnitPrice(hamburguesa.getPrice());

        // Act
        Order updatedOrder = orderService.addItemsToOrder(savedOrder.getId(), List.of(item1, item2));

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(2, updatedOrder.getItems().size());

        // Total should be 7.00 + 12.00 = 19.00
        assertEquals(0, new BigDecimal("19.00").compareTo(updatedOrder.getTotal()), "Total does not match expected");
    }
}
