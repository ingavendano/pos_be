package com.restaurante.backend.service;

import com.restaurante.backend.domain.dto.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductDTO createProduct(Long tenantId, Long categoryId, ProductDTO productDTO);

    ProductDTO updateProduct(Long id, Long categoryId, ProductDTO productDTO);

    Optional<ProductDTO> getProductById(Long id);

    ProductDTO restockProduct(Long id, Integer quantityToAdd);

    List<ProductDTO> getProductsByTenantId(Long tenantId);

    List<ProductDTO> getProductsByCategoryId(Long categoryId);

    void deleteProduct(Long id);
}
