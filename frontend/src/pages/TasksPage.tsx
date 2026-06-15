import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiDelete, apiGet, apiPost, apiPut } from "../api/client";
import { buildAssignUserPath } from "../api/paths";
import type { CreateTaskRequest, TaskResponse, UpdateTaskRequest } from "../api/types";
import DataTable from "../components/DataTable";
import HelpHint from "../components/HelpHint";
import { Field, btnPrimary, btnSecondary, inputClass } from "../components/Field";
import PageSection from "../components/PageSection";
import ResultPanel from "../components/ResultPanel";
import { hints } from "../content/hints";
import { useAction } from "../hooks/useAction";

const STATUSES = ["", "PENDING", "COMPLETED", "ARCHIVED"];

export default function TasksPage() {
  const [tasks, setTasks] = useState<TaskResponse[]>([]);
  const paginatedAction = useAction();
  const createAction = useAction();
  const updateAction = useAction();
  const deleteAction = useAction();
  const assignAction = useAction();
  const unassignAction = useAction();

  const [pageNo, setPageNo] = useState("0");
  const [pageSize, setPageSize] = useState("10");

  const [crtTitle, setCrtTitle] = useState("");
  const [crtDesc, setCrtDesc] = useState("");
  const [crtStatus, setCrtStatus] = useState("");

  const [updId, setUpdId] = useState("");
  const [updTitle, setUpdTitle] = useState("");
  const [updDesc, setUpdDesc] = useState("");
  const [updStatus, setUpdStatus] = useState("");

  const [delId, setDelId] = useState("");
  const [assignTaskId, setAssignTaskId] = useState("");
  const [assignUserId, setAssignUserId] = useState("");
  const [unassignTaskId, setUnassignTaskId] = useState("");

  const refresh = () =>
    apiGet<TaskResponse[]>("/api/task/admin/allTaskList").then(setTasks).catch(() => {});

  useEffect(() => {
    refresh();
  }, []);

  const columns = [
    { key: "id", header: "ID", render: (t: TaskResponse) => t.id },
    { key: "title", header: "Title", render: (t: TaskResponse) => t.title },
    { key: "desc", header: "Description", render: (t: TaskResponse) => t.description },
    { key: "status", header: "Status", render: (t: TaskResponse) => t.status },
    { key: "assignee", header: "Assignee", render: (t: TaskResponse) => t.assigneeId ?? "—" },
  ];

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-semibold">Tasks</h1>

      <PageSection
        title="All tasks"
        description={hints.tasks.allTasks}
        devDescription={hints.tasks.devAllTasks}
      >
        <DataTable columns={columns} rows={tasks} />
        <span className="inline-flex items-center gap-1 mt-3">
          <button
            type="button"
            className={btnSecondary}
            onClick={() => refresh()}
            title={hints.common.refreshList}
          >
            Refresh list
          </button>
          <HelpHint text={hints.common.refreshList} />
        </span>
      </PageSection>

      <PageSection
        title="Paginated tasks"
        description={`${hints.tasks.paginated} ${hints.common.advancedSection}`}
        devDescription={hints.tasks.devPaginated}
      >
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="pageNo" hint="Page number (starts at 0).">
            <input className={inputClass} value={pageNo} onChange={(e) => setPageNo(e.target.value)} />
          </Field>
          <Field label="pageSize" hint="How many tasks per page.">
            <input className={inputClass} value={pageSize} onChange={(e) => setPageSize(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            title={hints.common.runPaginated}
            onClick={() =>
              paginatedAction.run(() =>
                apiGet<TaskResponse[]>(
                  `/api/task/admin/allTaskListWithPagination?pageNo=${pageNo}&pageSize=${pageSize}`
                )
              )
            }
          >
            Run
          </button>
        </div>
        <ResultPanel {...paginatedAction} />
      </PageSection>

      <PageSection
        title="Create task"
        description={hints.tasks.create}
        devDescription={hints.tasks.devCreate}
      >
        <div className="grid gap-4 sm:grid-cols-2 max-w-2xl">
          <Field label="title (max 15)" hint={hints.tasks.title}>
            <input className={inputClass} value={crtTitle} onChange={(e) => setCrtTitle(e.target.value)} />
          </Field>
          <Field label="description (max 40)" hint={hints.tasks.description}>
            <input className={inputClass} value={crtDesc} onChange={(e) => setCrtDesc(e.target.value)} />
          </Field>
          <Field label="status (optional)" hint={hints.tasks.status} help={hints.tasks.statusLegend}>
            <select className={inputClass} value={crtStatus} onChange={(e) => setCrtStatus(e.target.value)}>
              {STATUSES.map((s) => (
                <option key={s} value={s}>
                  {s || "— default PENDING —"}
                </option>
              ))}
            </select>
          </Field>
        </div>
        <span className="inline-flex items-center gap-1 mt-4">
          <button
            type="button"
            className={btnPrimary}
            title={hints.tasks.afterCreateTaskHelp}
            onClick={() => {
              const body: CreateTaskRequest = {
                title: crtTitle,
                description: crtDesc,
                status: crtStatus || null,
              };
              createAction.run(() => apiPost("/api/task/admin/createTask", body)).then(() => refresh());
            }}
          >
            {hints.tasks.createTask}
          </button>
          <HelpHint text={hints.tasks.afterCreateTaskHelp} />
        </span>
        <p className="mt-3 text-xs text-slate-400 max-w-2xl">
          {hints.tasks.afterCreateTask}{" "}
          <a href="#assign-user-to-task" className="text-indigo-300 hover:text-indigo-200 underline">
            Assign below
          </a>
          {" · "}
          <Link to="/users" className="text-indigo-300 hover:text-indigo-200 underline">
            Find User ID
          </Link>
        </p>
        <ResultPanel {...createAction} />
      </PageSection>

      <PageSection
        title="Update task"
        description={hints.tasks.update}
        devDescription={hints.tasks.devUpdate}
      >
        <div className="grid gap-4 sm:grid-cols-2 max-w-2xl">
          <Field label="id" hint={hints.tasks.taskId} help={hints.common.idFromTable}>
            <input className={inputClass} value={updId} onChange={(e) => setUpdId(e.target.value)} />
          </Field>
          <Field label="title" hint={hints.tasks.title}>
            <input className={inputClass} value={updTitle} onChange={(e) => setUpdTitle(e.target.value)} />
          </Field>
          <Field label="description" hint={hints.tasks.description}>
            <input className={inputClass} value={updDesc} onChange={(e) => setUpdDesc(e.target.value)} />
          </Field>
          <Field label="status" hint={hints.tasks.statusOmit} help={hints.tasks.statusLegend}>
            <select className={inputClass} value={updStatus} onChange={(e) => setUpdStatus(e.target.value)}>
              <option value="">— omit —</option>
              {STATUSES.filter(Boolean).map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
          </Field>
        </div>
        <button
          type="button"
          className={`${btnPrimary} mt-4`}
          onClick={() => {
            const body: UpdateTaskRequest = { id: Number(updId) };
            if (updTitle) body.title = updTitle;
            if (updDesc) body.description = updDesc;
            if (updStatus) body.status = updStatus;
            updateAction.run(() => apiPut("/api/task/admin/updateTask", body)).then(() => refresh());
          }}
        >
          {hints.tasks.saveChanges}
        </button>
        <ResultPanel {...updateAction} />
      </PageSection>

      <PageSection
        title="Delete task"
        description={hints.tasks.delete}
        devDescription={hints.tasks.devDelete}
      >
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="id" hint={hints.tasks.taskId} help={hints.common.idFromTable}>
            <input className={inputClass} value={delId} onChange={(e) => setDelId(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            onClick={() => {
              if (!confirm(`Delete task ${delId}?`)) return;
              deleteAction.run(() => apiDelete(`/api/task/admin/deleteTask/${delId}`)).then(() => refresh());
            }}
          >
            {hints.tasks.deleteTask}
          </button>
        </div>
        <ResultPanel {...deleteAction} />
      </PageSection>

      <PageSection
        id="assign-user-to-task"
        title="Assign user to task"
        description={hints.tasks.assign}
        devDescription={hints.tasks.devAssign}
      >
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="taskId" hint={hints.tasks.taskId} help={hints.common.idFromTable}>
            <input className={inputClass} value={assignTaskId} onChange={(e) => setAssignTaskId(e.target.value)} />
          </Field>
          <Field label="userId" hint={hints.tasks.userId}>
            <input className={inputClass} value={assignUserId} onChange={(e) => setAssignUserId(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            onClick={() =>
              assignAction
                .run(() =>
                  apiPut(buildAssignUserPath(Number(assignTaskId), Number(assignUserId)))
                )
                .then(() => refresh())
            }
          >
            {hints.tasks.assignToPerson}
          </button>
        </div>
        <ResultPanel {...assignAction} />
      </PageSection>

      <PageSection
        title="Remove user from task"
        description={hints.tasks.unassign}
        devDescription={hints.tasks.devUnassign}
      >
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="taskId" hint={hints.tasks.taskId} help={hints.common.idFromTable}>
            <input className={inputClass} value={unassignTaskId} onChange={(e) => setUnassignTaskId(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            onClick={() =>
              unassignAction
                .run(() => apiPut(`/api/task/admin/removeUserFromTask/${unassignTaskId}`))
                .then(() => refresh())
            }
          >
            {hints.tasks.removeAssignee}
          </button>
        </div>
        <ResultPanel {...unassignAction} />
      </PageSection>
    </div>
  );
}
