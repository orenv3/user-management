import type { ReactNode } from "react";
import HelpHint from "./HelpHint";

export function Field({
  label,
  children,
  hint,
  help,
}: {
  label: string;
  children: ReactNode;
  hint?: string;
  help?: string;
}) {
  return (
    <div>
      <div className="flex items-center gap-1.5">
        <label className="text-sm text-slate-200">{label}</label>
        {help && <HelpHint text={help} />}
      </div>
      {hint && <p className="text-xs text-slate-500 mt-0.5">{hint}</p>}
      <div className="mt-1">{children}</div>
    </div>
  );
}

export const inputClass =
  "w-full rounded-lg bg-slate-950/60 border border-slate-800 px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-500";

export const btnPrimary =
  "rounded-lg bg-indigo-600 hover:bg-indigo-500 disabled:opacity-60 px-4 py-2 text-sm font-medium";

export const btnSecondary =
  "rounded-lg border border-slate-700 px-4 py-2 text-sm hover:bg-slate-900 disabled:opacity-60";
