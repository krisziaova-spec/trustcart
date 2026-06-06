-- TrustCart urgent Render/Supabase hotfix
-- Run this only if the live app is already down and you need DB cleanup before redeploy.

-- Fix old prepared-food enum value that caused: No enum constant ProductCategory.PREPARED_FOODS
UPDATE product
SET category = 'PREPARED_FOOD'
WHERE category = 'PREPARED_FOODS';

-- Remove orphan chat data that prevents Hibernate from adding chat_thread buyer FK cleanly.
DELETE FROM chat_message
WHERE thread_id IN (
    SELECT id FROM chat_thread
    WHERE buyer_id IS NOT NULL
      AND buyer_id NOT IN (SELECT id FROM buyer_account)
);

DELETE FROM chat_thread
WHERE buyer_id IS NOT NULL
  AND buyer_id NOT IN (SELECT id FROM buyer_account);
