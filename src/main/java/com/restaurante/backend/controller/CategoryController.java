package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Category;
import com.restaurante.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<Category> createCategory(@PathVariable Long tenantId, @RequestBody Category category) {
        return new ResponseEntity<>(categoryService.createCategory(tenantId, category), HttpStatus.CREATED);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Category>> getCategoriesByTenantId(@PathVariable Long tenantId) {
        return ResponseEntity.ok(categoryService.getCategoriesByTenantId(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
