package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Product;
import com.restaurante.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/tenant/{tenantId}/category/{categoryId}")
    public ResponseEntity<Product> createProduct(
            @PathVariable Long tenantId,
            @PathVariable Long categoryId,
            @RequestBody Product product) {
        return new ResponseEntity<>(productService.createProduct(tenantId, categoryId, product), HttpStatus.CREATED);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Product>> getProductsByTenantId(@PathVariable Long tenantId) {
        return ResponseEntity.ok(productService.getProductsByTenantId(tenantId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestParam(required = false) Long categoryId,
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, categoryId, product));
    }

    @PatchMapping("/{id}/restock")
    public ResponseEntity<Product> restockProduct(
            @PathVariable Long id,
            @RequestParam Integer quantityToAdd) {
        return ResponseEntity.ok(productService.restockProduct(id, quantityToAdd));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
