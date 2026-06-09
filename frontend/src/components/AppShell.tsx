import { useState } from "react";
import { Link, Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { displayEmail } from "../utils/redactSensitiveInResults";

const adminLinks = [
  { to: "/", label: "Overview" },
  { to: "/users", label: "Users" },
  { to: "/tasks", label: "Tasks" },
  { to: "/comments", label: "Comments" },
  { to: "/analytics", label: "Analytics" },
];

const userLinks = [
  { to: "/", label: "Overview" },
  { to: "/my-tasks", label: "My tasks" },
  { to: "/my-comments", label: "My comments" },
];

function NavLinks({
  links,
  pathname,
  onNavigate,
  className,
}: {
  links: { to: string; label: string }[];
  pathname: string;
  onNavigate?: () => void;
  className?: string;
}) {
  return (
    <nav className={className}>
      {links.map((l) => (
        <Link
          key={l.to}
          to={l.to}
          onClick={onNavigate}
          className={`block rounded-lg px-3 py-2 text-sm ${
            pathname === l.to
              ? "bg-indigo-600/25 text-indigo-200 border border-indigo-500/40"
              : "text-slate-300 hover:bg-slate-900"
          }`}
        >
          {l.label}
        </Link>
      ))}
    </nav>
  );
}

export default function AppShell() {
  const { me, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileNavOpen, setMobileNavOpen] = useState(false);

  function handleLogout() {
    logout();
    navigate("/login", { replace: true });
  }

  const links = me?.role === "ADMIN" ? adminLinks : userLinks;

  function closeMobileNav() {
    setMobileNavOpen(false);
  }

  return (
    <div className="min-h-screen flex flex-col md:flex-row">
      <aside className="hidden md:block md:w-52 md:shrink-0 border-r border-slate-800 bg-slate-950/60 p-4">
        <div className="text-sm font-semibold text-slate-100">User Management</div>
        <NavLinks links={links} pathname={location.pathname} className="mt-6 space-y-1" />
      </aside>

      {mobileNavOpen && (
        <div className="fixed inset-0 z-40 md:hidden">
          <button
            type="button"
            className="absolute inset-0 bg-black/60"
            aria-label="Close menu"
            onClick={closeMobileNav}
          />
          <aside className="relative z-50 h-full w-64 max-w-[85vw] border-r border-slate-800 bg-slate-950 p-4 shadow-xl">
            <div className="flex items-center justify-between mb-4">
              <div className="text-sm font-semibold text-slate-100">User Management</div>
              <button
                type="button"
                className="rounded-lg border border-slate-700 px-2 py-1 text-sm text-slate-300 hover:bg-slate-900"
                onClick={closeMobileNav}
                aria-label="Close navigation"
              >
                Close
              </button>
            </div>
            <NavLinks
              links={links}
              pathname={location.pathname}
              onNavigate={closeMobileNav}
              className="space-y-1"
            />
          </aside>
        </div>
      )}

      <div className="flex-1 flex flex-col min-w-0">
        <header className="border-b border-slate-800 bg-slate-950/40 px-4 sm:px-6 py-3 sm:py-4 flex flex-wrap items-center justify-between gap-2 sm:gap-4">
          <div className="flex items-center gap-2 min-w-0 flex-1">
            <button
              type="button"
              className="md:hidden rounded-lg border border-slate-700 px-2 py-1.5 text-sm text-slate-300 hover:bg-slate-900 shrink-0"
              onClick={() => setMobileNavOpen(true)}
              aria-label="Open navigation"
            >
              Menu
            </button>
            <div className="text-xs text-slate-400 min-w-0 truncate">
              {me ? (
                <>
                  Signed in as <span className="text-slate-200">{displayEmail(me.email)}</span> (
                  {me.role})
                </>
              ) : (
                "Loading session..."
              )}
            </div>
          </div>
          <div className="flex items-center gap-2 sm:gap-3 shrink-0">
            <a
              className="text-sm underline text-slate-300 hover:text-slate-100 hidden sm:inline"
              href="/swagger-ui/index.html"
              target="_blank"
              rel="noreferrer"
            >
              Swagger
            </a>
            <a
              className="text-sm underline text-slate-300 hover:text-slate-100 sm:hidden"
              href="/swagger-ui/index.html"
              target="_blank"
              rel="noreferrer"
            >
              API
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

        <main className="flex-1 overflow-auto px-4 sm:px-6 py-6 sm:py-8 w-full max-w-6xl">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
