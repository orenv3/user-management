export type AuthResponse = {
  token: string | null;
  email: string;
  role: "ADMIN" | "USER" | string;
  userId: number;
};

export type ApiErrorResponse = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: Record<string, string> | null;
};

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

export async function apiGet<T>(path: string): Promise<T> {
  const token = getToken();
  const res = await fetch(path, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  return (await parseJsonOrThrow(res)) as T;
}

export async function apiPost<T>(path: string, body: unknown): Promise<T> {
  const token = getToken();
  const res = await fetch(path, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify(body),
  });
  return (await parseJsonOrThrow(res)) as T;
}

