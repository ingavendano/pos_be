package com.restaurante.backend.controller;

import com.restaurante.backend.domain.dto.CategoryDTO;
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
    public ResponseEntity<CategoryDTO> createCategory(@PathVariable Long tenantId, @RequestBody CategoryDTO categoryDTO) {
        return new ResponseEntity<>(categoryService.createCategory(tenantId, categoryDTO), HttpStatus.CREATED);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByTenantId(@PathVariable Long tenantId) {
        return ResponseEntity.ok(categoryService.getCategoriesByTenantId(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
