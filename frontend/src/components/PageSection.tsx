import type { ReactNode } from "react";

export default function PageSection({
  title,
  description,
  children,
}: {
  title: string;
  description?: string;
  children: ReactNode;
}) {
  return (
    <section className="rounded-xl border border-slate-800 bg-slate-900/40 p-4 sm:p-5 shadow-sm">
      <h2 className="text-lg font-semibold text-slate-100">{title}</h2>
      {description && <p className="mt-1 text-sm text-slate-400">{description}</p>}
      <div className="mt-4">{children}</div>
    </section>
  );
}
