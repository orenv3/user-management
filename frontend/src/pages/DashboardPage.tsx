import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiGet, logout, type AuthResponse } from "../api/client";
import type { TaskResponse, TaskTableResponse } from "../api/types";

export default function DashboardPage() {
  const nav = useNavigate();
  const [me, setMe] = useState<AuthResponse | null>(null);
  const [tasksUser, setTasksUser] = useState<TaskTableResponse[] | null>(null);
  const [tasksAdmin, setTasksAdmin] = useState<TaskResponse[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const role = useMemo(() => me?.role ?? null, [me]);

  useEffect(() => {
    (async () => {
      try {
        setError(null);
        const meRes = await apiGet<AuthResponse>("/api/auth/me");
        setMe(meRes);

        if (meRes.role === "ADMIN") {
          const all = await apiGet<TaskResponse[]>("/api/task/admin/allTaskList");
          setTasksAdmin(all);
          setTasksUser(null);
        } else {
          const list = await apiGet<TaskTableResponse[]>(
            `/api/task/user/allTaskList/${meRes.userId}`
          );
          setTasksUser(list);
          setTasksAdmin(null);
        }
      } catch (e) {
        logout();
        nav("/login", { replace: true });
      }
    })();
  }, [nav]);

  function onLogout() {
    logout();
    nav("/login", { replace: true });
  }

  return (
    <div className="min-h-screen">
      <header className="border-b border-slate-800 bg-slate-950/40">
        <div className="mx-auto max-w-5xl px-6 py-4 flex items-center justify-between">
          <div>
            <div className="text-lg font-semibold">Dashboard</div>
            <div className="text-xs text-slate-400">
              {me ? (
                <>
                  Signed in as <span className="text-slate-200">{me.email}</span> (
                  {me.role})
                </>
              ) : (
                "Loading..."
              )}
            </div>
          </div>
          <div className="flex items-center gap-3">
            <a
              className="text-sm underline text-slate-300 hover:text-slate-100"
              href="/swagger-ui/index.html"
            >
              Swagger
            </a>
            <button
              className="rounded-lg border border-slate-700 px-3 py-2 text-sm hover:bg-slate-900"
              onClick={onLogout}
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-5xl px-6 py-8">
        {error && (
          <div className="mb-4 rounded-lg border border-rose-900/60 bg-rose-950/40 px-3 py-2 text-sm text-rose-200">
            {error}
          </div>
        )}

        {role === "ADMIN" && (
          <section className="rounded-2xl border border-slate-800 bg-slate-900/40 p-5">
            <h2 className="text-lg font-semibold">All tasks (admin)</h2>
            <p className="mt-1 text-sm text-slate-300">
              This uses <code className="text-slate-200">GET /api/task/admin/allTaskList</code>.
            </p>
            <div className="mt-4 overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="text-slate-400">
                  <tr className="border-b border-slate-800">
                    <th className="py-2 text-left">ID</th>
                    <th className="py-2 text-left">Title</th>
                    <th className="py-2 text-left">Status</th>
                    <th className="py-2 text-left">Assignee</th>
                  </tr>
                </thead>
                <tbody>
                  {tasksAdmin?.map((t) => (
                    <tr key={t.id} className="border-b border-slate-900">
                      <td className="py-2 text-slate-300">{t.id}</td>
                      <td className="py-2">{t.title}</td>
                      <td className="py-2 text-slate-300">{t.status}</td>
                      <td className="py-2 text-slate-300">{t.assigneeId ?? "-"}</td>
                    </tr>
                  ))}
                  {!tasksAdmin && (
                    <tr>
                      <td className="py-3 text-slate-400" colSpan={4}>
                        Loading...
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </section>
        )}

        {role && role !== "ADMIN" && (
          <section className="rounded-2xl border border-slate-800 bg-slate-900/40 p-5">
            <h2 className="text-lg font-semibold">My tasks (user)</h2>
            <p className="mt-1 text-sm text-slate-300">
              This uses{" "}
              <code className="text-slate-200">
                GET /api/task/user/allTaskList/{me?.userId}
              </code>
              .
            </p>
            <div className="mt-4 overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="text-slate-400">
                  <tr className="border-b border-slate-800">
                    <th className="py-2 text-left">ID</th>
                    <th className="py-2 text-left">Title</th>
                    <th className="py-2 text-left">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {tasksUser?.map((t) => (
                    <tr key={t.task_id} className="border-b border-slate-900">
                      <td className="py-2 text-slate-300">{t.task_id}</td>
                      <td className="py-2">{t.task_title}</td>
                      <td className="py-2 text-slate-300">{t.task_status}</td>
                    </tr>
                  ))}
                  {!tasksUser && (
                    <tr>
                      <td className="py-3 text-slate-400" colSpan={3}>
                        Loading...
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </section>
        )}
      </main>
    </div>
  );
}

