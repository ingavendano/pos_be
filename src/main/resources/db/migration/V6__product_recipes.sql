-- Create table for recipes/escandallos
CREATE TABLE product_recipes (
    id SERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    ingredient_id BIGINT NOT NULL REFERENCES products(id),
    quantity DECIMAL(10,4) NOT NULL,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    CONSTRAINT uk_product_ingredient UNIQUE (product_id, ingredient_id)
);

-- Add sellable flag to products
ALTER TABLE products ADD COLUMN is_sellable BOOLEAN DEFAULT TRUE;
