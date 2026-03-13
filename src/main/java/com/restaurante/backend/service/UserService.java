package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(Long tenantId, Long branchId, User user);

    User updateUser(Long id, Long branchId, User userDetails);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsernameAndTenantId(String username, Long tenantId);

    List<User> getUsersByTenantId(Long tenantId);

    List<User> getUsersByBranchId(Long branchId);

    void deactivateUser(Long id);
}
