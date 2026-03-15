package com.restaurante.backend.domain.entity;

public enum TaxType {
    STANDARD,  // Adds to total (e.g. IVA)
    TIP,       // Adds to total (Calculated on subtotal)
    RETENTION  // Subtracts from total to pay (informative/deductible)
}
