import { useCallback, useEffect, useState } from "react";
import { apiGet } from "../api/client";
import type { ActivityEventResponse, ActivitySummaryResponse } from "../api/types";
import DataTable from "../components/DataTable";
import { Field, btnPrimary, btnSecondary, inputClass } from "../components/Field";
import PageSection from "../components/PageSection";
import ResultPanel from "../components/ResultPanel";
import { useAction } from "../hooks/useAction";

type PagedEvents = {
  content: ActivityEventResponse[];
  totalElements: number;
  totalPages: number;
  number: number;
};

export default function AnalyticsPage() {
  const { run: runSummary, ...summaryPanel } = useAction();
  const { run: runEvents, ...eventsPanel } = useAction();
  const [summary, setSummary] = useState<ActivitySummaryResponse | null>(null);
  const [events, setEvents] = useState<ActivityEventResponse[]>([]);
  const [pageNumber, setPageNumber] = useState("0");
  const [pageSize, setPageSize] = useState("20");
  const [eventType, setEventType] = useState("");

  const loadSummary = useCallback(async () => {
    const data = await runSummary(() =>
      apiGet<ActivitySummaryResponse>("/api/analytics/admin/summary")
    );
    setSummary(data as ActivitySummaryResponse);
  }, [runSummary]);

  const loadEvents = useCallback(async () => {
    const typeParam = eventType ? `&eventType=${eventType}` : "";
    const data = await runEvents(() =>
      apiGet<PagedEvents>(
        `/api/analytics/admin/events?pageNumber=${pageNumber}&pageSize=${pageSize}${typeParam}`
      )
    );
    setEvents((data as PagedEvents).content ?? []);
  }, [runEvents, pageNumber, pageSize, eventType]);

  useEffect(() => {
    loadSummary().catch(() => {});
    loadEvents().catch(() => {});
  }, [loadSummary, loadEvents]);

  const eventColumns = [
    { key: "timestamp", header: "Time", render: (e: ActivityEventResponse) => new Date(e.timestamp).toLocaleString() },
    { key: "eventType", header: "Type", render: (e: ActivityEventResponse) => e.eventType },
    { key: "email", header: "User", render: (e: ActivityEventResponse) => e.email ?? "—" },
    { key: "path", header: "Path", render: (e: ActivityEventResponse) => e.path ?? "—" },
    { key: "action", header: "Action", render: (e: ActivityEventResponse) => e.action ?? "—" },
  ];

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-semibold">Analytics</h1>

      <PageSection title="Summary" description="GET /api/analytics/admin/summary">
        {summary ? (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4 max-w-4xl">
            <StatCard label="Page views" value={summary.totalPageViews} />
            <StatCard label="Unique sessions" value={summary.uniqueSessions} />
            <StatCard label="Logins" value={summary.totalLogins} />
            <StatCard label="API actions" value={summary.totalActions} />
          </div>
        ) : (
          <p className="text-sm text-slate-400">Loading summary...</p>
        )}

        {summary && (summary.loginsByUser.length > 0 || summary.actionsByUser.length > 0) && (
          <div className="mt-6 grid gap-6 sm:grid-cols-2 max-w-3xl">
            {summary.loginsByUser.length > 0 && (
              <div>
                <h3 className="text-sm font-medium text-slate-300 mb-2">Logins by user</h3>
                <ul className="text-sm space-y-1 text-slate-400">
                  {summary.loginsByUser.map((row) => (
                    <li key={row.email}>
                      {row.email}: <span className="text-slate-200">{row.count}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
            {summary.actionsByUser.length > 0 && (
              <div>
                <h3 className="text-sm font-medium text-slate-300 mb-2">Actions by user</h3>
                <ul className="text-sm space-y-1 text-slate-400">
                  {summary.actionsByUser.map((row) => (
                    <li key={row.email}>
                      {row.email}: <span className="text-slate-200">{row.count}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}

        <button type="button" className={`${btnSecondary} mt-4`} onClick={() => loadSummary()}>
          Refresh summary
        </button>
        <ResultPanel {...summaryPanel} />
      </PageSection>

      <PageSection title="Event log" description="GET /api/analytics/admin/events">
        <div className="flex flex-wrap gap-4 items-end mb-4">
          <Field label="pageNumber">
            <input className={inputClass} value={pageNumber} onChange={(e) => setPageNumber(e.target.value)} />
          </Field>
          <Field label="pageSize">
            <input className={inputClass} value={pageSize} onChange={(e) => setPageSize(e.target.value)} />
          </Field>
          <Field label="eventType (optional)">
            <select className={inputClass} value={eventType} onChange={(e) => setEventType(e.target.value)}>
              <option value="">All</option>
              <option value="PAGE_VIEW">PAGE_VIEW</option>
              <option value="LOGIN">LOGIN</option>
              <option value="ACTION">ACTION</option>
            </select>
          </Field>
          <button type="button" className={btnPrimary} onClick={() => loadEvents()}>
            Run
          </button>
        </div>
        <DataTable columns={eventColumns} rows={events} />
        <ResultPanel {...eventsPanel} />
      </PageSection>
    </div>
  );
}

function StatCard({ label, value }: { label: string; value: number }) {
  return (
    <div className="rounded-lg border border-slate-800 bg-slate-950/50 px-4 py-3">
      <div className="text-xs text-slate-400">{label}</div>
      <div className="text-2xl font-semibold text-slate-100 mt-1">{value}</div>
    </div>
  );
}
