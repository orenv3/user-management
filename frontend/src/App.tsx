import { Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import AppShell from "./components/AppShell";
import { AdminRoute, ProtectedRoute, UserRoute } from "./components/ProtectedRoute";
import { usePageTracking } from "./hooks/usePageTracking";
import AnalyticsPage from "./pages/AnalyticsPage";
import CommentsPage from "./pages/CommentsPage";
import LoginPage from "./pages/LoginPage";
import MyCommentsPage from "./pages/MyCommentsPage";
import MyTasksPage from "./pages/MyTasksPage";
import OverviewPage from "./pages/OverviewPage";
import TasksPage from "./pages/TasksPage";
import UsersPage from "./pages/UsersPage";

function AppRoutes() {
  usePageTracking();

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <ProtectedRoute>
            <AppShell />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<OverviewPage />} />
        <Route
          path="/analytics"
          element={
            <AdminRoute>
              <AnalyticsPage />
            </AdminRoute>
          }
        />
        <Route
          path="/users"
          element={
            <AdminRoute>
              <UsersPage />
            </AdminRoute>
          }
        />
          <Route
            path="/tasks"
            element={
              <AdminRoute>
                <TasksPage />
              </AdminRoute>
            }
          />
          <Route
            path="/comments"
            element={
              <AdminRoute>
                <CommentsPage />
              </AdminRoute>
            }
          />
          <Route
            path="/my-tasks"
            element={
              <UserRoute>
                <MyTasksPage />
              </UserRoute>
            }
          />
          <Route
            path="/my-comments"
            element={
              <UserRoute>
                <MyCommentsPage />
              </UserRoute>
            }
          />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  );
}
