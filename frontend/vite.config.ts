import path from "node:path";
import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

const repoRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");

export default defineConfig({
  envDir: repoRoot,
  plugins: [react()],
  // Build a SPA that is served by Spring Boot from "/" in production.
  base: "/",
  build: {
    outDir: "dist",
    emptyOutDir: true,
  },
  server: {
    // In dev mode, proxy API calls to Spring Boot.
    proxy: {
      "/api": "http://localhost:8080",
      "/swagger-ui": "http://localhost:8080",
      "/v3": "http://localhost:8080",
    },
  },
});

