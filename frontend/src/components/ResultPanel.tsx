import { formatResultForDisplay } from "../utils/redactSensitiveInResults";

export default function ResultPanel({
  loading,
  error,
  result,
}: {
  loading?: boolean;
  error?: string | null;
  result?: unknown;
}) {
  if (loading) {
    return (
      <div className="mt-3 rounded-lg border border-slate-800 bg-slate-950/50 px-3 py-2 text-sm text-slate-400">
        Running...
      </div>
    );
  }

  if (error) {
    return (
      <div className="mt-3 rounded-lg border border-rose-900/60 bg-rose-950/40 px-3 py-2 text-sm text-rose-200">
        {error}
      </div>
    );
  }

  if (result === undefined) return null;

  return (
    <pre className="mt-3 max-h-64 overflow-auto rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-xs text-slate-200 font-mono whitespace-pre-wrap">
      {formatResultForDisplay(result)}
    </pre>
  );
}
