# Frontend (Vite + React)

## Development

```bash
npm install
npm run dev
```

Proxies `/api/*` to `http://localhost:8080`.

### Local env (optional, gitignored)

Copy `.env.example` to `.env.local`:

- `VITE_PRIVATE_ADMIN_EMAIL` — must match `SEED_ADMIN1_EMAIL` in the root `.env`; this address is shown as `[private admin]` in the header and overview, **omitted from user tables**, and redacted in action result panels.
- `VITE_DEMO_EMAIL` / `VITE_DEMO_PASSWORD` — optional login form pre-fill for local dev.

## Production

Maven packages the build into the Spring Boot jar under `static/`.

## Admin console routes

| Route | Role |
|-------|------|
| `/login` | Public |
| `/` | Overview |
| `/users`, `/tasks`, `/comments` | ADMIN |
| `/my-tasks`, `/my-comments` | USER |
