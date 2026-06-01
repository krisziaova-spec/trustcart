# TrustCart Clean Clickable Buyer + Seller Version

This package restores the clean TrustCart dashboard layout and only applies the requested fixes:

- Buyer and Seller only; Admin is removed from the visible platform flow.
- Seller can create discount codes inside Seller Dashboard.
- Seller can publish products from Seller Center.
- All visible major controls are clickable: navigation, category tiles, filter chips, feature cards, product photos, product names, product details, cart, checkout, FAQ, policies, and site map.
- Product catalog includes at least 3 products per category with product photo URLs.
- Supabase setup includes schema and seed data in one file: `database/supabase-setup.sql`.

For Render, keep the same environment variables:

SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.YOUR_PROJECT_REF
SPRING_DATASOURCE_PASSWORD=YOUR_SUPABASE_PASSWORD
PORT=8080
SERVER_ADDRESS=0.0.0.0
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0
