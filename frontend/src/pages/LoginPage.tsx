import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiPost, setToken, type AuthResponse } from "../api/client";

const DEMO_USER = { email: "user", password: "pass" };
const DEMO_ADMIN = { email: "admin@gvino.com", password: "1234" };

export default function LoginPage() {
  const nav = useNavigate();
  const [email, setEmail] = useState(DEMO_USER.email);
  const [password, setPassword] = useState(DEMO_USER.password);
  const [activeDemo, setActiveDemo] = useState<"user" | "admin">("user");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  function fillDemo(demo: "user" | "admin") {
    const account = demo === "user" ? DEMO_USER : DEMO_ADMIN;
    setEmail(account.email);
    setPassword(account.password);
    setActiveDemo(demo);
    setError(null);
  }

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await apiPost<AuthResponse>("/api/auth/login", { email, password });
      setToken(res.token);
      nav("/", { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-6">
      <div className="w-full max-w-md rounded-2xl border border-slate-800 bg-slate-900/60 p-6 shadow-xl">
        <h1 className="text-2xl font-semibold">User Management</h1>
        <p className="mt-2 text-sm text-slate-300">
          Sign in with email and password. After login, the app stores a JWT in{" "}
          <code className="text-slate-200">sessionStorage</code> and sends it on every API
          request so the backend can enforce your role.
        </p>

        <div className="mt-4 rounded-lg border border-slate-800 bg-slate-950/40 px-3 py-3 text-xs text-slate-400 space-y-2">
          <p>
            <span className="text-slate-200 font-medium">Demo accounts</span> (created when{" "}
            <code className="text-slate-300">dev-seed</code> profile is active):
          </p>
          <ul className="list-disc list-inside space-y-1">
            <li>
              <span className="text-slate-300">Regular user</span> — email{" "}
              <code className="text-slate-200">user</code>, password{" "}
              <code className="text-slate-200">pass</code>. Sees only assigned tasks.
            </li>
            <li>
              <span className="text-slate-300">Admin</span> — email{" "}
              <code className="text-slate-200">admin@gvino.com</code>, password{" "}
              <code className="text-slate-200">1234</code>. Sees and manages all tasks.
            </li>
          </ul>
          <p>
            Use the buttons below to pre-fill the form, then click Login. You can still edit
            the fields manually.
          </p>
        </div>

        <div className="mt-4 flex flex-wrap gap-2">
          <button
            type="button"
            className={`rounded-lg border px-3 py-2 text-sm ${
              activeDemo === "user"
                ? "border-indigo-500 bg-indigo-600/20 text-indigo-200"
                : "border-slate-700 text-slate-300 hover:bg-slate-900"
            }`}
            onClick={() => fillDemo("user")}
          >
            Use regular user
          </button>
          <button
            type="button"
            className={`rounded-lg border px-3 py-2 text-sm ${
              activeDemo === "admin"
                ? "border-indigo-500 bg-indigo-600/20 text-indigo-200"
                : "border-slate-700 text-slate-300 hover:bg-slate-900"
            }`}
            onClick={() => fillDemo("admin")}
          >
            Use admin account
          </button>
        </div>

        <form className="mt-6 space-y-4" onSubmit={onSubmit}>
          <div>
            <label className="text-sm text-slate-200">Email</label>
            <input
              className="mt-1 w-full rounded-lg bg-slate-950/60 border border-slate-800 px-3 py-2 outline-none focus:ring-2 focus:ring-indigo-500"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder={DEMO_USER.email}
              autoComplete="username"
            />
          </div>
          <div>
            <label className="text-sm text-slate-200">Password</label>
            <input
              className="mt-1 w-full rounded-lg bg-slate-950/60 border border-slate-800 px-3 py-2 outline-none focus:ring-2 focus:ring-indigo-500"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              type="password"
              placeholder={DEMO_USER.password}
              autoComplete="current-password"
            />
          </div>

          {error && (
            <div className="rounded-lg border border-rose-900/60 bg-rose-950/40 px-3 py-2 text-sm text-rose-200">
              {error}
            </div>
          )}

          <button
            className="w-full rounded-lg bg-indigo-600 hover:bg-indigo-500 disabled:opacity-60 px-3 py-2 font-medium"
            disabled={loading}
            type="submit"
          >
            {loading ? "Logging in..." : "Login"}
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

