package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Branch;
import com.restaurante.backend.domain.entity.Order;
import com.restaurante.backend.domain.entity.OrderItem;
import com.restaurante.backend.domain.entity.RestaurantTable;
import com.restaurante.backend.domain.entity.User;
import com.restaurante.backend.domain.entity.Product;
import com.restaurante.backend.domain.entity.Customer;
import com.restaurante.backend.domain.entity.Warehouse;
import com.restaurante.backend.domain.entity.StockMovement;
import com.restaurante.backend.repository.BranchRepository;
import com.restaurante.backend.repository.CustomerRepository;
import com.restaurante.backend.repository.OrderRepository;
import com.restaurante.backend.repository.ProductRepository;
import com.restaurante.backend.repository.RestaurantTableRepository;
import com.restaurante.backend.repository.UserRepository;
import com.restaurante.backend.repository.WarehouseRepository;
import com.restaurante.backend.repository.ProductRecipeRepository;
import com.restaurante.backend.domain.entity.ProductRecipe;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.InventoryService;
import com.restaurante.backend.service.InvoiceService;
import com.restaurante.backend.service.NotificationService;
import com.restaurante.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRecipeRepository recipeRepository;
    private final InventoryService inventoryService;
    private final InvoiceService invoiceService;
    private final NotificationService notificationService;
    private final TenantSecurityService tenantSecurityService;

    @Override
    @Transactional
    public Order createOrder(Long tableId, Long userId, Long branchId, Order order) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Table not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("User not found"));
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Branch not found"));

        order.setRestaurantTable(table);
        order.setUser(user);
        order.setBranch(branch);
        order.setTenant(branch.getTenant());
        order.setStatus("PENDING");

        // Assign customer if provided in the body
        if (order.getCustomer() != null && order.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(order.getCustomer().getId())
                    .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                            "Customer not found"));
            order.setCustomer(customer);
        }

        // Link bidirectional items if present
        if (order.getItems() != null) {
            order.getItems().forEach(item -> {
                item.setOrder(order);
                item.setStatus("PENDING");
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                                "Product not found"));

                // Deduct inventory from the default warehouse
                processInventoryAdjustment(branch.getId(), product.getId(), item.getQuantity(),
                        StockMovement.MovementType.OUT, "Venta: Nueva Orden", user.getId());

                item.setProduct(product);
                if (item.getUnitPrice() == null) {
                    item.setUnitPrice(product.getPrice());
                }
                item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            });
            // Calculate total logically
            BigDecimal subtotal = order.getItems().stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal discountAmount = calculateDiscount(subtotal, order.getDiscountType(), order.getDiscountValue());
            order.setTotal(subtotal.subtract(discountAmount));
        }

        table.setStatus("OCCUPIED");
        tableRepository.save(table);

        Order saved = orderRepository.save(order);
        // Notify kitchen/staff of new order
        Long tenantId = branch.getTenant().getId();
        notificationService.sendToTenant(tenantId, "new_order",
                "{\"message\":\"Nueva orden en Mesa " + table.getNumber() + "\",\"orderId\":" + saved.getId() + "}");
        return saved;
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, String status, String paymentMethod) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        Order order = orderRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Order not found"));

        if ("PAID".equals(status)) {
            // Generate invoice (InvoiceServiceImpl will set order.status = PAID internally)
            invoiceService.generateInvoiceForOrder(order.getId(), paymentMethod);
            // Free the table
            RestaurantTable table = order.getRestaurantTable();
            table.setStatus("AVAILABLE");
            tableRepository.save(table);
            // Re-fetch to return the updated entity
            return orderRepository.findByIdAndTenantId(id, tenantId).orElse(order);
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        return orderRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public List<Order> getOrdersByBranchId(Long branchId) {
        return orderRepository.findByBranchId(branchId);
    }

    @Override
    public List<Order> getActiveOrdersByBranchId(Long branchId) {
        return orderRepository.findByBranchIdAndStatus(branchId, "PENDING"); // Simplification. Might want NOT IN
                                                                             // ('PAID')
    }

    @Override
    @Transactional
    public Order addItemsToOrder(Long orderId, List<OrderItem> items) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        Order order = orderRepository.findByIdAndTenantId(orderId, tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Order not found"));

        items.forEach(item -> {
            item.setOrder(order);

            if (item.getProduct() != null && item.getProduct().getId() != null) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                                "Product not found"));

                // Deduct inventory from the default warehouse
                processInventoryAdjustment(order.getBranch().getId(), product.getId(), item.getQuantity(),
                        StockMovement.MovementType.OUT, "Venta: Items adicionales Orden #" + order.getId(),
                        order.getUser().getId());

                item.setProduct(product);
                if (item.getUnitPrice() == null) {
                    item.setUnitPrice(product.getPrice());
                }
                item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }

            order.getItems().add(item);
        });

        BigDecimal newTotal = order.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(newTotal);

        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getActiveOrderByTableId(Long tableId) {
        // Find the active order for the table
        List<Order> orders = orderRepository.findByRestaurantTableIdAndStatusNot(tableId, "PAID");
        if (orders.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(orders.get(0));
    }

    @Override
    @Transactional
    public Order updateOrder(Long orderId, Order updatedOrder) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        Order existingOrder = orderRepository.findByIdAndTenantId(orderId, tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                        "Order not found with id " + orderId));

        // Ensure we are working with an active order
        if ("PAID".equals(existingOrder.getStatus())) {
            throw new com.restaurante.backend.exception.BusinessLogicException("Cannot update a paid order");
        }

        // Restore stock for existing items before clearing them
        existingOrder.getItems().forEach(oldItem -> {
            processInventoryAdjustment(existingOrder.getBranch().getId(), oldItem.getProduct().getId(),
                    oldItem.getQuantity(), StockMovement.MovementType.IN,
                    "Venta: Edición Orden #" + existingOrder.getId() + " (Restauración)",
                    existingOrder.getUser().getId());
        });

        // Clear existing items using orphanRemoval to sync with database
        existingOrder.getItems().clear();

        // Process new items from updatedOrder
        if (updatedOrder.getItems() != null && !updatedOrder.getItems().isEmpty()) {
            updatedOrder.getItems().forEach(item -> {
                item.setOrder(existingOrder);
                item.setStatus("PENDING");

                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                                "Product not found"));

                // Deduct stock for new items
                processInventoryAdjustment(existingOrder.getBranch().getId(), product.getId(), item.getQuantity(),
                        StockMovement.MovementType.OUT, "Venta: Edición Orden #" + existingOrder.getId() + " (Nuevo)",
                        existingOrder.getUser().getId());

                item.setProduct(product);

                // Keep the calculation logical here
                item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

                existingOrder.getItems().add(item);
            });
        }

        // Recalculate Total with discount
        BigDecimal subtotal = existingOrder.getItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Update customer if provided
        if (updatedOrder.getCustomer() != null && updatedOrder.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(updatedOrder.getCustomer().getId())
                    .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                            "Customer not found"));
            existingOrder.setCustomer(customer);
        } else if (updatedOrder.getCustomer() == null) {
            // Optional: allow unlinking customer if explicit null is sent
            // existingOrder.setCustomer(null);
        }

        // Accept discount fields from request
        if (updatedOrder.getDiscountType() != null) {
            existingOrder.setDiscountType(updatedOrder.getDiscountType());
            existingOrder.setDiscountValue(updatedOrder.getDiscountValue());
        }

        BigDecimal discountAmount = calculateDiscount(subtotal, existingOrder.getDiscountType(),
                existingOrder.getDiscountValue());
        existingOrder.setTotal(subtotal.subtract(discountAmount));

        return orderRepository.save(existingOrder);
    }

    /** Helper: calculates discount amount given a subtotal */
    private BigDecimal calculateDiscount(BigDecimal subtotal, String discountType, BigDecimal discountValue) {
        if (discountType == null || discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if ("PERCENT".equals(discountType)) {
            return subtotal.multiply(discountValue).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        }
        if ("FIXED".equals(discountType)) {
            return discountValue.min(subtotal); // cannot discount more than the total
        }
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        Order order = orderRepository.findByIdAndTenantId(id, tenantId).orElse(null);
        if (order != null) {
            order.getItems().forEach(oldItem -> {
                processInventoryAdjustment(order.getBranch().getId(), oldItem.getProduct().getId(),
                        oldItem.getQuantity(), StockMovement.MovementType.IN,
                        "Venta: Eliminación Orden #" + order.getId() + " (Restauración)", order.getUser().getId());
            });
            orderRepository.deleteByIdAndTenantId(id, tenantId);
        }
    }

    /** Helper to find the default warehouse for a branch and adjust inventory */
    private void processInventoryAdjustment(Long branchId, Long productId, int quantity,
            StockMovement.MovementType type, String reason, Long userId) {
        
        // 1. Deduct the product itself (standard behavior + syncs POS quantity)
        Warehouse warehouse = warehouseRepository.findFirstByBranchIdAndIsDefaultTrue(branchId)
                .orElseGet(() -> {
                    List<Warehouse> list = warehouseRepository.findByBranchId(branchId);
                    if (list.isEmpty()) {
                        throw new com.restaurante.backend.exception.BusinessLogicException(
                                "No se encontró ninguna bodega operativa para la sucursal.");
                    }
                    return list.get(0);
                });

        inventoryService.adjustStock(warehouse.getId(), productId, quantity, type, reason, userId);

        // 2. If it has a recipe (or it's a combo), deduct each component recursively
        List<ProductRecipe> recipes = recipeRepository.findByProductId(productId);
        
        if (recipes != null && !recipes.isEmpty()) {
            for (ProductRecipe recipe : recipes) {
                // Calculate quantity needed (recipe quantity * amount sold)
                int quantityNeeded = recipe.getQuantity().multiply(java.math.BigDecimal.valueOf(quantity)).intValue();
                
                if (quantityNeeded > 0) {
                    processInventoryAdjustment(branchId, recipe.getIngredient().getId(), quantityNeeded, type, 
                        reason + " (Ingrediente de: " + productId + ")", userId);
                }
            }
        }
    }
}
