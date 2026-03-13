package com.restaurante.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String component; // e.g., "DASHBOARD", "PRODUCTS", "ORDERS", "POS"

    @Column(nullable = false)
    @Builder.Default
    private Boolean canRead = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean canWrite = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean canDelete = false;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
