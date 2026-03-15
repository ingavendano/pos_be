package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.ProductRecipe;
import java.util.List;

public interface RecipeService {
    List<ProductRecipe> getRecipesByProduct(Long productId);
    ProductRecipe addIngredientToRecipe(Long productId, Long ingredientId, java.math.BigDecimal quantity, Long tenantId);
    void removeIngredientFromRecipe(Long recipeId);
}
