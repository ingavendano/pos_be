package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Login y /me: cargar rol y permisos para que el usuario tenga todos los permisos actualizados. */
    @EntityGraph(attributePaths = { "role", "role.permissions", "tenant", "branch" })
    Optional<User> findByUsernameAndTenantId(String username, Long tenantId);

    // Used when no tenant in context (e.g. localhost)
    @EntityGraph(attributePaths = { "role", "role.permissions", "tenant", "branch" })
    Optional<User> findByUsername(String username);

    // Used for listing users in admin page — load role and branch to avoid N+1
    @EntityGraph(attributePaths = { "role", "tenant", "branch" })
    List<User> findByTenantId(Long tenantId);

    // Used for branch-specific user lists
    @EntityGraph(attributePaths = { "role", "tenant", "branch" })
    List<User> findByBranchId(Long branchId);

    boolean existsByRoleId(Long roleId);

    @EntityGraph(attributePaths = { "role", "tenant", "branch" })
    Optional<User> findByIdAndTenantId(Long id, Long tenantId);

    void deleteByIdAndTenantId(Long id, Long tenantId);
}
