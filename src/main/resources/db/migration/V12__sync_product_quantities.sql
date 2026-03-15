-- Synchronize product quantity cache with the sum of all inventory records
UPDATE products p 
SET quantity = (
    SELECT COALESCE(SUM(i.quantity), 0) 
    FROM inventory i 
    WHERE i.product_id = p.id
);
