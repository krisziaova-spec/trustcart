# TrustCart Clean Updated Build

This package contains only the required application files.

## Important existing database fix
If you already deployed an older TrustCart database, run this SQL once before redeploying:

`database/migration-fbt-hotfix.sql`

This adds the missing FBT/seller approval columns such as `can_use_fbt`.

## Included updates
- Official TrustCart logo
- Clean icon-only favicon
- Seller approval workflow
- Fulfilled by Seller and Fulfilled by TrustCart
- Admin seller approval and FBT approval pages
- Demo sellers and products for Laguna/Cavite search samples
- Cart image, quantity +/-, remove button, and voucher checkout
- Mobile responsive UI updates
