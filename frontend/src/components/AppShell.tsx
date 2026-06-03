import { Link, Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { displayEmail } from "../utils/redactSensitiveInResults";

const adminLinks = [
  { to: "/", label: "Overview" },
  { to: "/users", label: "Users" },
  { to: "/tasks", label: "Tasks" },
  { to: "/comments", label: "Comments" },
];

const userLinks = [
  { to: "/", label: "Overview" },
  { to: "/my-tasks", label: "My tasks" },
  { to: "/my-comments", label: "My comments" },
];

export default function AppShell() {
  const { me, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  function handleLogout() {
    logout();
    navigate("/login", { replace: true });
  }
  const links = me?.role === "ADMIN" ? adminLinks : userLinks;

  return (
    <div className="min-h-screen flex">
      <aside className="w-52 shrink-0 border-r border-slate-800 bg-slate-950/60 p-4">
        <div className="text-sm font-semibold text-slate-100">User Management</div>
        <nav className="mt-6 space-y-1">
          {links.map((l) => (
            <Link
              key={l.to}
              to={l.to}
              className={`block rounded-lg px-3 py-2 text-sm ${
                location.pathname === l.to
                  ? "bg-indigo-600/25 text-indigo-200 border border-indigo-500/40"
                  : "text-slate-300 hover:bg-slate-900"
              }`}
            >
              {l.label}
            </Link>
          ))}
        </nav>
      </aside>

      <div className="flex-1 flex flex-col min-w-0">
        <header className="border-b border-slate-800 bg-slate-950/40 px-6 py-4 flex items-center justify-between gap-4">
          <div className="text-xs text-slate-400 min-w-0">
            {me ? (
              <>
                Signed in as <span className="text-slate-200">{displayEmail(me.email)}</span> ({me.role})
              </>
            ) : (
              "Loading session..."
            )}
          </div>
          <div className="flex items-center gap-3 shrink-0">
            <a
              className="text-sm underline text-slate-300 hover:text-slate-100"
              href="/swagger-ui/index.html"
              target="_blank"
              rel="noreferrer"
            >
              Swagger
            </a>
            <button
              type="button"
              className="rounded-lg border border-slate-700 px-3 py-2 text-sm hover:bg-slate-900"
              onClick={handleLogout}
            >
              Logout
            </button>
          </div>
        </header>

        <main className="flex-1 overflow-auto px-6 py-8 max-w-6xl">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
