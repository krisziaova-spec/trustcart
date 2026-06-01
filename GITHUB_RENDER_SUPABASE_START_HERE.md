# GitHub + Supabase + Render Start Here

## 1. Supabase

For fresh setup, run only:

```text
database/supabase-setup.sql
```

This creates the tables and loads sample products, sellers, buyer account, and discount codes.

## 2. Render

Create a Web Service from your GitHub repository.

Use:

```text
Runtime: Docker
Branch: main
Root Directory: blank, unless your files are inside a folder
Health Check Path: /health
```

Environment variables:

```text
SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.YOUR_PROJECT_REF
SPRING_DATASOURCE_PASSWORD=YOUR_SUPABASE_DATABASE_PASSWORD
PORT=8080
SERVER_ADDRESS=0.0.0.0
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0
```

## 3. Test Pages

```text
/
/buyer/login
/buyer/register
/cart
/checkout
/autoship
/track
/refund
/seller
/seller/login
/seller/apply
/seller/dashboard
/seller/products/new
/faq
/site-map
/privacy-policy
/terms-and-conditions
/return-refund-policy
/shipping-delivery-policy
/payment-policy
/buyer-protection-policy
/seller-policy
/authenticity-policy
/sustainability-policy
/prohibited-items-policy
/off-platform-policy
```

## 4. Demo Flow

1. Open buyer homepage.
2. Click categories and quick action tiles.
3. Apply target market location.
4. Open product details.
5. Login buyer.
6. Add product to cart.
7. Apply discount code.
8. Checkout with green option.
9. Track order.
10. Submit refund request.
11. Open Seller Centre.
12. Login seller.
13. Publish product.
14. Create seller discount code.
15. Return to shop and confirm product/page links work.
