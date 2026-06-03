const PRIVATE_EMAIL = import.meta.env.VITE_PRIVATE_ADMIN_EMAIL as string | undefined;

export const PRIVATE_ADMIN_LABEL = "[private admin]";

export function isPrivateAdminEmail(email: string | null | undefined): boolean {
  return !!PRIVATE_EMAIL && !!email && email === PRIVATE_EMAIL;
}

/** Safe email for any UI text (header, tables, overview). */
export function displayEmail(email: string | null | undefined): string {
  if (!email) return "—";
  if (isPrivateAdminEmail(email)) return PRIVATE_ADMIN_LABEL;
  return email;
}

/** Remove private admin from user lists so the row never appears in the web UI. */
export function filterPrivateAdminUsers<T extends { email: string }>(users: T[]): T[] {
  if (!PRIVATE_EMAIL) return users;
  return users.filter((u) => u.email !== PRIVATE_EMAIL);
}

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
  if (isPrivateAdminEmail(s)) return PRIVATE_ADMIN_LABEL;
  return s;
}

export function formatResultForDisplay(value: unknown): string {
  const redacted = redactSensitiveInResults(value);
  if (typeof redacted === "string") return redacted;
  return JSON.stringify(redacted, null, 2);
}
