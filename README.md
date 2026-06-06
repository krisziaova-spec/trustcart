# TrustCart Final Clean Operational Build

This build includes the latest clean dashboard update:

- Compact Credibility & Risk Center with summary cards, filters, quick search, View More, and Manage dropdowns.
- Clickable admin dashboard cards.
- Clickable seller dashboard cards.
- Buyer Profile / Points page.
- TrustPoints visible to buyers.
- TrustPoints rule: 1 point per ₱100 completed purchase; 1 point = ₱1 checkout discount.
- Points are credited after the buyer confirms received delivery.
- Reviews remain locked until the buyer confirms received/completed order.
- Buyer My Purchases flow retained.
- Review Integrity, Risk Center, FAQ, FBT, tickets, chat, attachments, warehouse stocks, analytics, and exports retained.

## URLs

Store: `/`

Buyer:
- `/buyer/login`
- `/buyer/profile`
- `/buyer/orders`
- `/buyer/messages`
- `/buyer/verification`
- `/buyer/faq`

Seller:
- `/seller/login`
- `/seller/dashboard`
- `/seller/fulfillment`
- `/seller/analytics`
- `/seller/messages`
- `/seller/reviews`

Admin:
- `/command-center/login`
- `/command-center`
- `/command-center/sellers`
- `/command-center/fulfillment`
- `/command-center/incoming-stocks`
- `/command-center/tickets`
- `/command-center/reviews`
- `/command-center/risk`
- `/command-center/analytics`

## Demo Credentials

Admin: `admin@trustcart.ph` / `trustadmin2026`

Buyer: `buyer@trustcart.ph` / `trust123`

Seller: `lagunafarmers@trustcart.ph` / `trust123`

## Database

For existing database, run once:

`database/migration-admin-security-hotfix.sql`

Do not run `database/supabase-setup.sql` unless you want to reset all data.
