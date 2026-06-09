import { formatResultForDisplay } from "../utils/redactSensitiveInResults";

function ErrorDisplay({
  error,
  fieldErrors,
}: {
  error: string;
  fieldErrors?: Record<string, string> | null;
}) {
  const entries = fieldErrors ? Object.entries(fieldErrors) : [];
  return (
    <div className="mt-3 rounded-lg border border-rose-900/60 bg-rose-950/40 px-3 py-2 text-sm text-rose-200">
      <p>{error}</p>
      {entries.length > 0 && (
        <ul className="mt-2 list-disc pl-5 space-y-1 text-rose-100/90">
          {entries.map(([field, msg]) => (
            <li key={field}>
              <span className="font-medium">{field}:</span> {msg}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default function ResultPanel({
  loading,
  error,
  fieldErrors,
  result,
}: {
  loading?: boolean;
  error?: string | null;
  fieldErrors?: Record<string, string> | null;
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
    return <ErrorDisplay error={error} fieldErrors={fieldErrors} />;
  }

  if (result === undefined) return null;

  return (
    <pre className="mt-3 max-h-64 overflow-auto rounded-lg border border-slate-800 bg-slate-950/60 px-3 py-2 text-xs text-slate-200 font-mono whitespace-pre-wrap">
      {formatResultForDisplay(result)}
    </pre>
  );
}
