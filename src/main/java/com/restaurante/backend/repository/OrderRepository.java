package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = { "restaurantTable", "user", "customer", "invoice", "items", "items.product" })
    List<Order> findByBranchId(Long branchId);

    @EntityGraph(attributePaths = { "restaurantTable", "user", "customer", "invoice" })
    Page<Order> findByBranchId(Long branchId, Pageable pageable);

    @EntityGraph(attributePaths = { "restaurantTable", "user", "customer", "invoice", "items", "items.product" })
    List<Order> findByBranchIdAndStatus(Long branchId, String status);

    @EntityGraph(attributePaths = { "restaurantTable", "user", "customer", "invoice" })
    Page<Order> findByBranchIdAndStatus(Long branchId, String status, Pageable pageable);

    @EntityGraph(attributePaths = { "restaurantTable", "user", "customer", "invoice", "items", "items.product" })
    List<Order> findByRestaurantTableIdAndStatusNot(Long tableId, String status);

    @EntityGraph(attributePaths = { "restaurantTable", "user", "customer", "invoice", "items", "items.product" })
    List<Order> findByBranchIdOrderByCreatedAtDesc(Long branchId);

    @EntityGraph(attributePaths = { "restaurantTable", "user", "customer", "invoice" })
    Page<Order> findByBranchIdOrderByCreatedAtDesc(Long branchId, Pageable pageable);

    /**
     * Kitchen Display query: load orders that are PENDING or PREPARING,
     * eagerly fetching table, user (waiter), and items + product names.
     * Ordered oldest first so urgency is clear.
     */
    @EntityGraph(attributePaths = { "restaurantTable", "user", "items", "items.product" })
    @Query("SELECT o FROM Order o WHERE o.branch.id = :branchId " +
            "AND o.status IN ('PENDING', 'PREPARING') " +
            "ORDER BY o.createdAt ASC")
    List<Order> findKitchenOrdersByBranchId(@Param("branchId") Long branchId);

    @EntityGraph(attributePaths = { "restaurantTable", "user", "items", "items.product" })
    Optional<Order> findWithItemsById(Long id);
}
