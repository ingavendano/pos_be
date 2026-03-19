package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Product;
import com.restaurante.backend.domain.entity.ProductRecipe;
import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.exception.ResourceNotFoundException;
import com.restaurante.backend.repository.ProductRecipeRepository;
import com.restaurante.backend.repository.ProductRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeServiceImpl implements RecipeService {

    private final ProductRecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;
    private final TenantSecurityService tenantSecurityService;

    @Override
    public List<ProductRecipe> getRecipesByProduct(Long productId) {
        return recipeRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public ProductRecipe addIngredientToRecipe(Long productId, Long ingredientId, BigDecimal quantity, Long tenantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Product ingredient = productRepository.findById(ingredientId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient product not found"));
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        ProductRecipe recipe = ProductRecipe.builder()
                .product(product)
                .ingredient(ingredient)
                .quantity(quantity)
                .tenant(tenant)
                .build();

        ProductRecipe saved = recipeRepository.save(recipe);
        updateProductProductionCost(product);
        return saved;
    }

    @Override
    @Transactional
    public void removeIngredientFromRecipe(Long recipeId) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        ProductRecipe recipe = recipeRepository.findByIdAndTenantId(recipeId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe item not found"));
        Product product = recipe.getProduct();
        recipeRepository.deleteByIdAndTenantId(recipeId, tenantId);
        updateProductProductionCost(product);
    }

    private void updateProductProductionCost(Product product) {
        List<ProductRecipe> recipes = recipeRepository.findByProductId(product.getId());
        BigDecimal totalCost = recipes.stream()
                .map(r -> r.getIngredient().getPrice().multiply(r.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        product.setProductionCost(totalCost);
        productRepository.save(product);
    }
}
