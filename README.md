# TrustCart Buyer-Seller Live Marketplace

TrustCart is a Spring Boot + PostgreSQL marketplace prototype focused on verified sellers, buyer protection, sustainable shopping, location-based seller discovery, discount codes, TrustPoints, and autoshipment.

This version is designed for a working presentation flow with **buyer access and seller access only**.

## Main Pages

- `/` - Buyer marketplace homepage
- `/buyer/login` - Buyer login
- `/buyer/register` - Buyer registration
- `/cart` - Cart
- `/checkout` - Checkout with payment options, discount code, TrustPoints, and green checkout
- `/autoship` - Buyer autoship subscriptions
- `/track` - Order tracking
- `/refund` - Protection center / refund request
- `/seller` - Seller Centre
- `/seller/login` - Seller login
- `/seller/apply` - Create seller account
- `/seller/dashboard` - Seller dashboard
- `/seller/products/new` - Seller product publishing form
- `/faq` - FAQ
- `/site-map` - Human-readable site map
- `/sitemap.xml` - XML sitemap
- `/health` - Render health check

## Sample Accounts

Buyer:

```text
buyer@trustcart.ph
trust123
```

Sellers:

```text
greentech@trustcart.ph / trust123
localgoods@trustcart.ph / trust123
ecohome@trustcart.ph / trust123
```

## Included Features

### Buyer

- Shopee-like public homepage before login
- Search bar
- Clickable category section
- Clickable promo/feature tiles
- Target market location and radius filter
- Nearby seller and pickup-capable filters
- Product details pages
- Add to cart
- Checkout
- Payment options
- Discount code input
- TrustPoints redemption
- Green checkout options
- Order tracking
- Refund request
- Autoship subscriptions

### Seller

- Separate Seller Centre
- Seller registration/login
- Exact store location captured for platform verification
- Buyer-facing seller address is protected and hidden
- Seller dashboard
- Product publishing with product photo URL
- Seller-created discount codes
- Product list monitoring
- Recent order monitoring

### Trust and Sustainability

- TrustCart Shield
- Product Trust Score
- Green Score
- Seller Passport
- Plastic-free packaging badge
- Locally sourced badge
- Low-waste delivery indicator
- Off-platform transaction warning
- Buyer protection policies
- Sustainability policy
- Prohibited items policy

## Product Catalog

The database seed contains at least **3 products per category** with image URLs:

- Electronics
- Mobile Accessories
- Fashion
- Beauty & Personal Care
- Home & Living
- Groceries
- Health & Wellness
- Baby & Kids
- Sports & Outdoors
- School & Office Supplies
- Automotive & Motorcycle
- Pet Supplies
- Sustainable Products
- Local Filipino Products

## Supabase Setup

Use this one file for a fresh Supabase database reset and setup:

```text
database/supabase-setup.sql
```

It creates tables and inserts sample buyer, sellers, discount codes, and 42 products.

If tables already exist and you only want to reload sample data, run:

```text
database/seed-data.sql
```

## Render Environment Variables

```text
SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.YOUR_PROJECT_REF
SPRING_DATASOURCE_PASSWORD=YOUR_SUPABASE_DATABASE_PASSWORD
PORT=8080
SERVER_ADDRESS=0.0.0.0
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0
```

Health check path:

```text
/health
```

## Local Run

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```
