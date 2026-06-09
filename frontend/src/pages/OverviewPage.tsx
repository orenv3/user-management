import { Link } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import PageSection from "../components/PageSection";
import { displayEmail } from "../utils/redactSensitiveInResults";

export default function OverviewPage() {
  const { me } = useAuth();

  const adminLinks = [
    { to: "/users", label: "Users", desc: "Add, update, and remove team members" },
    { to: "/tasks", label: "Tasks", desc: "Create work and assign it to your team" },
    { to: "/comments", label: "Comments", desc: "View and manage task discussions" },
    { to: "/analytics", label: "Analytics", desc: "Page views, logins, and user actions" },
  ];

  const userLinks = [
    { to: "/my-tasks", label: "My tasks", desc: "View assigned work and mark tasks complete" },
    { to: "/my-comments", label: "My comments", desc: "Comment on your tasks and read replies" },
  ];

  const links = me?.role === "ADMIN" ? adminLinks : userLinks;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold">Overview</h1>

      <PageSection title="Session" description="Your account">
        {me ? (
          <dl className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm max-w-md">
            <dt className="text-slate-400">User ID</dt>
            <dd className="text-slate-100">{me.userId}</dd>
            <dt className="text-slate-400">Email</dt>
            <dd className="text-slate-100">{displayEmail(me.email)}</dd>
            <dt className="text-slate-400">Role</dt>
            <dd className="text-slate-100">{me.role}</dd>
          </dl>
        ) : (
          <p className="text-sm text-slate-400">Loading...</p>
        )}
      </PageSection>

      <PageSection title="Quick links">
        <ul className="space-y-3">
          {links.map((l) => (
            <li key={l.to}>
              <Link to={l.to} className="text-indigo-300 hover:text-indigo-200 font-medium">
                {l.label}
              </Link>
              <span className="text-slate-500 text-sm"> — {l.desc}</span>
            </li>
          ))}
        </ul>
      </PageSection>
    </div>
  );
}
