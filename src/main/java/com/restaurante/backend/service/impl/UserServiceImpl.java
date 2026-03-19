package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Branch;
import com.restaurante.backend.domain.entity.Role;
import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.domain.entity.User;
import com.restaurante.backend.exception.ResourceNotFoundException;
import com.restaurante.backend.repository.BranchRepository;
import com.restaurante.backend.repository.RoleRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.repository.UserRepository;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final BranchRepository branchRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantSecurityService tenantSecurityService;

    @Override
    @Transactional
    public User createUser(Long tenantId, Long branchId, User user) {
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        // Load the Role from the database using its name and tenantId to avoid transient entity error
        if (user.getRole() != null && user.getRole().getName() != null) {
            String roleName = user.getRole().getName();
            Role role = roleRepository.findByNameAndTenantId(roleName, tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
            user.setRole(role);
        } else if (user.getRole() != null && user.getRole().getId() != null) {
            Role role = roleRepository.findById(user.getRole().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + user.getRole().getId()));
            user.setRole(role);
        }

        if (branchId != null) {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
            user.setBranch(branch);
        }

        // Encode password if provided
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        user.setTenant(tenant);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, Long branchId, User userDetails) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        User user = userRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }

        // Update branch using explicit branchId parameter
        if (branchId != null) {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
            user.setBranch(branch);
        } else {
            // branchId == null means no specific branch (global access)
            user.setBranch(null);
        }

        // Load the managed Role entity to avoid transient entity error
        if (userDetails.getRole() != null) {
            Role managedRole = null;
            if (userDetails.getRole().getId() != null) {
                managedRole = roleRepository.findById(userDetails.getRole().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            } else if (userDetails.getRole().getName() != null) {
                managedRole = roleRepository.findByNameAndTenantId(
                        userDetails.getRole().getName(), user.getTenant().getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Role not found: " + userDetails.getRole().getName()));
            }
            if (managedRole != null) {
                user.setRole(managedRole);
            }
        }

        if (userDetails.getIsActive() != null) {
            user.setIsActive(userDetails.getIsActive());
        }

        // Re-encode password only if a new one is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        return userRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public Optional<User> getUserByUsernameAndTenantId(String username, Long tenantId) {
        return userRepository.findByUsernameAndTenantId(username, tenantId);
    }

    @Override
    public List<User> getUsersByTenantId(Long tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    @Override
    public List<User> getUsersByBranchId(Long branchId) {
        return userRepository.findByBranchId(branchId);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        User user = userRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }
}
