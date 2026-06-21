import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiDelete, apiGet, apiPost, apiPut } from "../api/client";
import type {
  CreateUserRequest,
  UpdateUserRequest,
  UserResponse,
} from "../api/types";
import DataTable from "../components/DataTable";
import HelpHint from "../components/HelpHint";
import { Field, btnPrimary, btnSecondary, inputClass } from "../components/Field";
import PageSection from "../components/PageSection";
import ResultPanel from "../components/ResultPanel";
import { hints } from "../content/hints";
import { useAction } from "../hooks/useAction";
import { getRuntimeConfig } from "../config/runtimeConfig";
import { filterPrivateAdminUsers } from "../utils/redactSensitiveInResults";

export default function UsersPage() {
  const {
    seedUserName,
    seedUserEmail,
    seedUserPassword,
    seedAdminName,
    seedAdminEmail,
    seedAdminPassword,
  } = getRuntimeConfig();
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
    const data = await listAction.run(async () => {
      const raw = await apiGet<UserResponse[]>("/api/userTable/admin/allUserList");
      return filterPrivateAdminUsers(raw);
    });
    applyUserList(data as UserResponse[]);
  }, [listAction, applyUserList]);

  useEffect(() => {
    apiGet<UserResponse[]>("/api/userTable/admin/allUserList")
      .then(applyUserList)
      .catch(() => {});
  }, [applyUserList]);

  function fillRegisterUser() {
    if (seedUserName) setRegName(seedUserName);
    if (seedUserEmail) setRegEmail(seedUserEmail);
    if (seedUserPassword) setRegPassword(seedUserPassword);
    setRegIsAdmin(false);
    setRegActive(true);
    registerAction.reset();
  }

  function fillRegisterAdmin() {
    if (seedAdminName) setRegName(seedAdminName);
    if (seedAdminEmail) setRegEmail(seedAdminEmail);
    if (seedAdminPassword) setRegPassword(seedAdminPassword);
    setRegIsAdmin(true);
    setRegActive(true);
    registerAction.reset();
  }

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

      <PageSection
        title="All users"
        description={hints.users.allUsers}
        devDescription={hints.users.devAllUsers}
      >
        <DataTable columns={userColumns} rows={users} />
        <span className="inline-flex items-center gap-1 mt-3">
          <button
            type="button"
            className={btnSecondary}
            onClick={() => loadAll()}
            title={hints.common.refreshList}
          >
            Refresh list
          </button>
          <HelpHint text={hints.common.refreshList} />
        </span>
        <ResultPanel {...listAction} />
      </PageSection>

      <PageSection
        title="Paginated users"
        description={`${hints.users.paginated} ${hints.common.advancedSection}`}
        devDescription={hints.users.devPaginated}
      >
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="pageNumber" hint="Page number (starts at 0).">
            <input className={inputClass} value={pageNumber} onChange={(e) => setPageNumber(e.target.value)} />
          </Field>
          <Field label="pageSize" hint="How many users per page.">
            <input className={inputClass} value={pageSize} onChange={(e) => setPageSize(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            title={hints.common.runPaginated}
            onClick={() =>
              paginatedAction.run(async () => {
                const data = await apiGet<UserResponse[]>(
                  `/api/userTable/admin/allUserListWithPagination?pageNumber=${pageNumber}&pageSize=${pageSize}`
                );
                return filterPrivateAdminUsers(data);
              })
            }
          >
            Run
          </button>
        </div>
        <ResultPanel {...paginatedAction} />
      </PageSection>

      <PageSection
        title="Register user"
        description={hints.users.register}
        devDescription={hints.users.devRegister}
      >
        {(seedUserEmail || seedAdminEmail) && (
          <div className="mb-4 rounded-lg border border-slate-800 bg-slate-950/40 p-3">
            <p className="text-xs text-slate-400">{hints.users.registerDemoIntro}</p>
            <div className="mt-2 flex flex-col gap-2">
              {seedAdminEmail && (
                <div className="flex flex-wrap items-center gap-2">
                  <button type="button" className={btnSecondary} onClick={fillRegisterAdmin}>
                    {hints.login.useManagerAccount}
                  </button>
                  <span className="text-xs text-slate-500">{seedAdminEmail}</span>
                  <HelpHint text={hints.login.managerAccountHelp} />
                </div>
              )}
              {seedUserEmail && (
                <div className="flex flex-wrap items-center gap-2">
                  <button type="button" className={btnSecondary} onClick={fillRegisterUser}>
                    {hints.login.useTeamMemberAccount}
                  </button>
                  <span className="text-xs text-slate-500">{seedUserEmail}</span>
                  <HelpHint text={hints.login.teamMemberAccountHelp} />
                </div>
              )}
            </div>
          </div>
        )}
        <div className="grid gap-4 sm:grid-cols-2 max-w-2xl">
          <Field label="name (max 15 characters)" hint={hints.users.name}>
            <input className={inputClass} value={regName} onChange={(e) => setRegName(e.target.value)} />
          </Field>
          <Field label="email" hint={hints.users.email}>
            <input className={inputClass} value={regEmail} onChange={(e) => setRegEmail(e.target.value)} />
          </Field>
          <Field label="password" hint={hints.users.password}>
            <input type="password" className={inputClass} value={regPassword} onChange={(e) => setRegPassword(e.target.value)} />
          </Field>
          <Field label="isAdmin" hint={hints.users.isAdmin}>
            <input type="checkbox" checked={regIsAdmin} onChange={(e) => setRegIsAdmin(e.target.checked)} className="mt-2" />
          </Field>
          <Field label="active" hint={hints.users.active}>
            <input type="checkbox" checked={regActive} onChange={(e) => setRegActive(e.target.checked)} className="mt-2" />
          </Field>
        </div>
        <span className="inline-flex items-center gap-1 mt-4">
          <button
            type="button"
            className={btnPrimary}
            title={hints.users.afterCreateAccountHelp}
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
            {hints.users.createAccount}
          </button>
          <HelpHint text={hints.users.afterCreateAccountHelp} />
        </span>
        <p className="mt-3 text-xs text-slate-400 max-w-2xl">
          {hints.users.afterCreateAccount}{" "}
          <Link to="/tasks" className="text-indigo-300 hover:text-indigo-200 underline">
            Go to Tasks
          </Link>
        </p>
        <ResultPanel {...registerAction} />
      </PageSection>

      <PageSection
        title="Update user"
        description={hints.users.update}
        devDescription={hints.users.devUpdate}
      >
        <div className="grid gap-4 sm:grid-cols-2 max-w-2xl">
          <Field label="id (required, min 2)" hint={hints.users.updateId} help={hints.common.idFromTable}>
            <input className={inputClass} value={updId} onChange={(e) => setUpdId(e.target.value)} />
          </Field>
          <Field label="name" hint={hints.users.name}>
            <input className={inputClass} value={updName} onChange={(e) => setUpdName(e.target.value)} />
          </Field>
          <Field label="email" hint={hints.users.email}>
            <input className={inputClass} value={updEmail} onChange={(e) => setUpdEmail(e.target.value)} />
          </Field>
          <Field label="password" hint={hints.users.password}>
            <input type="password" className={inputClass} value={updPassword} onChange={(e) => setUpdPassword(e.target.value)} />
          </Field>
          <Field label="isAdmin (empty = omit)" hint={hints.users.omitField}>
            <select className={inputClass} value={updIsAdmin} onChange={(e) => setUpdIsAdmin(e.target.value)}>
              <option value="">— omit —</option>
              <option value="true">true</option>
              <option value="false">false</option>
            </select>
          </Field>
          <Field label="active (empty = omit)" hint={hints.users.omitField}>
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
          {hints.users.saveChanges}
        </button>
        <ResultPanel {...updateAction} />
      </PageSection>

      <PageSection
        title="Delete user"
        description={hints.users.delete}
        devDescription={hints.users.devDelete}
      >
        <div className="flex flex-wrap gap-4 items-end max-w-md">
          <Field label="id (min 2)" hint={hints.users.updateId} help={hints.common.idFromTable}>
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
            {hints.users.deleteUser}
          </button>
        </div>
        <ResultPanel {...deleteAction} />
      </PageSection>
    </div>
  );
}
