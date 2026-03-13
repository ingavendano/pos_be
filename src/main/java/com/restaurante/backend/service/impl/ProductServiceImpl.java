package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Category;
import com.restaurante.backend.domain.entity.Product;
import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.domain.entity.Warehouse;
import com.restaurante.backend.domain.entity.StockMovement;
import com.restaurante.backend.repository.CategoryRepository;
import com.restaurante.backend.repository.ProductRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.repository.WarehouseRepository;
import com.restaurante.backend.service.InventoryService;
import com.restaurante.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import com.restaurante.backend.domain.entity.User;
import com.restaurante.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Product createProduct(Long tenantId, Long categoryId, Product product) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Tenant not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Category not found"));

        product.setTenant(tenant);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);

        // Automatic Inventory Loading
        if (savedProduct.getQuantity() != null && savedProduct.getQuantity() > 0) {
            initializeProductStockInWarehouse(savedProduct);
        }

        return savedProduct;
    }

    private void initializeProductStockInWarehouse(Product product) {
        // Find a default warehouse for the tenant (using any branch)
        // Since product is tenant-level, we'll try to find any default warehouse in
        // that tenant
        Warehouse warehouse = warehouseRepository.findByTenantId(product.getTenant().getId()).stream()
                .filter(Warehouse::isDefault)
                .findFirst()
                .orElseGet(() -> {
                    List<Warehouse> list = warehouseRepository.findByTenantId(product.getTenant().getId());
                    return list.isEmpty() ? null : list.get(0);
                });

        if (warehouse != null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Long userId = userRepository.findByUsername(username).map(User::getId).orElse(null);

            inventoryService.adjustStock(
                    warehouse.getId(),
                    product.getId(),
                    product.getQuantity(),
                    StockMovement.MovementType.IN,
                    "Carga inicial (Creación de producto)",
                    userId);
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Long categoryId, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Product not found"));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setIsAvailable(productDetails.getIsAvailable());
        product.setImageUrl(productDetails.getImageUrl());
        if (productDetails.getQuantity() != null) {
            product.setQuantity(productDetails.getQuantity());
        }
        if (productDetails.getMinStock() != null) {
            product.setMinStock(productDetails.getMinStock());
        }

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                            "Category not found"));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product restockProduct(Long id, Integer quantityToAdd) {
        Product product = productRepository.findById(id)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Product not found"));

        if (quantityToAdd == null || quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }

        int currentQuantity = product.getQuantity() != null ? product.getQuantity() : 0;
        product.setQuantity(currentQuantity + quantityToAdd);

        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByTenantId(Long tenantId) {
        return productRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
