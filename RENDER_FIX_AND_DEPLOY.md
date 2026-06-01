# TrustCart Render Fix and Deploy Notes

If Render shows **No open ports detected** or **Exited with status 1**, the application exited before Render detected a listening web server.

## Required Render settings

- Runtime: Docker
- Branch: main
- Root Directory: leave blank if `pom.xml` and `Dockerfile` are in repository root
- Dockerfile path: `Dockerfile`
- Health Check Path: `/health`

## Required Render environment variables

```text
SPRING_PROFILES_ACTIVE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres.YOUR_PROJECT_REF
SPRING_DATASOURCE_PASSWORD=YOUR_SUPABASE_DATABASE_PASSWORD
PORT=8080
SERVER_ADDRESS=0.0.0.0
JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0
```

## Supabase reminder

Use the **Session Pooler** connection string on port `5432`.
Do not use `[YOUR-PASSWORD]`; replace it with your real Supabase database password.

## If deploy still fails

Open Render logs and search for the first line that begins with `Caused by:`. That line gives the real reason for the crash.
