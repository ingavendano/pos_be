package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.*;
import com.restaurante.backend.dto.SetupWizardRequest;
import com.restaurante.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final TenantRepository tenantRepository;
    private final BranchRepository branchRepository;
    private final WarehouseRepository warehouseRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void runSetupWizard(SetupWizardRequest request) {
        // 1. Create Tenant
        Tenant tenant = Tenant.builder()
                .name(request.getCompanyName())
                .domain(request.getDomain())
                .currency(request.getCurrency())
                .currencySymbol(request.getCurrencySymbol())
                .build();
        tenant = tenantRepository.save(tenant);

        // 2. Create Branch
        Branch branch = Branch.builder()
                .tenant(tenant)
                .name(request.getBranchName())
                .address(request.getBranchAddress())
                .phone(request.getBranchPhone())
                .build();
        branch = branchRepository.save(branch);

        // 3. Create Principal Warehouse
        String warehouseName = (request.getWarehouseName() != null && !request.getWarehouseName().isBlank())
                ? request.getWarehouseName()
                : "Bodega Principal";
        Warehouse warehouse = Warehouse.builder()
                .name(warehouseName)
                .branch(branch)
                .tenant(tenant)
                .isDefault(true)
                .build();
        warehouseRepository.save(warehouse);

        // 4. Create Custom Roles if provided, or default ADMIN role
        Role adminRole = null;
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (SetupWizardRequest.RoleConfigRequest roleConfig : request.getRoles()) {
                Role role = Role.builder()
                        .tenant(tenant)
                        .name(roleConfig.getName())
                        .description(roleConfig.getDescription())
                        .build();
                role = roleRepository.save(role);

                if (roleConfig.getName().equalsIgnoreCase("ADMIN") || adminRole == null) {
                    adminRole = role; // set first one as admin if not explicitly named ADMIN
                }

                if (roleConfig.getPermissions() != null) {
                    for (SetupWizardRequest.PermissionRequest permRequest : roleConfig.getPermissions()) {
                        RolePermission permission = RolePermission.builder()
                                .role(role)
                                .component(permRequest.getComponent())
                                .canRead(permRequest.getCanRead())
                                .canWrite(permRequest.getCanWrite())
                                .canDelete(permRequest.getCanDelete())
                                .build();
                        rolePermissionRepository.save(permission);
                    }
                }
            }
        } else {
            // Default ADMIN role
            adminRole = Role.builder()
                    .tenant(tenant)
                    .name("ADMIN")
                    .description("Administrator")
                    .build();
            adminRole = roleRepository.save(adminRole);
        }

        // 4. Create Admin User
        User adminUser = User.builder()
                .tenant(tenant)
                .branch(branch)
                .name(request.getAdminName())
                .username(request.getAdminUsername())
                .password(passwordEncoder.encode(request.getAdminPassword()))
                .role(adminRole)
                .build();
        userRepository.save(adminUser);
    }
}
