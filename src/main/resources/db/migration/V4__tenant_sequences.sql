-- Add tenant_id and consecutive_number to orders
ALTER TABLE orders ADD COLUMN tenant_id BIGINT;
ALTER TABLE orders ADD COLUMN consecutive_number BIGINT;

-- Fill tenant_id from branches
UPDATE orders o
SET tenant_id = b.tenant_id
FROM branches b
WHERE o.branch_id = b.id;

-- Make tenant_id NOT NULL and add foreign key
ALTER TABLE orders ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE orders ADD CONSTRAINT fk_orders_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);

-- Create sequences table
CREATE TABLE tenant_sequences (
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    entity_name VARCHAR(50) NOT NULL,
    current_value BIGINT DEFAULT 0,
    PRIMARY KEY (tenant_id, entity_name)
);

-- Function to set consecutive_number
CREATE OR REPLACE FUNCTION fn_get_next_tenant_sequence()
RETURNS TRIGGER AS $$
DECLARE
    next_val BIGINT;
BEGIN
    INSERT INTO tenant_sequences (tenant_id, entity_name, current_value)
    VALUES (NEW.tenant_id, TG_TABLE_NAME, 1)
    ON CONFLICT (tenant_id, entity_name)
    DO UPDATE SET current_value = tenant_sequences.current_value + 1
    RETURNING current_value INTO next_val;

    NEW.consecutive_number := next_val;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for orders
CREATE TRIGGER trg_orders_consecutive
BEFORE INSERT ON orders
FOR EACH ROW
EXECUTE FUNCTION fn_get_next_tenant_sequence();

-- Initialize existing orders sequences
WITH ranked_orders AS (
    SELECT id, tenant_id, row_number() OVER (PARTITION BY tenant_id ORDER BY id) as rank
    FROM orders
)
UPDATE orders o
SET consecutive_number = r.rank
FROM ranked_orders r
WHERE o.id = r.id;

-- Initialize tenant_sequences table for existing data
INSERT INTO tenant_sequences (tenant_id, entity_name, current_value)
SELECT tenant_id, 'orders', COALESCE(MAX(consecutive_number), 0)
FROM orders
GROUP BY tenant_id
ON CONFLICT (tenant_id, entity_name) DO UPDATE SET current_value = EXCLUDED.current_value;
