# TrustCart Final Complete Operational Build

TrustCart is a trust-centered local marketplace with verified sellers, protected buyers, Fulfilled by Seller, Fulfilled by TrustCart, seller/buyer safety controls, chat, ticketing, and warehouse incoming-stock tracking.

## Main URLs

Public store:
- `/`

Buyer:
- `/buyer/login`
- `/buyer/register`
- `/buyer/messages`
- `/support/contact`
- `/cart`
- `/checkout`

Seller:
- `/seller/login`
- `/seller/apply`
- `/seller/dashboard`
- `/seller/fulfillment`
- `/seller/analytics`
- `/seller/messages`
- `/seller/support/contact`
- `/seller/report-buyer`

Admin / Command Center:
- `/command-center/login`
- `/command-center`
- `/command-center/sellers`
- `/command-center/fulfillment`
- `/command-center/incoming-stocks`
- `/command-center/tickets`
- `/command-center/buyers`
- `/command-center/analytics`

## Admin Demo Login

- Email: `admin@trustcart.ph`
- Password: `trustadmin2026`

## Added Operational Features

- Buyer can chat/contact seller from product or store pages.
- Seller has protected buyer chat inbox.
- Buyer can open general support tickets.
- Seller can open seller support tickets and report buyers.
- Admin ticketing has separate Buyer Concerns and Seller Concerns tabs.
- Seller can create incoming stock shipments to a preferred TrustCart warehouse.
- Admin can monitor incoming stocks and update received warehouse quantity.
- TrustCart warehouse network seeded across major Philippine cities.
- Admin can approve FBT products after warehouse stock verification.

## Database

For an existing database, run once after successful deploy:

```sql
database/migration-admin-security-hotfix.sql
```

Do not run `database/supabase-setup.sql` on an existing database unless you want a full reset.
