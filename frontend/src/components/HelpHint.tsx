import { useId, useRef, useState } from "react";

export default function HelpHint({ text, className }: { text: string; className?: string }) {
  const [open, setOpen] = useState(false);
  const id = useId();
  const buttonRef = useRef<HTMLButtonElement>(null);

  function close() {
    setOpen(false);
    buttonRef.current?.focus();
  }

  return (
    <span className={`relative inline-flex align-middle ${className ?? ""}`}>
      <button
        ref={buttonRef}
        type="button"
        className="inline-flex h-5 w-5 items-center justify-center rounded-full border border-slate-600 text-[11px] leading-none text-slate-400 hover:border-slate-500 hover:text-slate-200"
        aria-label="More information"
        aria-expanded={open}
        aria-describedby={open ? id : undefined}
        title={text}
        onClick={() => setOpen((v) => !v)}
        onBlur={(e) => {
          if (!e.currentTarget.parentElement?.contains(e.relatedTarget as Node)) {
            setOpen(false);
          }
        }}
      >
        ?
      </button>
      {open && (
        <span
          id={id}
          role="tooltip"
          className="absolute left-1/2 top-full z-50 mt-1.5 w-56 -translate-x-1/2 rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-xs leading-relaxed text-slate-200 shadow-lg"
        >
          {text}
          <button
            type="button"
            className="mt-2 block text-[11px] text-indigo-300 hover:text-indigo-200"
            onClick={close}
          >
            Close
          </button>
        </span>
      )}
    </span>
  );
}
