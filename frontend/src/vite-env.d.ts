/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_PRIVATE_ADMIN_EMAIL?: string;
  readonly VITE_DEMO_EMAIL?: string;
  readonly VITE_DEMO_PASSWORD?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
