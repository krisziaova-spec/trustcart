# TrustCart Final Operational Build

This is the complete TrustCart project with buyer, seller, admin, review integrity, order review gating, and credibility/risk center updates.

## Main URLs

Store / Buyer / Seller public marketplace:
- `/`

Buyer:
- `/buyer/login`
- `/buyer/orders`
- `/buyer/messages`
- `/buyer/verification`
- `/support/contact`
- `/buyer/faq`
- `/buyer/logout`

Seller:
- `/seller/login`
- `/seller/dashboard`
- `/seller/fulfillment`
- `/seller/analytics`
- `/seller/messages`
- `/seller/reviews`
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
- `/command-center/reviews`
- `/command-center/risk`
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
- Buyer order dashboard with Shopee-inspired order tabs
- Review form locked until buyer confirms received/completed order
- Product reviews with rating, verified purchase, written review, and proof attachment
- Review Integrity System with seller appeals and admin moderation
- Credibility & Risk Center for buyer and seller/store account standing
- Buyer ID and face verification submission page
- Admin risk sorting, risk levels, account standing, verification status, and CSV export
- Stronger order tracking page
- Payment simulation / escrow demo
- Buyer-to-seller chat with proof attachments
- Buyer and seller support tickets with proof attachments
- Admin tickets separated into Buyer Concerns and Seller Concerns
- Seller can report buyers with proof attachments
- Seller fulfillment dashboard and FBT monitoring
- Seller sales analytics and downloadable reports
- Admin analytics with filters and downloadable CSV reports
- Incoming stock tracker for TrustCart warehouses
- Fulfilled by Seller and Fulfilled by TrustCart model
- Admin store activate/deactivate/suspend controls
- Admin buyer activate/deactivate/block controls
- FAQ / Buyer Help Center with buyer protection, points, orders, fulfillment, chat, tickets, reviews, and safety questions
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
