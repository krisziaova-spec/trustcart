# TrustCart Location, Autoship Goods, and Storefront Update

## Added

### Buyer location search
- Reworded the confusing “Location preset” label to “Choose search area”.
- Added automatic browser geolocation prompt, subject to browser permission.
- Added “Use my current location” button.
- Kept manual city preset selection.
- Map preview updates from selected/current latitude and longitude.
- Nearby seller filtering now uses the selected latitude, longitude, and radius when “Show sellers inside this radius only” is checked.

### Added subscription-ready goods
Added more product categories and seeded subscription-ready sample products with image URLs:
- Fast-Moving Consumer Goods (FMCG)
- Convenience Goods
- Consumer Staples
- Everyday Essentials
- Daily Necessities
- Packaged Goods

New sample goods include detergent, dishwashing liquid, hand soap, tissue, paper towels, trash bags, rice, oats, coffee, canned tuna, powdered milk, baby wipes, shampoo/conditioner, toothpaste, cleaner, and cooking oil.

### Seller storefront
Added a public storefront page:
- `/store/{sellerId}`
- Shows seller profile photo, banner, description, seller area, trust details, promos, autoship products, and all store products.
- Product cards and detail pages now link to the seller storefront.

### Seller-side enhancements
The seller dashboard now includes:
- Add product listing
- Create discount/promo code
- Edit store profile
- Public storefront link
- Store profile photo URL and banner URL
- Sales report cards: gross sales, customer orders, units sold, active listings
- Customer orders table for orders containing the seller’s products

## Important prototype note
Seller product photos, store profile photo, and banner can now be uploaded through the seller forms. The prototype saves uploaded files to a local `/uploads` folder and serves them through `/uploads/**`. For production, connect file upload to durable cloud storage such as S3, Cloudinary, Supabase Storage, or Firebase Storage because local Render storage can be temporary across redeploys.

## Validation
JavaScript syntax was checked with `node --check` for:
- `location-assist.js`
- `search-assist.js`

Maven build could not be run in this environment because `mvn` is not installed.
