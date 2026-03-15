package com.restaurante.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String phone;

    @Column
    private String email;

    @Column(length = 20)
    private String nit;

    /** NRC (Solo para Crédito Fiscal) */
    @Column(length = 30)
    private String nrc;

    @Column(length = 200)
    private String giro;

    @Column(length = 20)
    private String documentType;

    @Column(length = 20)
    private String documentNumber;

    @Column(length = 2)
    private String departamento;

    @Column(length = 2)
    private String municipio;

    @Column(length = 255)
    private String complemento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}
