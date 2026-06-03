import { useCallback, useState } from "react";

export function useAction() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<unknown>(undefined);

  const run = useCallback(async (fn: () => Promise<unknown>) => {
    setLoading(true);
    setError(null);
    setResult(undefined);
    try {
      const data = await fn();
      setResult(data);
      return data;
    } catch (e) {
      setError(e instanceof Error ? e.message : "Request failed");
      throw e;
    } finally {
      setLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setError(null);
    setResult(undefined);
    setLoading(false);
  }, []);

  return { loading, error, result, run, reset };
}
