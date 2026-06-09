# Frontend (Vite + React)

For a plain-language overview and demo walkthrough, see the root [readMe.md](../readMe.md#for-everyone).

## Environment variables

All `VITE_*` settings live in the **repo-root** [`.env`](../.env) — the same file as `JWT_SECRET` and `SEED_*`. Copy [`.env.example`](../.env.example) to `.env` at the project root.

Vite loads them via `envDir: ".."` in [`vite.config.ts`](vite.config.ts). After changing `VITE_*`, restart `npm run dev` or rebuild the Docker image.

## Development

```bash
npm install
npm run dev
```

Proxies `/api/*` to `http://localhost:8080`.

### Demo video on login page

Recommended: host on YouTube and set in repo-root `.env`:

```env
VITE_DEMO_VIDEO_URL=https://youtu.be/4f4ND3ctlD8
```

Alternatively, use a local file at `public/demo.mp4` with `VITE_DEMO_VIDEO_URL=/demo.mp4`.

Restart the dev server or run `docker compose up --build` after changes.

## Production

Maven packages the build into the Spring Boot jar under `static/`. The Dockerfile copies repo-root `.env` before `npm run build`.

## Routes

| Route | Role |
|-------|------|
| `/login` | Public |
| `/` | Overview |
| `/users`, `/tasks`, `/comments`, `/analytics` | ADMIN |
| `/my-tasks`, `/my-comments` | USER |
