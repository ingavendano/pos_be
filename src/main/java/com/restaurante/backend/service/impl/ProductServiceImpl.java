package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.dto.ProductDTO;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@lombok.extern.slf4j.Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductDTO createProduct(Long tenantId, Long categoryId, ProductDTO productDTO) {
        log.info("Creating new product: {} for tenant: {}", productDTO.getName(), tenantId);
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Tenant not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .isAvailable(productDTO.getIsAvailable() != null ? productDTO.getIsAvailable() : true)
                .isSellable(productDTO.getIsSellable() != null ? productDTO.getIsSellable() : true)
                .quantity(productDTO.getQuantity() != null ? productDTO.getQuantity() : 0)
                .minStock(productDTO.getMinStock() != null ? productDTO.getMinStock() : 0)
                .imageUrl(productDTO.getImageUrl())
                .productionCost(productDTO.getProductionCost())
                .tenant(tenant)
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);

        // Automatic Inventory Loading
        if (savedProduct.getQuantity() != null && savedProduct.getQuantity() > 0) {
            initializeProductStockInWarehouse(savedProduct);
        }

        return mapToDTO(savedProduct);
    }

    private void initializeProductStockInWarehouse(Product product) {
        log.debug("Initializing stock for product: {} in default warehouse", product.getId());
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
        } else {
            log.warn("No default warehouse found to initialize stock for product: {}", product.getId());
        }
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, Long categoryId, ProductDTO productDTO) {
        log.info("Updating product id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Product not found"));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        if (productDTO.getIsAvailable() != null) product.setIsAvailable(productDTO.getIsAvailable());
        if (productDTO.getIsSellable() != null) product.setIsSellable(productDTO.getIsSellable());
        product.setImageUrl(productDTO.getImageUrl());
        if (productDTO.getQuantity() != null) {
            int oldQty = product.getQuantity() != null ? product.getQuantity() : 0;
            int newQty = productDTO.getQuantity();
            if (oldQty != newQty) {
                int delta = newQty - oldQty;
                updateInventoryForProduct(product, delta, "Ajuste manual desde edición de producto");
            }
            // The sync inside updateInventoryForProduct will set the final product.quantity
        }
        if (productDTO.getMinStock() != null) {
            product.setMinStock(productDTO.getMinStock());
        }
        if (productDTO.getProductionCost() != null) {
            product.setProductionCost(productDTO.getProductionCost());
        }

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                            "Category not found"));
            product.setCategory(category);
        }

        return mapToDTO(productRepository.save(product));
    }

    @Override
    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    @Transactional
    public ProductDTO restockProduct(Long id, Integer quantityToAdd) {
        Product product = productRepository.findById(id)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Product not found"));

        if (quantityToAdd == null || quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }

        if (quantityToAdd != null && quantityToAdd > 0) {
            updateInventoryForProduct(product, quantityToAdd, "Reabastecimiento de producto");
        }

        return mapToDTO(product);
    }

    @Override
    public List<ProductDTO> getProductsByTenantId(Long tenantId) {
        return productRepository.findByTenantId(tenantId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private void updateInventoryForProduct(Product product, int delta, String reason) {
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
                    Math.abs(delta),
                    delta > 0 ? StockMovement.MovementType.IN : StockMovement.MovementType.OUT,
                    reason,
                    userId);
        } else {
            log.warn("No warehouse found to adjust stock for product: {}", product.getId());
            // Fallback sync if no warehouse exists (should not happen in normal flow)
            int currentQty = product.getQuantity() != null ? product.getQuantity() : 0;
            product.setQuantity(currentQty + delta);
            productRepository.save(product);
        }
    }

    private ProductDTO mapToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .isAvailable(product.getIsAvailable())
                .quantity(product.getQuantity())
                .minStock(product.getMinStock())
                .imageUrl(product.getImageUrl())
                .isSellable(product.getIsSellable())
                .productionCost(product.getProductionCost())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .tenantId(product.getTenant().getId())
                .build();
    }
}
