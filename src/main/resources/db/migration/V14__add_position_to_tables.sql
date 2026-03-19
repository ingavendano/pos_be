-- Migration to add position coordinates to tables for the interactive map
ALTER TABLE restaurant_tables ADD COLUMN posx INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE restaurant_tables ADD COLUMN posy INTEGER DEFAULT 0 NOT NULL;
