# TrustCart UI Image Search + Live Seller Hotfix

This hotfix includes:

1. Removed the unnecessary helper text under the search controls:
   - Voice: click the mic...
   - Image: upload a photo...
   - Image Search Prompt...

2. Updated image search behavior:
   - The image upload area is hidden by default.
   - The upload control only appears after clicking Image Search.
   - The native file input is hidden and replaced with a cleaner Choose Photo button.

3. Restored online seller sample live section:
   - Restored three TrustCart Live sample cards.
   - Added embedded sample videos for Eco Home Essentials Live, Local Finds Showcase, and Beauty and Care Live.

4. Kept previous deploy hotfixes:
   - ProductCategory enum includes FMCG, Convenience Goods, Consumer Staples, Everyday Essentials, Daily Necessities, and Packaged Goods.
   - BuyerController includes the missing market filter helper.

Deploy reminder:
- Replace/push the full updated source to GitHub.
- In Render, use Manual Deploy > Clear build cache & deploy.
- SQL rerun is not required for this UI-only hotfix unless you also want to reset demo data.
