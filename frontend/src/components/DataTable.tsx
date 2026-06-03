export type Column<T> = {
  key: string;
  header: string;
  render: (row: T) => React.ReactNode;
};

export default function DataTable<T>({
  columns,
  rows,
  emptyMessage = "No data.",
}: {
  columns: Column<T>[];
  rows: T[];
  emptyMessage?: string;
}) {
  if (rows.length === 0) {
    return <p className="text-sm text-slate-400">{emptyMessage}</p>;
  }

  return (
    <div className="overflow-x-auto rounded-lg border border-slate-800">
      <table className="w-full text-sm text-left">
        <thead className="bg-slate-950/80 text-slate-300">
          <tr>
            {columns.map((c) => (
              <th key={c.key} className="px-3 py-2 font-medium border-b border-slate-800">
                {c.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr
              key={i}
              className={i % 2 === 0 ? "bg-slate-900/30" : "bg-slate-950/20"}
            >
              {columns.map((c) => (
                <td key={c.key} className="px-3 py-2 border-b border-slate-800/60 text-slate-200">
                  {c.render(row)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
