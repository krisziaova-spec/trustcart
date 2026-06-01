# TrustCart Color Try-On Update

This full package keeps the existing TrustCart system and updates only the Virtual Try-On demo assets and seed data.

Updated Try-On items:

Men Basic T-Shirt:
- Green
- Navy
- White
- Black

Women Simple Dress:
- Sage
- Rose
- Navy
- Cream

These use realistic PNG garment overlays instead of the earlier cartoon-style SVG overlays.

Updated files:
- `src/main/resources/templates/try-on.html`
- `src/main/resources/static/js/tryon.js`
- `src/main/resources/static/css/styles.css`
- `src/main/java/com/trustcart/config/DataSeeder.java`
- `database/supabase-setup.sql`
- `src/main/resources/static/img/tryon/*.png`

Deployment:
1. Replace the GitHub repository contents with this full package.
2. Redeploy on Render using Clear build cache & deploy.
3. Run `database/supabase-setup.sql` in Supabase only if you want to reset/reload the updated sample data.
