package com.restaurante.backend.domain.dto.dte;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DtePayload {
    private Identificacion identificacion;
    private Emisor emisor;
    private Receptor receptor;
    private List<CuerpoDocumento> cuerpoDocumento;
    private Resumen resumen;

    @Data @Builder
    public static class Identificacion {
        private String version;
        private String ambiente; // 00=Test, 01=Prod
        private String tipoDte; // 01=FE, 03=CCF
        private String numeroControl;
        private String codigoGeneracion;
        private String fechaEmision;
        private String horaEmision;
    }

    @Data @Builder
    public static class Emisor {
        private String nit;
        private String nrc;
        private String nombre;
        private String codGiro;
        private String descGiro;
        private Direccion direccion;
    }

    @Data @Builder
    public static class Receptor {
        private String tipoDocumento;
        private String numDocumento;
        private String nrc;
        private String nombre;
        private String codGiro;
        private String descGiro;
        private Direccion direccion;
    }

    @Data @Builder
    public static class Direccion {
        private String departamento;
        private String municipio;
        private String complemento;
    }

    @Data @Builder
    public static class CuerpoDocumento {
        private Integer numItem;
        private String tipoItem;
        private Integer cantidad;
        private String uniMedida;
        private String descripcion;
        private Double precioUni;
        private Double montoDescu;
        private Double ventaNoSuj;
        private Double ventaExenta;
        private Double ventaGravada;
    }

    @Data @Builder
    public static class Resumen {
        private Double totalNoSuj;
        private Double totalExenta;
        private Double totalGravada;
        private Double subTotalVentas;
        private Double totalDescu;
        private Double totalIva;
        private Double totalPagar;
        private String totalLetras;
    }
}
