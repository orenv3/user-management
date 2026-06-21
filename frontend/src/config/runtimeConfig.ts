export type RuntimeConfig = {
  demoVideoUrl?: string;
  projectRepoUrl?: string;
  privateAdminEmail?: string;
  seedUserName?: string;
  seedUserEmail?: string;
  seedUserPassword?: string;
  seedAdminName?: string;
  seedAdminEmail?: string;
  seedAdminPassword?: string;
};

const EMPTY_CONFIG: RuntimeConfig = {};

let runtimeConfig: RuntimeConfig = EMPTY_CONFIG;

function trimEnv(value: string | undefined): string | undefined {
  const trimmed = value?.trim();
  return trimmed ? trimmed : undefined;
}

function viteFallback(): RuntimeConfig {
  return {
    demoVideoUrl: trimEnv(import.meta.env.VITE_DEMO_VIDEO_URL),
    projectRepoUrl: trimEnv(import.meta.env.VITE_PROJECT_REPO_URL),
    privateAdminEmail: trimEnv(import.meta.env.VITE_PRIVATE_ADMIN_EMAIL),
    seedUserName: trimEnv(import.meta.env.VITE_SEED_USER_NAME),
    seedUserEmail: trimEnv(import.meta.env.VITE_SEED_USER_EMAIL),
    seedUserPassword: trimEnv(import.meta.env.VITE_SEED_USER_PASSWORD),
    seedAdminName: trimEnv(import.meta.env.VITE_SEED_ADMIN_NAME),
    seedAdminEmail: trimEnv(import.meta.env.VITE_SEED_ADMIN_EMAIL),
    seedAdminPassword: trimEnv(import.meta.env.VITE_SEED_ADMIN_PASSWORD),
  };
}

function mergeWithViteFallback(fromApi: RuntimeConfig): RuntimeConfig {
  const fallback = viteFallback();
  return {
    demoVideoUrl: fromApi.demoVideoUrl ?? fallback.demoVideoUrl,
    projectRepoUrl: fromApi.projectRepoUrl ?? fallback.projectRepoUrl,
    privateAdminEmail: fromApi.privateAdminEmail ?? fallback.privateAdminEmail,
    seedUserName: fromApi.seedUserName ?? fallback.seedUserName,
    seedUserEmail: fromApi.seedUserEmail ?? fallback.seedUserEmail,
    seedUserPassword: fromApi.seedUserPassword ?? fallback.seedUserPassword,
    seedAdminName: fromApi.seedAdminName ?? fallback.seedAdminName,
    seedAdminEmail: fromApi.seedAdminEmail ?? fallback.seedAdminEmail,
    seedAdminPassword: fromApi.seedAdminPassword ?? fallback.seedAdminPassword,
  };
}

export function getRuntimeConfig(): RuntimeConfig {
  return runtimeConfig;
}

export function getPrivateAdminEmail(): string | undefined {
  return runtimeConfig.privateAdminEmail;
}

export async function loadRuntimeConfig(): Promise<void> {
  try {
    const res = await fetch("/api/config");
    if (!res.ok) {
      throw new Error(`Failed to load runtime config (${res.status})`);
    }
    const fromApi = (await res.json()) as RuntimeConfig;
    runtimeConfig = mergeWithViteFallback(fromApi);
  } catch {
    runtimeConfig = viteFallback();
  }
}
