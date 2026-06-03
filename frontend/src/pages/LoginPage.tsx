import { FormEvent, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { apiPost, setToken } from "../api/client";
import type { AuthResponse } from "../api/types";
import { useAuth } from "../auth/AuthContext";
import { Field, btnPrimary, inputClass } from "../components/Field";

const demoEmail = import.meta.env.VITE_DEMO_EMAIL as string | undefined;
const demoPassword = import.meta.env.VITE_DEMO_PASSWORD as string | undefined;

export default function LoginPage() {
  const nav = useNavigate();
  const { me, loading, refresh } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  if (!loading && me) {
    return <Navigate to="/" replace />;
  }

  function fillDemo() {
    if (demoEmail) setEmail(demoEmail);
    if (demoPassword) setPassword(demoPassword);
    setError(null);
  }

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const res = await apiPost<AuthResponse>("/api/auth/login", { email, password });
      setToken(res.token);
      await refresh();
      nav("/", { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login failed");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-6">
      <div className="w-full max-w-md rounded-2xl border border-slate-800 bg-slate-900/60 p-6 shadow-xl">
        <h1 className="text-2xl font-semibold">User Management</h1>
        <p className="mt-2 text-sm text-slate-300">
          Sign in with the credentials from your local <code className="text-slate-200">.env</code>{" "}
          seed or registered users. JWT is stored in session storage.
        </p>

        {demoEmail && (
          <button type="button" className="mt-4 text-sm text-indigo-300 underline" onClick={fillDemo}>
            Pre-fill from local env (VITE_DEMO_EMAIL)
          </button>
        )}

        <form className="mt-6 space-y-4" onSubmit={onSubmit}>
          <Field label="Email">
            <input
              className={inputClass}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="username"
            />
          </Field>
          <Field label="Password">
            <input
              className={inputClass}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              type="password"
              autoComplete="current-password"
            />
          </Field>

          {error && (
            <div className="rounded-lg border border-rose-900/60 bg-rose-950/40 px-3 py-2 text-sm text-rose-200">
              {error}
            </div>
          )}

          <button className={`w-full ${btnPrimary}`} disabled={submitting} type="submit">
            {submitting ? "Logging in..." : "Login"}
          </button>

          <div className="text-xs text-slate-400">
            Swagger:{" "}
            <a className="underline" href="/swagger-ui/index.html">
              /swagger-ui/index.html
            </a>
          </div>
        </form>
      </div>
    </div>
  );
}
