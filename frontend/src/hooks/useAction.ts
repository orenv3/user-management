import { useCallback, useState } from "react";
import { ApiClientError } from "../api/client";

export function useAction() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string> | null>(null);
  const [result, setResult] = useState<unknown>(undefined);

  const run = useCallback(async (fn: () => Promise<unknown>) => {
    setLoading(true);
    setError(null);
    setFieldErrors(null);
    setResult(undefined);
    try {
      const data = await fn();
      setResult(data);
      return data;
    } catch (e) {
      if (e instanceof ApiClientError) {
        setError(e.message);
        setFieldErrors(e.fieldErrors ?? null);
      } else {
        setError(e instanceof Error ? e.message : "Request failed");
        setFieldErrors(null);
      }
      throw e;
    } finally {
      setLoading(false);
    }
  }, []);

  const reset = useCallback(() => {
    setError(null);
    setFieldErrors(null);
    setResult(undefined);
    setLoading(false);
  }, []);

  return { loading, error, fieldErrors, result, run, reset };
}
