package com.restaurante.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetupWizardRequest {

    // Tenant info
    private String companyName;
    private String domain;
    private String currency;
    private String currencySymbol;

    // First branch info
    private String branchName;
    private String branchAddress;
    private String branchPhone;

    // Principal warehouse info
    private String warehouseName;

    // Admin user info
    private String adminName;
    private String adminUsername;
    private String adminPassword;

    // Optional dynamic roles config
    // If not provided, we can seed default roles (ADMIN, WAITER, etc.)
    private List<RoleConfigRequest> roles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleConfigRequest {
        private String name;
        private String description;
        private List<PermissionRequest> permissions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionRequest {
        private String component; // e.g., "DASHBOARD", "PRODUCTS", "POS"
        private Boolean canRead;
        private Boolean canWrite;
        private Boolean canDelete;
    }
}
