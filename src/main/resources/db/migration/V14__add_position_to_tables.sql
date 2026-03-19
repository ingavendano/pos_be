-- Migration to add position coordinates to tables for the interactive map
ALTER TABLE tables ADD COLUMN posx INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE tables ADD COLUMN posy INTEGER DEFAULT 0 NOT NULL;
