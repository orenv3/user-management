import { Link } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import HelpHint from "../components/HelpHint";
import PageSection from "../components/PageSection";
import { adminNavLinks, hints, userNavLinks } from "../content/hints";
import { displayEmail } from "../utils/redactSensitiveInResults";

export default function OverviewPage() {
  const { me } = useAuth();

  const links = me?.role === "ADMIN" ? adminNavLinks.slice(1) : userNavLinks.slice(1);

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold">Overview</h1>

      <PageSection title="Session" description={hints.overview.session}>
        {me ? (
          <dl className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm max-w-md">
            <dt className="text-slate-400 flex items-center gap-1">
              User ID
              <HelpHint text={hints.overview.userIdHelp} />
            </dt>
            <dd className="text-slate-100">{me.userId}</dd>
            <dt className="text-slate-400">Email</dt>
            <dd className="text-slate-100">{displayEmail(me.email)}</dd>
            <dt className="text-slate-400 flex items-center gap-1">
              Role
              <HelpHint text={hints.overview.roleHelp} />
            </dt>
            <dd className="text-slate-100">{me.role}</dd>
          </dl>
        ) : (
          <p className="text-sm text-slate-400">Loading...</p>
        )}
      </PageSection>

      <PageSection title="Quick links" description={hints.overview.quickLinks}>
        <ul className="space-y-3">
          {links.map((l) => (
            <li key={l.to} className="flex flex-wrap items-center gap-x-2 gap-y-1">
              <Link
                to={l.to}
                className="text-indigo-300 hover:text-indigo-200 font-medium"
                title={l.hint}
              >
                {l.label}
              </Link>
              <span className="text-slate-500 text-sm">— {l.hint}</span>
              <HelpHint text={l.hint} />
            </li>
          ))}
        </ul>
      </PageSection>
    </div>
  );
}
