package com.restaurante.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.backend.domain.dto.dte.DtePayload;
import com.restaurante.backend.domain.entity.Invoice;
import com.restaurante.backend.domain.entity.OrderItem;
import com.restaurante.backend.service.DteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DteServiceImpl implements DteService {

    private final ObjectMapper objectMapper;

    @Override
    public String generateAndSignDte(Invoice invoice) {
        try {
            DtePayload.Identificacion ident = DtePayload.Identificacion.builder()
                    .version("1")
                    .ambiente("00") // Test
                    .tipoDte(invoice.getDteType())
                    .numeroControl(invoice.getControlNumber())
                    .codigoGeneracion(invoice.getGenerationCode())
                    .fechaEmision(invoice.getIssuedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .horaEmision(invoice.getIssuedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .build();

            DtePayload.Emisor emisor = DtePayload.Emisor.builder()
                    .nit(invoice.getTenant().getNit())
                    .nrc(invoice.getTenant().getNrc())
                    .nombre(invoice.getTenant().getName())
                    .descGiro(invoice.getTenant().getGiro())
                    .direccion(DtePayload.Direccion.builder()
                            .departamento("06") // Hardcoded for now, should come from tenant
                            .municipio("14")
                            .complemento("San Salvador")
                            .build())
                    .build();

            DtePayload.Receptor receptor = DtePayload.Receptor.builder()
                    .tipoDocumento(invoice.getCustomer() != null ? invoice.getCustomer().getDocumentType() : "36") // 36=NIT
                    .numDocumento(invoice.getCustomer() != null ? invoice.getCustomer().getDocumentNumber() : "0000-000000-000-0")
                    .nombre(invoice.getCustomer() != null ? invoice.getCustomer().getName() : "CLIENTE FINAL")
                    .build();

            var items = IntStream.range(0, invoice.getOrder().getItems().size())
                    .<DtePayload.CuerpoDocumento>mapToObj(i -> {
                        OrderItem item = invoice.getOrder().getItems().get(i);
                        return DtePayload.CuerpoDocumento.builder()
                                .numItem(i + 1)
                                .tipoItem("1") // 1=Bien, 2=Servicio
                                .cantidad(item.getQuantity())
                                .uniMedida("59") // 59=Unidad
                                .descripcion(item.getProduct().getName())
                                .precioUni(item.getUnitPrice().doubleValue())
                                .ventaGravada(item.getQuantity() * item.getUnitPrice().doubleValue())
                                .build();
                    })
                    .collect(Collectors.toList());

            DtePayload.Resumen resumen = DtePayload.Resumen.builder()
                    .totalGravada(invoice.getSubtotal().doubleValue())
                    .totalIva(invoice.getTax().doubleValue())
                    .totalPagar(invoice.getTotal().doubleValue())
                    .totalLetras("SON DOLARES")
                    .build();

            DtePayload payload = DtePayload.builder()
                    .identificacion(ident)
                    .emisor(emisor)
                    .receptor(receptor)
                    .cuerpoDocumento(items)
                    .resumen(resumen)
                    .build();

            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("Error generating DTE JSON for invoice {}: {}", invoice.getId(), e.getMessage());
            throw new RuntimeException("DTE Generation failed", e);
        }
    }

    @Override
    public void transmitDte(Invoice invoice) {
        log.info("Transmitting DTE {} to Ministerio de Hacienda...", invoice.getControlNumber());
        // Simulación de transmisión
        invoice.setDteStatus("SENT");
        invoice.setReceptionSello("S-2024-" + System.currentTimeMillis());
    }
}
