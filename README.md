# TrustCart Final Operational Build

This is the complete TrustCart project with the final buyer/seller/admin operational updates.

## Main URLs

Store / Buyer / Seller public marketplace:
- `/`

Buyer:
- `/buyer/login`
- `/buyer/orders`
- `/buyer/messages`
- `/support/contact`
- `/buyer/logout`

Seller:
- `/seller/login`
- `/seller/dashboard`
- `/seller/fulfillment`
- `/seller/analytics`
- `/seller/messages`
- `/seller/support/contact`
- `/seller/report-buyer`

Admin Command Center:
- `/command-center/login`
- `/command-center`
- `/command-center/sellers`
- `/command-center/fulfillment`
- `/command-center/incoming-stocks`
- `/command-center/tickets`
- `/command-center/buyers`
- `/command-center/analytics`

## Demo Credentials

Admin:
- Email: `admin@trustcart.ph`
- Password: `trustadmin2026`

Buyer:
- Email: `buyer@trustcart.ph`
- Password: `trust123`

Seller sample accounts:
- Email: `lagunafarmers@trustcart.ph`
- Password: `trust123`

Other seller emails also use `trust123`, for example:
- `caviterice@trustcart.ph`
- `chrysanthemumrice@trustcart.ph`
- `malagasangmart@trustcart.ph`

## Final Improvements Included

- Premium buyer, seller, and admin login pages
- Buyer logout access
- Buyer order dashboard
- Stronger order tracking page
- Payment simulation / escrow demo
- Buyer-to-seller chat
- Chat file attachments for proof
- Buyer support tickets with proof attachments
- Seller support tickets with proof attachments
- Seller can report buyers with proof attachments
- Admin tickets separated into Buyer Concerns and Seller Concerns
- Product reviews with rating, text review, verified purchase check, and proof attachment
- Seller fulfillment dashboard
- Seller sales analytics
- Admin sales analytics with filters
- Downloadable CSV reports for seller analytics, seller fulfillment, admin analytics, tickets, and incoming stocks
- Incoming stock tracker for TrustCart warehouses
- Fulfilled by Seller and Fulfilled by TrustCart model
- Admin store activate/deactivate/suspend controls
- Admin buyer activate/deactivate/block controls
- Cleaner product card spacing and mobile dashboard polish

## Deployment Order

For an existing database:
1. Backup the database.
2. Run `database/migration-admin-security-hotfix.sql` once.
3. Deploy this project.
4. Clear Render build cache if needed.

For a full reset/fresh database only:
- Run `database/supabase-setup.sql`, then deploy.

Do not expose the Command Center link on the public buyer/seller navigation.
