const SESSION_KEY = "analytics_session_id";

function createSessionId(): string {
  if (typeof crypto !== "undefined" && typeof crypto.randomUUID === "function") {
    return crypto.randomUUID();
  }
  return `${Date.now()}-${Math.random().toString(36).slice(2)}`;
}

function getOrCreateSessionId(): string {
  let id = sessionStorage.getItem(SESSION_KEY);
  if (!id) {
    id = createSessionId();
    sessionStorage.setItem(SESSION_KEY, id);
  }
  return id;
}

/** Records a page view when the route changes. */
export function trackPageView(path: string) {
  const sessionId = getOrCreateSessionId();
  fetch("/api/analytics/event", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      eventType: "PAGE_VIEW",
      path,
      sessionId,
    }),
  }).catch(() => {});
}

export { getOrCreateSessionId };
