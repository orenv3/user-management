import { useCallback, useEffect, useState } from "react";
import { apiDelete, apiGet, apiPost, apiPut } from "../api/client";
import type {
  CreateUserRequest,
  UpdateUserRequest,
  UserResponse,
} from "../api/types";
import DataTable from "../components/DataTable";
import { Field, btnPrimary, btnSecondary, inputClass } from "../components/Field";
import PageSection from "../components/PageSection";
import ResultPanel from "../components/ResultPanel";
import { useAction } from "../hooks/useAction";
import { filterPrivateAdminUsers } from "../utils/redactSensitiveInResults";

export default function UsersPage() {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const listAction = useAction();
  const paginatedAction = useAction();
  const registerAction = useAction();
  const updateAction = useAction();
  const deleteAction = useAction();

  const [pageNumber, setPageNumber] = useState("0");
  const [pageSize, setPageSize] = useState("10");

  const [regName, setRegName] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [regIsAdmin, setRegIsAdmin] = useState(false);
  const [regActive, setRegActive] = useState(true);
  const [regPassword, setRegPassword] = useState("");

  const [updId, setUpdId] = useState("");
  const [updName, setUpdName] = useState("");
  const [updEmail, setUpdEmail] = useState("");
  const [updIsAdmin, setUpdIsAdmin] = useState("");
  const [updActive, setUpdActive] = useState("");
  const [updPassword, setUpdPassword] = useState("");

  const [delId, setDelId] = useState("");

  const applyUserList = useCallback((data: UserResponse[]) => {
    setUsers(filterPrivateAdminUsers(data));
  }, []);

  const loadAll = useCallback(async () => {
    const data = await listAction.run(() => apiGet<UserResponse[]>("/api/userTable/admin/allUserList"));
    applyUserList(data as UserResponse[]);
  }, [listAction, applyUserList]);

  useEffect(() => {
    apiGet<UserResponse[]>("/api/userTable/admin/allUserList")
      .then(applyUserList)
      .catch(() => {});
  }, [applyUserList]);

  const userColumns = [
    { key: "id", header: "ID", render: (u: UserResponse) => u.id },
    { key: "name", header: "Name", render: (u: UserResponse) => u.name },
    {
      key: "email",
      header: "Email",
      render: (u: UserResponse) => u.email,
    },
    { key: "admin", header: "Admin", render: (u: UserResponse) => (u.isAdmin ? "yes" : "no") },
    { key: "active", header: "Active", render: (u: UserResponse) => (u.active ? "yes" : "no") },
  ];

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-semibold">Users</h1>

      <PageSection title="All users" description="GET /api/userTable/admin/allUserList">
        <DataTable columns={userColumns} rows={users} />
        <button type="button" className={`${btnSecondary} mt-3`} onClick={() => loadAll()}>
          Refresh list
        </button>
        <ResultPanel {...listAction} />
      </PageSection>

      <PageSection title="Paginated users" description="GET .../allUserListWithPagination">
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="pageNumber">
            <input className={inputClass} value={pageNumber} onChange={(e) => setPageNumber(e.target.value)} />
          </Field>
          <Field label="pageSize">
            <input className={inputClass} value={pageSize} onChange={(e) => setPageSize(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            onClick={() =>
              paginatedAction.run(() =>
                apiGet<UserResponse[]>(
                  `/api/userTable/admin/allUserListWithPagination?pageNumber=${pageNumber}&pageSize=${pageSize}`
                )
              )
            }
          >
            Run
          </button>
        </div>
        <ResultPanel {...paginatedAction} />
      </PageSection>

      <PageSection title="Register user" description="POST /api/auth/admin/registerUser">
        <div className="grid gap-4 sm:grid-cols-2 max-w-2xl">
          <Field label="name (max 15)">
            <input className={inputClass} value={regName} onChange={(e) => setRegName(e.target.value)} />
          </Field>
          <Field label="email">
            <input className={inputClass} value={regEmail} onChange={(e) => setRegEmail(e.target.value)} />
          </Field>
          <Field label="password">
            <input type="password" className={inputClass} value={regPassword} onChange={(e) => setRegPassword(e.target.value)} />
          </Field>
          <Field label="isAdmin">
            <input type="checkbox" checked={regIsAdmin} onChange={(e) => setRegIsAdmin(e.target.checked)} className="mt-2" />
          </Field>
          <Field label="active">
            <input type="checkbox" checked={regActive} onChange={(e) => setRegActive(e.target.checked)} className="mt-2" />
          </Field>
        </div>
        <button
          type="button"
          className={`${btnPrimary} mt-4`}
          onClick={() => {
            const body: CreateUserRequest = {
              name: regName,
              email: regEmail,
              isAdmin: regIsAdmin,
              active: regActive,
              password: regPassword,
            };
            registerAction.run(() => apiPost("/api/auth/admin/registerUser", body)).then(() => loadAll());
          }}
        >
          Run
        </button>
        <ResultPanel {...registerAction} />
      </PageSection>

      <PageSection title="Update user" description="PUT /api/userTable/admin/updateUser">
        <div className="grid gap-4 sm:grid-cols-2 max-w-2xl">
          <Field label="id (required, min 2)">
            <input className={inputClass} value={updId} onChange={(e) => setUpdId(e.target.value)} />
          </Field>
          <Field label="name">
            <input className={inputClass} value={updName} onChange={(e) => setUpdName(e.target.value)} />
          </Field>
          <Field label="email">
            <input className={inputClass} value={updEmail} onChange={(e) => setUpdEmail(e.target.value)} />
          </Field>
          <Field label="password">
            <input type="password" className={inputClass} value={updPassword} onChange={(e) => setUpdPassword(e.target.value)} />
          </Field>
          <Field label="isAdmin (empty = omit)">
            <select className={inputClass} value={updIsAdmin} onChange={(e) => setUpdIsAdmin(e.target.value)}>
              <option value="">— omit —</option>
              <option value="true">true</option>
              <option value="false">false</option>
            </select>
          </Field>
          <Field label="active (empty = omit)">
            <select className={inputClass} value={updActive} onChange={(e) => setUpdActive(e.target.value)}>
              <option value="">— omit —</option>
              <option value="true">true</option>
              <option value="false">false</option>
            </select>
          </Field>
        </div>
        <button
          type="button"
          className={`${btnPrimary} mt-4`}
          onClick={() => {
            const body: UpdateUserRequest = { id: Number(updId) };
            if (updName) body.name = updName;
            if (updEmail) body.email = updEmail;
            if (updPassword) body.password = updPassword;
            if (updIsAdmin !== "") body.isAdmin = updIsAdmin === "true";
            if (updActive !== "") body.active = updActive === "true";
            updateAction.run(() => apiPut("/api/userTable/admin/updateUser", body)).then(() => loadAll());
          }}
        >
          Run
        </button>
        <ResultPanel {...updateAction} />
      </PageSection>

      <PageSection title="Delete user" description="DELETE .../deleteUser/{id}">
        <div className="flex flex-wrap gap-4 items-end max-w-md">
          <Field label="id (min 2)">
            <input className={inputClass} value={delId} onChange={(e) => setDelId(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            onClick={() => {
              if (!confirm(`Delete user ${delId}?`)) return;
              deleteAction.run(() => apiDelete(`/api/userTable/admin/deleteUser/${delId}`)).then(() => loadAll());
            }}
          >
            Run
          </button>
        </div>
        <ResultPanel {...deleteAction} />
      </PageSection>
    </div>
  );
}
