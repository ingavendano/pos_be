package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByTenantId(Long tenantId);

    Optional<Role> findByNameAndTenantId(String name, Long tenantId);
}
