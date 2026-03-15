-- Mejoras para soportar Facturación Electrónica (DTE) El Salvador

-- Actualización de la tabla Customers
ALTER TABLE customers ADD COLUMN IF NOT EXISTS document_type VARCHAR(20); -- DUI, NIT, Pasaporte
ALTER TABLE customers ADD COLUMN IF NOT EXISTS document_number VARCHAR(20);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS nrc VARCHAR(20);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS giro VARCHAR(255);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS departamento VARCHAR(2); -- Código MH (ej: 06)
ALTER TABLE customers ADD COLUMN IF NOT EXISTS municipio VARCHAR(2); -- Código MH (ej: 14)
ALTER TABLE customers ADD COLUMN IF NOT EXISTS complemento VARCHAR(255); -- Dirección detalle

-- Actualización de la tabla Invoices
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS dte_status VARCHAR(20) DEFAULT 'PENDING'; -- PENDING, SENT, REJECTED, INVALIDATED
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS dte_json TEXT;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS invalidation_sello VARCHAR(40);
