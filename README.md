# TrustCart Ultra - Render + Supabase Ready Version

This version is prepared for GitHub, Render, and Supabase. Start with `GITHUB_RENDER_SUPABASE_START_HERE.md`.

Included deployment files:

- `Dockerfile` - Render-compatible Docker build
- `render.yaml` - optional Render Blueprint
- `.env.example` - environment variable template
- `/health` endpoint - Render health check route
- `database/supabase-setup.sql` - optional Supabase SQL script

---

# TrustCart 100/100 - Excellent Spring Boot E-Commerce Prototype

TrustCart is a green, trust-focused e-commerce prototype with buyer shopping pages, seller access, admin approval, checkout, simulated payment options, order tracking, refund requests, seller passport verification, product trust scoring, review integrity, and sustainability scoring.

This upgraded version is designed to stand out from a normal Shopee clone by focusing on **trust, transparency, buyer protection, and sustainability**.

## Best Routes

- Buyer marketplace: http://localhost:8080/
- Product details: click any product from homepage
- Cart: http://localhost:8080/cart
- Checkout: http://localhost:8080/checkout
- Track order: http://localhost:8080/track
- Protection / Refund center: http://localhost:8080/refund
- Seller center: http://localhost:8080/seller
- Seller login: http://localhost:8080/seller/login
- Seller application: http://localhost:8080/seller/apply
- Admin dashboard: http://localhost:8080/admin

## Seller Accounts

Use these emails on the Seller Login page:

- greentech@trustcart.ph
- localgoods@trustcart.ph
- ecohome@trustcart.ph

No password is required because this is a school prototype. Seller access is simulated through session login.

## What Makes This Version Excellent

### Buyer-side features

- Professional marketplace homepage
- Category search and product browsing
- Product details page
- Add to cart
- Cart management
- Transparent checkout
- Payment options: COD, GCash presentation, Maya presentation, card presentation, and bank transfer presentation
- Order success page
- Order tracking page
- Refund / protection center

### Trust and anti-deception features

- TrustCart Shield Verified badge
- Product Trust Score breakdown
- Seller Passport display
- Verified seller approval gate
- Product approval gate before public listing
- Smart review summary
- Red flag summary
- Verified buyer review policy display
- Buyer protection and refund tracker

### Sustainability features

- Green Score for each product
- Eco-friendly product badge
- Plastic-free packaging badge
- Locally sourced badge
- Low-waste delivery badge
- Green checkout options:
  - Eco-Packaging
  - No Extra Plastic
  - Consolidated Eco Delivery
- Eco delivery discount shown in the true price breakdown

### Seller-side features

- Separate Seller Center like Shopee Seller Centre
- Seller application form
- Seller login
- Seller dashboard
- Seller Passport verification indicators
- Product submission form
- Product status monitoring

### Admin-side features

- Admin dashboard
- Approve/reject sellers
- Approve/reject products
- Seller Passport monitor
- Trust, sustainability, review integrity, and buyer protection dashboard
- Update order status
- Update refund status

## Run Locally - Easiest Mode

This project runs immediately using H2 in-memory database by default.

```bash
mvn spring-boot:run
```

Open:

```txt
http://localhost:8080
```

If you see a Whitelabel 404, make sure you opened exactly `http://localhost:8080/` and that the terminal says `Started TrustCartApplication`.

## Run with PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE trustcart_db;
```

Run with environment variables:

### Windows PowerShell

```powershell
$env:SPRING_PROFILES_ACTIVE="postgres"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/trustcart_db"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
mvn spring-boot:run
```

### macOS/Linux

```bash
export SPRING_PROFILES_ACTIVE=postgres
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/trustcart_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password
mvn spring-boot:run
```

## GitHub Upload

```bash
git init
git add .
git commit -m "TrustCart 100 prototype"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/trustcart.git
git push -u origin main
```

## Free Hosting Suggestion

Recommended if Render is not available:

1. Push this project to GitHub.
2. Create a free PostgreSQL database using Neon or Supabase.
3. Deploy the Spring Boot app using Koyeb with Dockerfile.
4. Add these environment variables:
   - SPRING_PROFILES_ACTIVE=postgres
   - SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_HOST/YOUR_DB?sslmode=require
   - SPRING_DATASOURCE_USERNAME=YOUR_DB_USER
   - SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD

## Prototype Notes

- Payment is simulated only. No real money is processed.
- Seller verification is simulated through admin approval.
- Product approval is simulated through admin approval.
- Product reviews and trust scores use sample/rule-based data.
- Delivery tracking uses mock order statuses.
- Sustainability scoring is prototype logic, not a certified environmental audit.

## Visible Database Files

A `database/` folder is included for instructors who require SQL scripts:

- `database/schema.sql` - PostgreSQL table creation script
- `database/seed-data.sql` - sample sellers and products
- `database/README.md` - H2 and PostgreSQL setup guide

The application also creates database tables automatically through Spring Boot JPA/Hibernate.

## Retention Features Added

TrustCart now includes additional customer-retention features:

- Product photos on listings and product detail pages
- Admin-created discount codes
- Checkout discount code application
- TrustPoints rewards on every purchase
- Points redemption at checkout
- Loyalty tier display
- Autoshipment subscriptions for repeat-use essentials

Active sample discount codes:

```text
WELCOME100
GREEN5
LOCAL50
```

Rewards logic:

```text
Earn: 1 TrustPoint for every ₱20 spent
Redeem: 10 TrustPoints = ₱1 checkout discount
```

Autoshipment is available for selected essentials such as groceries, home living, pet supplies, wellness, beauty care, and sustainable products.

## Added Information Pages

TrustCart now includes customer-facing information pages that make the website feel more complete and easier to present:

- `/faq` - Frequently Asked Questions for buyers, sellers, sustainability, rewards, subscriptions, pickup, and buyer protection.
- `/site-map` - Human-readable site map for buyer, seller, admin, checkout, tracking, refund, and autoship pages.
- `/sitemap.xml` - XML sitemap endpoint for deployment and search-engine style structure.


## Full Online Shop Pages Added

TrustCart now includes standard online store support and policy pages:

- `/about`
- `/help-center`
- `/contact-us`
- `/faq`
- `/site-map`
- `/sitemap.xml`
- `/privacy-policy`
- `/terms-and-conditions`
- `/return-refund-policy`
- `/shipping-delivery-policy`
- `/payment-policy`
- `/buyer-protection-policy`
- `/seller-policy`
- `/authenticity-policy`
- `/sustainability-policy`
- `/prohibited-items-policy`
- `/off-platform-policy`

## Render Fix

This package includes a safer Dockerfile and server binding settings for Render. Use `/health` as the health check path.
