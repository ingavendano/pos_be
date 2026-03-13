-- Create base entities for integration tests.

INSERT INTO tenants (id, created_at, currency, currency_symbol, domain, name, is_active) VALUES (1, CURRENT_TIMESTAMP, 'USD', '$', 'localhost', 'Restaurante Central', true);

INSERT INTO branches (id, address, name, tenant_id) VALUES (1, '123 Av Principal', 'Sucursal Norte', 1);

INSERT INTO roles (id, description, name, tenant_id) VALUES (1, 'Admin', 'ADMIN', 1);

INSERT INTO users (id, name, password, username, branch_id, role_id, tenant_id, is_active) VALUES (1, 'Administrador', 'hashed', 'admin', 1, 1, 1, true);

INSERT INTO categories (id, description, name, tenant_id) VALUES (1, 'Cat 1', 'Bebidas', 1);
INSERT INTO categories (id, description, name, tenant_id) VALUES (2, 'Cat 2', 'Platos', 1);

INSERT INTO products (id, is_available, name, price, category_id, tenant_id) VALUES (1, true, 'Limonada', 3.50, 1, 1);
INSERT INTO products (id, is_available, name, price, category_id, tenant_id) VALUES (4, true, 'Hamburguesa', 12.00, 2, 1);

INSERT INTO tables (id, capacity, number, status, branch_id) VALUES (1, 4, 1, 'AVAILABLE', 1);

-- We also need a tax for some calculations maybe?
INSERT INTO taxes (id, is_active, name, percentage, tenant_id) VALUES (1, true, 'IVA', 16.00, 1);

INSERT INTO warehouses (id, is_default, name, branch_id, tenant_id) VALUES (1, true, 'Central', 1, 1);

INSERT INTO inventory (id, quantity, product_id, warehouse_id) VALUES (1, 100, 1, 1);
INSERT INTO inventory (id, quantity, product_id, warehouse_id) VALUES (2, 100, 4, 1);
