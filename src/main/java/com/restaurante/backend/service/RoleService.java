package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Role;
import com.restaurante.backend.domain.entity.RolePermission;
import com.restaurante.backend.dto.RoleRequest;
import com.restaurante.backend.dto.RoleResponse;
import com.restaurante.backend.repository.RoleRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

        private final RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final TenantRepository tenantRepository;

        @Transactional(readOnly = true)
        public List<RoleResponse> getRolesByTenant(Long tenantId) {
                return roleRepository.findByTenantId(tenantId).stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public RoleResponse getRoleById(Long id, Long tenantId) {
                Role role = roleRepository.findById(id)
                                .filter(r -> r.getTenant().getId().equals(tenantId))
                                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                                                "Rol no encontrado"));
                return mapToResponse(role);
        }

        @Transactional
        public RoleResponse createRole(Long tenantId, RoleRequest request) {
                var tenant = tenantRepository.getReferenceById(tenantId);

                Role role = Role.builder()
                                .name(request.getName())
                                .description(request.getDescription())
                                .tenant(tenant)
                                .build();

                List<RolePermission> permissions = request.getPermissions().stream()
                                .map(p -> RolePermission.builder()
                                                .role(role)
                                                .component(p.getComponent())
                                                .canRead(p.getCanRead() != null ? p.getCanRead() : false)
                                                .canWrite(p.getCanWrite() != null ? p.getCanWrite() : false)
                                                .canDelete(p.getCanDelete() != null ? p.getCanDelete() : false)
                                                .build())
                                .collect(Collectors.toList());

                role.setPermissions(permissions);
                return mapToResponse(roleRepository.save(role));
        }

        @Transactional
        public RoleResponse updateRole(Long id, Long tenantId, RoleRequest request) {
                Role role = roleRepository.findById(id)
                                .filter(r -> r.getTenant().getId().equals(tenantId))
                                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                                                "Rol no encontrado"));

                role.setName(request.getName());
                role.setDescription(request.getDescription());

                role.getPermissions().clear();

                List<RolePermission> permissions = request.getPermissions().stream()
                                .map(p -> RolePermission.builder()
                                                .role(role)
                                                .component(p.getComponent())
                                                .canRead(p.getCanRead() != null ? p.getCanRead() : false)
                                                .canWrite(p.getCanWrite() != null ? p.getCanWrite() : false)
                                                .canDelete(p.getCanDelete() != null ? p.getCanDelete() : false)
                                                .build())
                                .collect(Collectors.toList());

                role.getPermissions().addAll(permissions);

                return mapToResponse(roleRepository.save(role));
        }

        @Transactional
        public void deleteRole(Long id, Long tenantId) {
                Role role = roleRepository.findById(id)
                                .filter(r -> r.getTenant().getId().equals(tenantId))
                                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                                                "Rol no encontrado"));

                if (userRepository.existsByRoleId(id)) {
                        throw new com.restaurante.backend.exception.BusinessLogicException(
                                        "No se puede eliminar el rol porque tiene usuarios asignados.");
                }

                roleRepository.delete(role);
        }

        private RoleResponse mapToResponse(Role role) {
                List<RoleResponse.PermissionResponse> permissionResponses = role.getPermissions().stream()
                                .map(p -> RoleResponse.PermissionResponse.builder()
                                                .id(p.getId())
                                                .component(p.getComponent())
                                                .canRead(p.getCanRead())
                                                .canWrite(p.getCanWrite())
                                                .canDelete(p.getCanDelete())
                                                .build())
                                .collect(Collectors.toList());

                return RoleResponse.builder()
                                .id(role.getId())
                                .name(role.getName())
                                .description(role.getDescription())
                                .permissions(permissionResponses)
                                .build();
        }
}
