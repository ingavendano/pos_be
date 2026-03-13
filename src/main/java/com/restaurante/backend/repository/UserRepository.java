package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndTenantId(String username, Long tenantId);

    // Used during JWT authentication — eagerly load everything needed for security
    // context
    @EntityGraph(attributePaths = { "role", "role.permissions", "tenant", "branch" })
    Optional<User> findByUsername(String username);

    // Used for listing users in admin page — load role and branch to avoid N+1
    @EntityGraph(attributePaths = { "role", "tenant", "branch" })
    List<User> findByTenantId(Long tenantId);

    // Used for branch-specific user lists
    @EntityGraph(attributePaths = { "role", "tenant", "branch" })
    List<User> findByBranchId(Long branchId);

    boolean existsByRoleId(Long roleId);
}
