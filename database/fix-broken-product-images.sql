-- TrustCart image hotfix
-- Run this once in your PostgreSQL/Supabase SQL editor after deploying the updated code.
-- Purpose: replace two broken external image URLs with local images inside the project.

UPDATE product
SET image_url = '/img/products/paper-towel.png'
WHERE LOWER(name) = LOWER('Paper Towel 6 Rolls');

UPDATE product
SET image_url = '/img/products/canned-tuna.png'
WHERE LOWER(name) = LOWER('Canned Tuna 6-Pack');

-- Optional check: this should show the two updated products.
SELECT id, name, image_url
FROM product
WHERE LOWER(name) IN (LOWER('Paper Towel 6 Rolls'), LOWER('Canned Tuna 6-Pack'));
