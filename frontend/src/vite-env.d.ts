/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_PRIVATE_ADMIN_EMAIL?: string;
  readonly VITE_DEMO_EMAIL?: string;
  readonly VITE_DEMO_PASSWORD?: string;
  readonly VITE_SEED_USER_NAME?: string;
  readonly VITE_SEED_USER_EMAIL?: string;
  readonly VITE_SEED_USER_PASSWORD?: string;
  readonly VITE_SEED_ADMIN_NAME?: string;
  readonly VITE_SEED_ADMIN_EMAIL?: string;
  readonly VITE_SEED_ADMIN_PASSWORD?: string;
  readonly VITE_DEMO_VIDEO_URL?: string;
  readonly VITE_PROJECT_REPO_URL?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
