package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Invoice;

public interface DteService {
    /**
     * Genera el JSON firmado para el Ministerio de Hacienda.
     */
    String generateAndSignDte(Invoice invoice);

    /**
     * Transmite el DTE al API de Hacienda.
     */
    void transmitDte(Invoice invoice);
}
