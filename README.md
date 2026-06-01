# TrustCart Complete AR/VR + Live Selling System

TrustCart is a buyer-and-seller marketplace prototype focused on verified sellers, protected checkout, sustainability, location-based discovery, live selling, TrustPoints rewards, autoshipment, and a working browser-based Virtual Try-On Preview.

## Main Pages

- `/` Buyer marketplace dashboard
- `/buyer/login` Buyer login
- `/buyer/register` Buyer registration
- `/cart` Cart
- `/checkout` Checkout
- `/try-on` Virtual Try-On Preview for Men and Women Fashion
- `/autoship` Autoship subscriptions
- `/track` Track order
- `/refund` Refund request
- `/seller` Seller Centre
- `/seller/login` Seller login
- `/seller/apply` Seller registration
- `/seller/dashboard` Seller dashboard
- `/seller/products/new` Add product
- `/seller/discounts/new` Create seller discount code
- `/faq`, `/site-map`, and policy pages
- `/health` Render health check

## Demo Accounts

Buyer:
- `buyer@trustcart.ph`
- `trust123`

Sellers:
- `greentech@trustcart.ph` / `trust123`
- `localgoods@trustcart.ph` / `trust123`
- `ecohome@trustcart.ph` / `trust123`

## Local Run

```bash
mvn spring-boot:run
```

Open `http://localhost:8080`.

## Render + Supabase Environment Variables

Use Supabase Session Pooler details.

```text
SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_SUPABASE_POOLER_HOST:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.YOUR_PROJECT_REF
SPRING_DATASOURCE_PASSWORD=YOUR_SUPABASE_PASSWORD
PORT=8080
SERVER_ADDRESS=0.0.0.0
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0
```

## Database

Run only this one file in Supabase SQL Editor if you want to reset/reload the database:

```text
database/supabase-setup.sql
```

## About Virtual Try-On

The Virtual Try-On Preview is a working browser-based prototype. The user can upload/take a photo, choose Men or Women items, overlay clothing, drag, resize, rotate, reset, and open the product. The uploaded photo is handled client-side and is not saved to the database.

This is not full AI body segmentation. It is a functional AR-style preview suitable for a school prototype.
