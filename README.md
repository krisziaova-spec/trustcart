# TrustCart Clean Final Build

This package keeps only the required project files:
- `src/`
- `database/`
- `pom.xml`
- `Dockerfile`
- `render.yaml`
- `README.md`

## Required database step for existing Render/Supabase database
Run this SQL once before redeploying if your database already exists:

```sql
-- database/migration-fbt-hotfix.sql
```

This adds the required columns for seller approval and Fulfilled by TrustCart, including `can_use_fbt`.

For a fresh database, use:

```sql
-- database/supabase-setup.sql
```

## Covered updates
- Official TrustCart logo added.
- Favicon is cart/shield icon only, without the TrustCart word.
- Seller cannot instantly activate a store.
- Seller must submit a Create Store application.
- Seller status starts as `PENDING`.
- Requirements notice is generated for ID, proof of address, optional permit, and selfie with ID.
- Admin can approve or reject sellers at `/admin/sellers`.
- Pending sellers cannot log in.
- Approved sellers can log in and manage their store.
- Fulfilled by Seller is the default fulfillment type.
- Fulfilled by TrustCart can be requested and approved through `/admin/fulfillment`.
- Product cards and cart display FBS/FBT badges.
- Cart now shows product image, plus/minus quantity buttons, and delete icon.
- Checkout includes voucher/discount field.
- Buyer and seller login pages use matching TrustCart branding.
- Logout option added for seller and buyer sessions.
- Added multiple demo stores/products in Laguna and Cavite for location search demo.
- Mobile responsive improvements retained.

## Demo URLs
- Home/shop: `/`
- Buyer login: `/buyer/login`
- Seller login: `/seller/login`
- Seller application: `/seller/apply`
- Seller dashboard: `/seller/dashboard`
- Admin seller approval: `/admin/sellers`
- Admin fulfillment approval: `/admin/fulfillment`


## Admin URL

The admin dashboard is intentionally hidden from buyer and seller navigation. Open it directly at:

`/trustcart-admin-portal-2026/sellers`

Admin sections:
- `/trustcart-admin-portal-2026/sellers` - approve or reject seller applications
- `/trustcart-admin-portal-2026/fulfillment` - approve Fulfilled by TrustCart product requests

Do not add this link to buyer or seller pages.

## Deployment Reminder

Replace the whole project with this package. Do not copy only selected files, because the build requires the included model, repository, service, template, and static files.
