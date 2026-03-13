-- Seed Initial Data for Development and Testing
-- Uses ON CONFLICT DO NOTHING to avoid duplicate key errors on subsequent runs

-- 1. Create Tenant
INSERT INTO tenants (id, created_at, updated_at, currency, currency_symbol, domain, name, giro, nit, nrc)
VALUES (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USD', '$', 'localhost', 'Restaurante Central', NULL, NULL, NULL)
ON CONFLICT (id) DO NOTHING;

-- Reset tenant sequence just in case
SELECT setval('tenants_id_seq', (SELECT MAX(id) FROM tenants));

-- 2. Create Branch
INSERT INTO branches (id, created_at, updated_at, address, name, phone, tenant_id)
VALUES (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '123 Av Principal, Zona Norte', 'Sucursal Norte', NULL, 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('branches_id_seq', (SELECT MAX(id) FROM branches));

-- 2.5 Create Roles
INSERT INTO roles (id, created_at, updated_at, description, name, tenant_id)
VALUES 
  (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Administrador del sistema', 'ADMIN', 1),
  (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Mesero', 'WAITER', 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));

-- 3. Create Users
-- Passwords are "admin123" and "mesero123" hashed with BCrypt
INSERT INTO users (id, created_at, updated_at, name, password, username, branch_id, role_id, tenant_id, is_active)
VALUES 
  (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Administrador', '$2a$10$7/O9m1d5i2lD9q88b7.IauG4qYk/Fm2u0bS7U9Zk4pL9VZbL3yq9S', 'admin', 1, 1, 1, true),
  (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Mesero Juan', '$2a$10$U2c/Zq7T8W/lP9eY9iYhMe7pZ9w1VbQ9C3eR5W/V9m8oR4tP/J3x6', 'mesero', 1, 2, 1, true)
ON CONFLICT (id) DO NOTHING;

-- Ensure users_id_seq tracks MAX
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- 4. Create Taxes
INSERT INTO taxes (id, created_at, updated_at, is_active, name, percentage, tenant_id)
VALUES 
  (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 'IVA', 16.00, 1),
  (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 'Servicio', 10.00, 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('taxes_id_seq', (SELECT MAX(id) FROM taxes));

-- 5. Create Categories
INSERT INTO categories (id, created_at, updated_at, description, name, is_active, tenant_id)
VALUES 
  (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Bebidas frías y calientes', 'Bebidas', true, 1),
  (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Comida principal', 'Platos Fuertes', true, 1),
  (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Dulces y pasteles', 'Postres', true, 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));

-- 6. Create Products
INSERT INTO products (id, created_at, updated_at, description, is_active, name, price, category_id, tenant_id)
VALUES 
  (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, true, 'Limonada', 3.50, 1, 1),
  (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, true, 'Cerveza Artesanal', 5.00, 1, 1),
  (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, true, 'Refresco de Cola', 2.50, 1, 1),
  
  (4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Carne, queso, tomate y lechuga', true, 'Hamburguesa Clásica', 12.00, 2, 1),
  (5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Orden de 3 tacos', true, 'Tacos al Pastor', 9.50, 2, 1),
  (6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, true, 'Pizza Margarita', 15.00, 2, 1),
  (7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, true, 'Ensalada César', 10.00, 2, 1),
  
  (8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, true, 'Pastel de Chocolate', 6.00, 3, 1),
  (9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, true, 'Helado de Vainilla', 4.00, 3, 1),
  (10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, true, 'Tiramisú', 7.50, 3, 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));

-- 7. Create Tables
INSERT INTO restaurant_tables (id, created_at, updated_at, capacity, number, status, branch_id)
VALUES 
  (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 1, 'AVAILABLE', 1),
  (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 2, 'AVAILABLE', 1),
  (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 8, 3, 'AVAILABLE', 1),
  (4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 4, 'AVAILABLE', 1),
  (5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6, 5, 'AVAILABLE', 1),
  (6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 6, 'AVAILABLE', 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('restaurant_tables_id_seq', (SELECT MAX(id) FROM restaurant_tables));

-- 8. Default Warehouse Setup
INSERT INTO warehouses (id, created_at, updated_at, address, is_active, is_default, name, branch_id, tenant_id)
VALUES 
  (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Bogeda Principal', true, true, 'Central', 1, 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('warehouses_id_seq', (SELECT MAX(id) FROM warehouses));
