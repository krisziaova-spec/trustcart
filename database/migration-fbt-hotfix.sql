-- TrustCart existing database hotfix/migration
-- Run this once in Render PostgreSQL/Supabase SQL editor before redeploying if your database already exists.

ALTER TABLE IF EXISTS seller ADD COLUMN IF NOT EXISTS can_use_fbt BOOLEAN DEFAULT FALSE;
ALTER TABLE IF EXISTS seller ADD COLUMN IF NOT EXISTS fulfillment_preference VARCHAR(255) DEFAULT 'SELLER';
ALTER TABLE IF EXISTS seller ADD COLUMN IF NOT EXISTS requirements_status VARCHAR(255) DEFAULT 'COMPLETED';
ALTER TABLE IF EXISTS seller ADD COLUMN IF NOT EXISTS requirements_note VARCHAR(1200) DEFAULT 'Requirements completed and approved by TrustCart.';
ALTER TABLE IF EXISTS seller ADD COLUMN IF NOT EXISTS store_profile_image_url VARCHAR(1200);
ALTER TABLE IF EXISTS seller ADD COLUMN IF NOT EXISTS store_banner_image_url VARCHAR(1200);
ALTER TABLE IF EXISTS seller ADD COLUMN IF NOT EXISTS store_description VARCHAR(1200);

ALTER TABLE IF EXISTS product ADD COLUMN IF NOT EXISTS fulfilled_by VARCHAR(255) DEFAULT 'SELLER';
ALTER TABLE IF EXISTS product ADD COLUMN IF NOT EXISTS fulfillment_status VARCHAR(255) DEFAULT 'SELLER_MANAGED';
ALTER TABLE IF EXISTS product ADD COLUMN IF NOT EXISTS trust_cart_stock INTEGER DEFAULT 0;
ALTER TABLE IF EXISTS product ADD COLUMN IF NOT EXISTS fulfillment_note VARCHAR(1200) DEFAULT 'Seller stores, packs, and ships this product.';

UPDATE seller SET can_use_fbt = FALSE WHERE can_use_fbt IS NULL;
UPDATE seller SET fulfillment_preference = 'SELLER' WHERE fulfillment_preference IS NULL;
UPDATE seller SET requirements_status = 'COMPLETED' WHERE requirements_status IS NULL;
UPDATE product SET fulfilled_by = 'SELLER' WHERE fulfilled_by IS NULL;
UPDATE product SET fulfillment_status = 'SELLER_MANAGED' WHERE fulfillment_status IS NULL;
