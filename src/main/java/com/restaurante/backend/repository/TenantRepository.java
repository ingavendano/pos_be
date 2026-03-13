package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByName(String name);

    Optional<Tenant> findByDomain(String domain);
}
