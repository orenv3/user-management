import { FormEvent, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { ApiClientError, apiPost, setToken } from "../api/client";
import type { AuthResponse } from "../api/types";
import { useAuth } from "../auth/AuthContext";
import DemoVideo, { hasDemoVideo } from "../components/DemoVideo";
import AppFooter from "../components/AppFooter";
import HelpHint from "../components/HelpHint";
import { Field, btnPrimary, btnSecondary, inputClass } from "../components/Field";
import { hints } from "../content/hints";
import { getRuntimeConfig } from "../config/runtimeConfig";
import { getProjectRepoUrl } from "../utils/demoConfig";
import { summarizeFieldErrors, validateLoginFields } from "../utils/validateAuthFields";

function isAuthFailureMessage(message: string): boolean {
  return (
    message === "Unauthorized" ||
    message === "Invalid email or password" ||
    /^Request failed \(401\)$/.test(message)
  );
}

export default function LoginPage() {
  const nav = useNavigate();
  const { me, loading, refresh } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string> | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const showVideo = hasDemoVideo();
  const projectRepoUrl = getProjectRepoUrl();
  const {
    seedUserEmail,
    seedUserPassword,
    seedAdminEmail,
    seedAdminPassword,
  } = getRuntimeConfig();

  if (!loading && me) {
    return <Navigate to="/" replace />;
  }

  function clearErrors() {
    setError(null);
    setFieldErrors(null);
  }

  function fillDemoUser() {
    if (seedUserEmail) setEmail(seedUserEmail);
    if (seedUserPassword) setPassword(seedUserPassword);
    clearErrors();
  }

  function fillDemoAdmin() {
    if (seedAdminEmail) setEmail(seedAdminEmail);
    if (seedAdminPassword) setPassword(seedAdminPassword);
    clearErrors();
  }

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    clearErrors();

    const clientFieldErrors = validateLoginFields(email, password);
    if (clientFieldErrors) {
      setFieldErrors(clientFieldErrors);
      setError(summarizeFieldErrors(clientFieldErrors));
      return;
    }

    setSubmitting(true);
    try {
      const res = await apiPost<AuthResponse>("/api/auth/login", {
        email: email.trim(),
        password,
      });
      setToken(res.token);
      await refresh();
      nav("/", { replace: true });
    } catch (err) {
      if (err instanceof ApiClientError) {
        if (isAuthFailureMessage(err.message)) {
          setError("Invalid email or password");
          setFieldErrors(null);
        } else {
          setError(err.message);
          setFieldErrors(err.fieldErrors ?? null);
        }
      } else {
        setError(err instanceof Error ? err.message : "Login failed");
        setFieldErrors(null);
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="min-h-screen flex flex-col">
      <div className="flex-1 flex items-center justify-center p-4 sm:p-6">
        <div
          className={`w-full rounded-2xl border border-slate-800 bg-slate-900/60 p-5 sm:p-6 shadow-xl ${
            showVideo ? "max-w-4xl" : "max-w-md"
          }`}
        >
        <div className={showVideo ? "flex flex-col lg:flex-row gap-8" : undefined}>
          {showVideo && (
            <div className="flex-1 min-w-0">
              <DemoVideo />
            </div>
          )}

          <div className={showVideo ? "flex-1 min-w-0" : undefined}>
            <h1 className="text-2xl font-semibold">User Management</h1>
            <p className="mt-2 text-sm text-slate-300">
              Portfolio demo — sign in as a manager or team member to explore task assignment,
              progress tracking, and comments.
            </p>

            {projectRepoUrl && (
              <p className="mt-2 text-sm">
                <a
                  className="text-indigo-300 hover:text-indigo-200 underline"
                  href={projectRepoUrl}
                  target="_blank"
                  rel="noreferrer"
                >
                  About this project
                </a>
              </p>
            )}

            {(seedUserEmail || seedAdminEmail) && (
              <div className="mt-4 rounded-lg border border-slate-800 bg-slate-950/40 p-4">
                <h2 className="text-sm font-medium text-slate-200">{hints.login.demoTitle}</h2>
                <p className="mt-1 text-xs text-slate-400">{hints.login.demoIntro}</p>
                <div className="mt-3 flex flex-col gap-2">
                  {seedAdminEmail && (
                    <div className="flex flex-wrap items-center gap-2">
                      <button type="button" className={btnSecondary} onClick={fillDemoAdmin}>
                        {hints.login.useManagerAccount}
                      </button>
                      <span className="text-xs text-slate-500">{seedAdminEmail}</span>
                      <HelpHint text={hints.login.managerAccountHelp} />
                    </div>
                  )}
                  {seedUserEmail && (
                    <div className="flex flex-wrap items-center gap-2">
                      <button type="button" className={btnSecondary} onClick={fillDemoUser}>
                        {hints.login.useTeamMemberAccount}
                      </button>
                      <span className="text-xs text-slate-500">{seedUserEmail}</span>
                      <HelpHint text={hints.login.teamMemberAccountHelp} />
                    </div>
                  )}
                </div>
              </div>
            )}

            <form className="mt-6 space-y-4" onSubmit={onSubmit}>
              <Field label="Email" hint={hints.login.emailHint}>
                <input
                  className={inputClass}
                  value={email}
                  onChange={(e) => {
                    setEmail(e.target.value);
                    if (fieldErrors?.email) clearErrors();
                  }}
                  type="email"
                  autoComplete="username"
                />
              </Field>
              <Field label="Password" hint={hints.login.passwordHint}>
                <input
                  className={inputClass}
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                    if (fieldErrors?.password) clearErrors();
                  }}
                  type="password"
                  autoComplete="current-password"
                />
              </Field>

              {error && (
                <div className="rounded-lg border border-rose-900/60 bg-rose-950/40 px-3 py-2 text-sm text-rose-200">
                  <p>{error}</p>
                  {fieldErrors && Object.keys(fieldErrors).length > 0 && (
                    <ul className="mt-2 list-disc pl-5 space-y-1 text-rose-100/90">
                      {Object.entries(fieldErrors).map(([field, msg]) => (
                        <li key={field}>
                          <span className="font-medium">{field}:</span> {msg}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              )}

              <button className={`w-full ${btnPrimary}`} disabled={submitting} type="submit">
                {submitting ? "Logging in..." : "Login"}
              </button>

              <div className="text-xs text-slate-400">
                Developer docs:{" "}
                <a className="underline" href="/swagger-ui/index.html" title={hints.login.swagger}>
                  API reference (Swagger)
                </a>
              </div>
            </form>
          </div>
        </div>
      </div>
      </div>
      <AppFooter />
    </div>
  );
}
