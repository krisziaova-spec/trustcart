# TrustCart realistic try-on update

This package changes the Virtual Try-On demo to use only two cleaner sample garments:

- Men Basic T-Shirt
- Women Simple Dress

Both use realistic flat-lay PNG assets instead of the earlier cartoon-style SVG clothing set.

Updated areas:
- `src/main/resources/templates/try-on.html`
- `src/main/resources/static/js/tryon.js`
- `src/main/resources/static/css/styles.css`
- `src/main/java/com/trustcart/config/DataSeeder.java`
- `database/supabase-setup.sql`
- new garment image assets in `src/main/resources/static/img/tryon/`
