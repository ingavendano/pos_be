package com.restaurante.backend.controller;

import com.restaurante.backend.dto.KitchenOrderResponse;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenService kitchenService;
    private final TenantSecurityService tenantSecurity;

    /**
     * Polled every N seconds by the Kitchen Display frontend.
     * Returns all PENDING and PREPARING orders for the given branch.
     */
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<KitchenOrderResponse>> getKitchenOrders(
            @PathVariable Long branchId,
            @RequestParam(required = false) List<Long> categoryIds) {
        tenantSecurity.verifyBranchAccess(branchId);
        return ResponseEntity.ok(kitchenService.getKitchenOrders(branchId, categoryIds));
    }

    /**
     * Advances an order through the status pipeline:
     * PENDING → PREPARING → READY
     */
    @PatchMapping("/orders/{orderId}/advance")
    public ResponseEntity<KitchenOrderResponse> advanceOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(kitchenService.advanceOrderStatus(orderId));
    }
}
