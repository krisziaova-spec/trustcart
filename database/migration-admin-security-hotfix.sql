-- TrustCart Admin Security + Buyer Safety Patch
-- Safe to run on an existing PostgreSQL/Supabase database.

ALTER TABLE buyer_account ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'ACTIVE';
ALTER TABLE buyer_account ADD COLUMN IF NOT EXISTS report_count INTEGER DEFAULT 0;
ALTER TABLE buyer_account ADD COLUMN IF NOT EXISTS admin_safety_note VARCHAR(1500);
ALTER TABLE buyer_account ADD COLUMN IF NOT EXISTS deactivated_at TIMESTAMP;
ALTER TABLE buyer_account ADD COLUMN IF NOT EXISTS blocked_at TIMESTAMP;

UPDATE buyer_account SET status = 'ACTIVE' WHERE status IS NULL OR status = '';
UPDATE buyer_account SET report_count = 0 WHERE report_count IS NULL;

ALTER TABLE support_ticket ADD COLUMN IF NOT EXISTS buyer_id BIGINT;
ALTER TABLE support_ticket ADD COLUMN IF NOT EXISTS reported_buyer_email VARCHAR(255);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_support_ticket_buyer_account'
    ) THEN
        ALTER TABLE support_ticket
        ADD CONSTRAINT fk_support_ticket_buyer_account
        FOREIGN KEY (buyer_id) REFERENCES buyer_account(id);
    END IF;
END $$;
