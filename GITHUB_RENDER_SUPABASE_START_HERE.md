# TrustCart Deployment Guide: GitHub + Render + Supabase

This folder is ready for:

- GitHub repository upload
- Render web service deployment
- Supabase PostgreSQL database connection

## 1. Confirm GitHub root folder

When you open the GitHub repository, these files must be visible immediately in the root:

```text
pom.xml
Dockerfile
render.yaml
src/
database/
README.md
```

Do not upload a parent folder that hides these files one level deeper.

## 2. Create Supabase database

1. Open Supabase.
2. Create a new project.
3. Go to **Connect**.
4. Copy the **Postgres connection details**.
5. For Render, use the Supabase **pooler/session** connection details if available.

Spring Boot needs this format:

```text
jdbc:postgresql://HOST:PORT/DATABASE?sslmode=require
```

Example format only:

```text
jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres?sslmode=require
```

Your username may look like:

```text
postgres.your_project_ref
```

## 3. Push to GitHub

```bash
git init
git add .
git commit -m "TrustCart Render Supabase ready"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/trustcart.git
git push -u origin main
```

## 4. Deploy on Render

Recommended method:

1. Go to Render Dashboard.
2. New > Web Service.
3. Connect your GitHub repository.
4. Choose Docker runtime / Dockerfile deployment.
5. Set Health Check Path to:

```text
/health
```

6. Add environment variables:

```text
SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_SUPABASE_HOST:6543/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.YOUR_PROJECT_REF
SPRING_DATASOURCE_PASSWORD=YOUR_SUPABASE_PASSWORD
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0 -Dserver.address=0.0.0.0
```

7. Deploy.

## 5. Optional: Render Blueprint

This folder includes `render.yaml`. You can use Render Blueprint deployment if your instructor or team prefers infrastructure-as-code.

The `render.yaml` intentionally does not include real Supabase credentials. Render will ask for them because secret values are marked with `sync: false`.

## 6. Test live site

After Render gives you the live URL, open:

```text
/
/buyer/login
/seller
/seller/login
/admin
/cart
/checkout
/health
```

## 7. Sample accounts

Buyer:

```text
buyer@trustcart.ph
Password: trust123
```

Sellers:

```text
greentech@trustcart.ph / trust123
localgoods@trustcart.ph / trust123
ecohome@trustcart.ph / trust123
```

## 8. Important prototype notes

- Payment is simulated only.
- Seller location is verified by admin in the prototype.
- Exact seller address is not exposed publicly to protect marketplace transactions.
- Buyers are warned that off-platform transactions are not covered by TrustCart buyer protection.
- Tables can be auto-created by Spring Boot JPA.
- Manual SQL is available at `database/supabase-setup.sql` only if required.
