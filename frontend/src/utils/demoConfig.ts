import { getRuntimeConfig } from "../config/runtimeConfig";

function trimEnv(value: string | undefined): string | undefined {
  const trimmed = value?.trim();
  return trimmed ? trimmed : undefined;
}

export function getDemoVideoUrl(): string | undefined {
  return trimEnv(getRuntimeConfig().demoVideoUrl);
}

export function getProjectRepoUrl(): string | undefined {
  return trimEnv(getRuntimeConfig().projectRepoUrl);
}

/** Returns embed URL for YouTube watch/youtu.be/embed links, or null if not YouTube. */
export function toYouTubeEmbedUrl(url: string): string | null {
  try {
    const parsed = new URL(url);
    const host = parsed.hostname.replace(/^www\./, "");

    if (host === "youtu.be") {
      const id = parsed.pathname.slice(1).split("/")[0];
      return id ? `https://www.youtube.com/embed/${id}` : null;
    }

    if (host === "youtube.com" || host === "m.youtube.com") {
      if (parsed.pathname === "/watch") {
        const id = parsed.searchParams.get("v");
        return id ? `https://www.youtube.com/embed/${id}` : null;
      }
      const embedMatch = /^\/embed\/([^/]+)/.exec(parsed.pathname);
      if (embedMatch) {
        return `https://www.youtube.com/embed/${embedMatch[1]}`;
      }
    }
  } catch {
    return null;
  }

  return null;
}

export function isLocalVideoPath(url: string): boolean {
  return url.startsWith("/");
}

export function isDemoVideoConfigured(): boolean {
  const demoVideoUrl = getDemoVideoUrl();
  if (!demoVideoUrl) return false;
  return toYouTubeEmbedUrl(demoVideoUrl) !== null || isLocalVideoPath(demoVideoUrl);
}
