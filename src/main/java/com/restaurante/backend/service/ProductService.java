package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(Long tenantId, Long categoryId, Product product);

    Product updateProduct(Long id, Long categoryId, Product productDetails);

    Optional<Product> getProductById(Long id);

    Product restockProduct(Long id, Integer quantityToAdd);

    List<Product> getProductsByTenantId(Long tenantId);

    List<Product> getProductsByCategoryId(Long categoryId);

    void deleteProduct(Long id);
}
