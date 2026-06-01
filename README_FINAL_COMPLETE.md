# TrustCart Final Complete System

This is the final clean TrustCart Spring Boot + Supabase/PostgreSQL + Render-ready package.

## Included core features

- Buyer marketplace homepage with the preferred dashboard layout
- Buyer registration and login
- Seller Centre, seller application, seller login, seller dashboard
- Seller product upload
- Seller-created discount codes
- WELCOME10 first-time buyer promo
- Product catalog with photos and at least 3 items per category
- Category tiles with icons
- Target Market Location with map-style preview
- Nearby seller and pickup-capable seller filters
- Protected seller location concept
- Cart and checkout
- Payment option selection
- TrustPoints loyalty rewards
- Autoship/subscription orders
- Order tracking
- Refund request
- TrustCart Live with sample online-selling videos
- Virtual Try-On Preview for Men and Women fashion items
- 10 Men Try-On items and 10 Women Try-On items
- Gift Registry for baby shower, wedding/newlywed, birthday, housewarming, graduation, and wishlist use cases
- Shareable gift registry links
- Protected recipient address and protected checkout reminder
- FAQ, Help Center, Site Map, and policy pages
- Supabase full SQL setup with complete visible seed data
- Render deployment files

## Main pages

- `/`
- `/buyer/login`
- `/buyer/register`
- `/cart`
- `/checkout`
- `/try-on`
- `/registry`
- `/registry/create`
- `/autoship`
- `/track`
- `/refund`
- `/seller`
- `/seller/login`
- `/seller/apply`
- `/seller/dashboard`
- `/faq`
- `/site-map`

## Supabase setup

Run only this file in Supabase SQL Editor if you want to reset/reload data:

`database/supabase-setup.sql`

It includes:

- 3 sellers
- 1 buyer account
- 3 discount codes
- 42 regular products
- 20 Virtual Try-On products
- 2 sample gift registries
- 8 sample gift registry items

## Demo accounts

Buyer:

- `buyer@trustcart.ph`
- `trust123`

Sellers:

- `greentech@trustcart.ph` / `trust123`
- `localgoods@trustcart.ph` / `trust123`
- `ecohome@trustcart.ph` / `trust123`

## Render environment variables

Use your own Supabase values:

- `SPRING_PROFILES_ACTIVE=postgres`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_SUPABASE_POOLER_HOST:5432/postgres?sslmode=require`
- `SPRING_DATASOURCE_USERNAME=postgres.YOUR_PROJECT_REF`
- `SPRING_DATASOURCE_PASSWORD=YOUR_SUPABASE_PASSWORD`
- `PORT=8080`
- `SERVER_ADDRESS=0.0.0.0`
- `JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0`

## Notes

- Payment is simulated for prototype presentation.
- Location and map are prototype UI features, not live Google Maps/Mapbox API.
- Virtual Try-On is a working browser-based overlay preview, not full AI body fitting.
- Live selling videos are sample embeds only.
- Gift Registry keeps recipient address protected and routes gift buying through TrustCart.
