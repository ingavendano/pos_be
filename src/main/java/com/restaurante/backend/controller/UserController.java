package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.User;
import com.restaurante.backend.dto.UserResponseDto;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TenantSecurityService tenantSecurity;

    @PostMapping("/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(
            @PathVariable Long tenantId,
            @RequestParam(required = false) Long branchId,
            @Valid @RequestBody User user) {
        tenantSecurity.verifyTenantAccess(tenantId);
        User createdUser = userService.createUser(tenantId, branchId, user);
        return new ResponseEntity<>(UserResponseDto.fromEntity(createdUser), HttpStatus.CREATED);
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByTenantId(@PathVariable Long tenantId) {
        tenantSecurity.verifyTenantAccess(tenantId);
        List<UserResponseDto> dtos = userService.getUsersByTenantId(tenantId).stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByBranchId(@PathVariable Long branchId) {
        tenantSecurity.verifyBranchAccess(branchId);
        List<UserResponseDto> dtos = userService.getUsersByBranchId(branchId).stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserResponseDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) Long branchId,
            @RequestBody User user) {
        User updatedUser = userService.updateUser(id, branchId, user);
        return ResponseEntity.ok(UserResponseDto.fromEntity(updatedUser));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
