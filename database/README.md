# TrustCart Database Files

The application can automatically create/update tables through Spring Boot JPA using `spring.jpa.hibernate.ddl-auto=update`.

Manual database files are provided for instructor review or optional manual setup:

- `schema.sql` - full PostgreSQL schema with buyer accounts, sellers, products, orders, refunds, discount codes, rewards, and autoshipment subscriptions.
- `seed-data.sql` - sample users, sellers, products, product photo URLs, and active discount codes.
- `supabase-setup.sql` - same schema prepared for Supabase PostgreSQL.

Recommended for deployment: let Spring Boot create the tables first, then use the app's DataSeeder and Admin dashboard to manage sample data and discount codes.
