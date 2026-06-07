export type { AuthResponse } from "./types";
export type { ApiErrorResponse } from "./types";

import type { ApiErrorResponse } from "./types";

function getToken(): string | null {
  return sessionStorage.getItem("token");
}

export function setToken(token: string | null) {
  if (!token) sessionStorage.removeItem("token");
  else sessionStorage.setItem("token", token);
}

export function logout() {
  setToken(null);
}

/** Parses JSON bodies; plain-text success responses (e.g. "Deleted: true") are returned as-is. */
function parseResponseBody(text: string, ok: boolean): unknown {
  const trimmed = text.trim();
  if (!trimmed) return null;
  try {
    return JSON.parse(trimmed);
  } catch {
    if (ok) return trimmed;
    return { message: trimmed };
  }
}

async function parseJsonOrThrow(res: Response) {
  const text = await res.text();
  const body = parseResponseBody(text, res.ok);
  if (res.ok) return body;
  const err = body as ApiErrorResponse | null;
  const message =
    err?.message ??
    (typeof body === "string" ? body : null) ??
    `Request failed (${res.status})`;
  throw new Error(message);
}

function authHeaders(): Record<string, string> {
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export async function apiGet<T>(path: string): Promise<T> {
  const res = await fetch(path, { headers: authHeaders() });
  return (await parseJsonOrThrow(res)) as T;
}

export async function apiPost<T>(path: string, body?: unknown): Promise<T> {
  const res = await fetch(path, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...authHeaders(),
    },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
  return (await parseJsonOrThrow(res)) as T;
}

export async function apiPut<T>(path: string, body?: unknown): Promise<T> {
  const res = await fetch(path, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      ...authHeaders(),
    },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
  return (await parseJsonOrThrow(res)) as T;
}

export async function apiDelete<T>(path: string): Promise<T> {
  const res = await fetch(path, {
    method: "DELETE",
    headers: authHeaders(),
  });
  return (await parseJsonOrThrow(res)) as T;
}
