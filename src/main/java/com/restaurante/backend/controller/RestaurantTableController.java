package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.RestaurantTable;
import com.restaurante.backend.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    @PostMapping("/branch/{branchId}")
    public ResponseEntity<RestaurantTable> createTable(@PathVariable Long branchId,
            @RequestBody RestaurantTable table) {
        return new ResponseEntity<>(tableService.createTable(branchId, table), HttpStatus.CREATED);
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<RestaurantTable>> getTablesByBranchId(@PathVariable Long branchId) {
        return ResponseEntity.ok(tableService.getTablesByBranchId(branchId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTable> getTableById(@PathVariable Long id) {
        return tableService.getTableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTable> updateTable(@PathVariable Long id, @RequestBody RestaurantTable table) {
        return ResponseEntity.ok(tableService.updateTable(id, table));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RestaurantTable> updateTableStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(tableService.updateTableStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
