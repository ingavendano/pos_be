-- Migración para añadir índices de rendimiento en columnas críticas
-- Optimización de consultas comunes por Tenant, Categoría y Órdenes

-- Índices en la tabla Products
CREATE INDEX idx_products_tenant_id ON products(tenant_id);
CREATE INDEX idx_products_category_id ON products(category_id);

-- Índices en la tabla Orders
CREATE INDEX idx_orders_tenant_id ON orders(tenant_id);
CREATE INDEX idx_orders_branch_id ON orders(branch_id);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Índices en la tabla Order Items
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

-- Índices en la tabla Invoices
CREATE INDEX idx_invoices_order_id ON invoices(order_id);

-- Índices en la tabla Tenants (frecuentemente buscados por dominio)
CREATE INDEX idx_tenants_domain ON tenants(domain);
