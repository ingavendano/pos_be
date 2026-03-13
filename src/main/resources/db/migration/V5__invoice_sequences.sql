-- Add tenant_id and consecutive_number to invoices
ALTER TABLE invoices ADD COLUMN tenant_id BIGINT;
ALTER TABLE invoices ADD COLUMN consecutive_number BIGINT;

-- Fill tenant_id from orders
UPDATE invoices i
SET tenant_id = o.tenant_id
FROM orders o
WHERE i.order_id = o.id;

-- Make tenant_id NOT NULL and add foreign key
ALTER TABLE invoices ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE invoices ADD CONSTRAINT fk_invoices_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);

-- Trigger for invoices
CREATE TRIGGER trg_invoices_consecutive
BEFORE INSERT ON invoices
FOR EACH ROW
EXECUTE FUNCTION fn_get_next_tenant_sequence();

-- Initialize existing invoices sequences
WITH ranked_invoices AS (
    SELECT id, tenant_id, row_number() OVER (PARTITION BY tenant_id ORDER BY id) as rank
    FROM invoices
)
UPDATE invoices i
SET consecutive_number = r.rank
FROM ranked_invoices r
WHERE i.id = r.id;

-- Initialize tenant_sequences table for existing data
INSERT INTO tenant_sequences (tenant_id, entity_name, current_value)
SELECT tenant_id, 'invoices', COALESCE(MAX(consecutive_number), 0)
FROM invoices
GROUP BY tenant_id
ON CONFLICT (tenant_id, entity_name) DO UPDATE SET current_value = EXCLUDED.current_value;
