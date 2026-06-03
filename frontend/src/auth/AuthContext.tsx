import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { apiGet, logout as clearToken, setToken } from "../api/client";
import type { AuthResponse } from "../api/types";

type AuthContextValue = {
  me: AuthResponse | null;
  loading: boolean;
  refresh: () => Promise<void>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [me, setMe] = useState<AuthResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const refresh = useCallback(async () => {
    const token = sessionStorage.getItem("token");
    if (!token) {
      setMe(null);
      return;
    }
    const res = await apiGet<AuthResponse>("/api/auth/me");
    setMe(res);
  }, []);

  useEffect(() => {
    (async () => {
      try {
        await refresh();
      } catch {
        clearToken();
        setMe(null);
      } finally {
        setLoading(false);
      }
    })();
  }, [refresh]);

  const logout = useCallback(() => {
    clearToken();
    setMe(null);
  }, []);

  const value = useMemo(
    () => ({ me, loading, refresh, logout }),
    [me, loading, refresh, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
