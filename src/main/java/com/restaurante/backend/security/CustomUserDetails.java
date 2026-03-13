package com.restaurante.backend.security;

import com.restaurante.backend.domain.entity.User;
import com.restaurante.backend.dto.PermissionDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String name;
    private final String roleName;
    private final Long tenantId;
    private final Long branchId;
    private final String branchName;
    private final boolean isActive;
    private final List<PermissionDto> permissions;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.name = user.getName();
        this.roleName = user.getRole().getName();
        this.tenantId = user.getTenant().getId();
        this.branchId = user.getBranch() != null ? user.getBranch().getId() : null;
        this.branchName = user.getBranch() != null ? user.getBranch().getName() : "Global";
        this.isActive = user.getIsActive() != null ? user.getIsActive() : true;
        
        this.permissions = user.getRole().getPermissions().stream()
                .map(p -> PermissionDto.builder()
                        .component(p.getComponent())
                        .canRead(p.getCanRead())
                        .canWrite(p.getCanWrite())
                        .canDelete(p.getCanDelete())
                        .build())
                .collect(Collectors.toList());
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getRoleName() { return roleName; }
    public Long getTenantId() { return tenantId; }
    public Long getBranchId() { return branchId; }
    public String getBranchName() { return branchName; }
    public List<PermissionDto> getPermissions() { return permissions; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
