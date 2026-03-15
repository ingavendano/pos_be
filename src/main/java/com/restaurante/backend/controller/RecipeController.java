package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.ProductRecipe;
import com.restaurante.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductRecipe>> getRecipesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(recipeService.getRecipesByProduct(productId));
    }

    @PostMapping("/product/{productId}/ingredient/{ingredientId}")
    public ResponseEntity<ProductRecipe> addIngredient(
            @PathVariable Long productId,
            @PathVariable Long ingredientId,
            @RequestParam BigDecimal quantity,
            @RequestParam Long tenantId) {
        return new ResponseEntity<>(recipeService.addIngredientToRecipe(productId, ingredientId, quantity, tenantId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeIngredient(@PathVariable Long id) {
        recipeService.removeIngredientFromRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
