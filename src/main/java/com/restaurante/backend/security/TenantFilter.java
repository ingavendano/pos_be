package com.restaurante.backend.security;

import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.repository.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter that runs early in the request lifecycle to identify the tenant
 * based on the domain (Host header).
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter extends OncePerRequestFilter {

    private final TenantRepository tenantRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String host = request.getHeader("Host");
        // Remove port if present
        String domain = host != null ? host.split(":")[0] : "";
        
        log.info("Request to Host: {}, Domain identified: {}", host, domain);

        if (!domain.isEmpty()) {
            // Try exact match first
            Optional<Tenant> tenantOpt = tenantRepository.findByDomain(domain);
            
            // If not found and it's a localhost subdomain (e.g., ventaloca.localhost), try matching the subdomain part
            if (tenantOpt.isEmpty() && domain.endsWith(".localhost")) {
                String subdomain = domain.substring(0, domain.lastIndexOf(".localhost"));
                tenantOpt = tenantRepository.findByDomain(subdomain);
                if (tenantOpt.isPresent()) {
                    log.info("Found tenant by subdomain match: {} for domain: {}", tenantOpt.get().getName(), domain);
                }
            }

            if (tenantOpt.isPresent()) {
                TenantContext.setCurrentTenant(tenantOpt.get());
                log.debug("Identified tenant: {} for domain: {}", tenantOpt.get().getName(), domain);
            } else {
                log.warn("No tenant found for domain: {}", domain);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Important to clear context after request to avoid leaks in thread pool
            TenantContext.clear();
        }
    }
}
