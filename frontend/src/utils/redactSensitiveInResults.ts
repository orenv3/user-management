const PRIVATE_EMAIL = import.meta.env.VITE_PRIVATE_ADMIN_EMAIL as string | undefined;

const PASSWORD_KEYS = new Set(["password", "oldPassword", "newPassword"]);

export function redactSensitiveInResults(value: unknown): unknown {
  if (value === null || value === undefined) return value;
  if (typeof value === "string") {
    return redactString(value);
  }
  if (Array.isArray(value)) {
    return value.map(redactSensitiveInResults);
  }
  if (typeof value === "object") {
    const out: Record<string, unknown> = {};
    for (const [key, val] of Object.entries(value as Record<string, unknown>)) {
      if (key === "token") continue;
      if (PASSWORD_KEYS.has(key)) continue;
      out[key] = redactSensitiveInResults(val);
    }
    return out;
  }
  return value;
}

function redactString(s: string): string {
  if (PRIVATE_EMAIL && s === PRIVATE_EMAIL) {
    return "[private admin]";
  }
  return s;
}

export function formatResultForDisplay(value: unknown): string {
  const redacted = redactSensitiveInResults(value);
  if (typeof redacted === "string") return redacted;
  return JSON.stringify(redacted, null, 2);
}
