import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { me, loading } = useAuth();
  const hasToken = !!sessionStorage.getItem("token");

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center text-slate-400">
        Loading...
      </div>
    );
  }

  if (!hasToken || !me) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}

export function AdminRoute({ children }: { children: React.ReactNode }) {
  const { me } = useAuth();
  if (me?.role !== "ADMIN") {
    return <NotAllowed />;
  }
  return <>{children}</>;
}

export function UserRoute({ children }: { children: React.ReactNode }) {
  const { me } = useAuth();
  if (me?.role !== "USER") {
    return <NotAllowed />;
  }
  return <>{children}</>;
}

function NotAllowed() {
  return (
    <div className="rounded-xl border border-amber-900/50 bg-amber-950/30 px-4 py-6 text-amber-100">
      <h2 className="font-semibold text-lg">Not allowed</h2>
      <p className="mt-2 text-sm text-amber-200/80">
        Your account does not have access to this section.
      </p>
    </div>
  );
}
