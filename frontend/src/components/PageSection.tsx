import type { ReactNode } from "react";

export default function PageSection({
  id,
  title,
  description,
  devDescription,
  children,
}: {
  id?: string;
  title: string;
  description?: string;
  devDescription?: string;
  children: ReactNode;
}) {
  return (
    <section
      id={id}
      className="rounded-xl border border-slate-800 bg-slate-900/40 p-4 sm:p-5 shadow-sm"
    >
      <h2 className="text-lg font-semibold text-slate-100">{title}</h2>
      {description && <p className="mt-1 text-sm text-slate-400">{description}</p>}
      {devDescription && (
        <details className="mt-2 text-xs text-slate-500">
          <summary className="cursor-pointer hover:text-slate-400">Developer info</summary>
          <p className="mt-1 font-mono">{devDescription}</p>
        </details>
      )}
      <div className="mt-4">{children}</div>
    </section>
  );
}
