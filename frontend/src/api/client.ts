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

async function parseJsonOrThrow(res: Response) {
  const text = await res.text();
  const json = text ? JSON.parse(text) : null;
  if (res.ok) return json;
  const err = json as ApiErrorResponse | null;
  const message = err?.message ?? `Request failed (${res.status})`;
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
