package com.restaurante.backend.security;

import com.restaurante.backend.repository.BranchRepository;
import com.restaurante.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Central service to validate tenant ownership of resources.
 * Use this in any controller/service to prevent cross-tenant data access.
 */
@Service
@RequiredArgsConstructor
public class TenantSecurityService {

    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    /**
     * Returns the tenantId of the currently authenticated user or identified domain.
     */
    public Long getCurrentTenantId() {
        // Try context first (identified by domain)
        Long contextId = TenantContext.getCurrentTenantId();
        if (contextId != null) {
            return contextId;
        }

        // Fallback to authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getTenantId();
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .map(u -> u.getTenant().getId())
                .orElseThrow(() -> new SecurityException("No se pudo identificar la empresa (Tenant)."));
    }

    /**
     * Throws a SecurityException if the given tenantId does not match the current
     * user's tenant.
     */
    public void verifyTenantAccess(Long tenantId) {
        Long currentTenantId = getCurrentTenantId();
        if (!currentTenantId.equals(tenantId)) {
            throw new SecurityException("Acceso denegado: no perteneces a este tenant.");
        }
    }

    /**
     * Throws a SecurityException if the given branch does not belong to the current
     * user's tenant.
     */
    public void verifyBranchAccess(Long branchId) {
        Long currentTenantId = getCurrentTenantId();
        branchRepository.findById(branchId).ifPresent(branch -> {
            if (!branch.getTenant().getId().equals(currentTenantId)) {
                throw new SecurityException("Acceso denegado: esta sucursal no pertenece a tu empresa.");
            }
        });
    }
}
