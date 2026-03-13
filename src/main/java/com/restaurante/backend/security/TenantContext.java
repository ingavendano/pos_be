package com.restaurante.backend.security;

import com.restaurante.backend.domain.entity.Tenant;

/**
 * Utility to store and retrieve the current tenant in a thread-local variable.
 * This allows us to access the tenant from anywhere in the current request.
 */
public class TenantContext {
    private static final ThreadLocal<Tenant> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(Tenant tenant) {
        currentTenant.set(tenant);
    }

    public static Tenant getCurrentTenant() {
        return currentTenant.get();
    }

    public static Long getCurrentTenantId() {
        Tenant t = currentTenant.get();
        return t != null ? t.getId() : null;
    }

    public static void clear() {
        currentTenant.remove();
    }
}
