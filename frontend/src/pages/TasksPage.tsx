import { useEffect, useState } from "react";
import { apiDelete, apiGet, apiPost, apiPut } from "../api/client";
import { buildAssignUserPath } from "../api/paths";
import type { CreateTaskRequest, TaskResponse, UpdateTaskRequest } from "../api/types";
import DataTable from "../components/DataTable";
import { Field, btnPrimary, btnSecondary, inputClass } from "../components/Field";
import PageSection from "../components/PageSection";
import ResultPanel from "../components/ResultPanel";
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

      <PageSection title="All tasks" description="GET /api/task/admin/allTaskList">
        <DataTable columns={columns} rows={tasks} />
        <button type="button" className={`${btnSecondary} mt-3`} onClick={() => refresh()}>
          Refresh list
        </button>
      </PageSection>

      <PageSection title="Paginated tasks" description="GET .../allTaskListWithPagination">
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="pageNo">
            <input className={inputClass} value={pageNo} onChange={(e) => setPageNo(e.target.value)} />
          </Field>
          <Field label="pageSize">
            <input className={inputClass} value={pageSize} onChange={(e) => setPageSize(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
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

      <PageSection title="Create task" description="POST .../createTask">
        <div className="grid gap-4 sm:grid-cols-2 max-w-2xl">
          <Field label="title (max 15)">
            <input className={inputClass} value={crtTitle} onChange={(e) => setCrtTitle(e.target.value)} />
          </Field>
          <Field label="description (max 40)">
            <input className={inputClass} value={crtDesc} onChange={(e) => setCrtDesc(e.target.value)} />
          </Field>
          <Field label="status (optional)">
            <select className={inputClass} value={crtStatus} onChange={(e) => setCrtStatus(e.target.value)}>
              {STATUSES.map((s) => (
                <option key={s} value={s}>
                  {s || "— default PENDING —"}
                </option>
              ))}
            </select>
          </Field>
        </div>
        <button
          type="button"
          className={`${btnPrimary} mt-4`}
          onClick={() => {
            const body: CreateTaskRequest = {
              title: crtTitle,
              description: crtDesc,
              status: crtStatus || null,
            };
            createAction.run(() => apiPost("/api/task/admin/createTask", body)).then(() => refresh());
          }}
        >
          Run
        </button>
        <ResultPanel {...createAction} />
      </PageSection>

      <PageSection title="Update task" description="PUT .../updateTask">
        <div className="grid gap-4 sm:grid-cols-2 max-w-2xl">
          <Field label="id">
            <input className={inputClass} value={updId} onChange={(e) => setUpdId(e.target.value)} />
          </Field>
          <Field label="title">
            <input className={inputClass} value={updTitle} onChange={(e) => setUpdTitle(e.target.value)} />
          </Field>
          <Field label="description">
            <input className={inputClass} value={updDesc} onChange={(e) => setUpdDesc(e.target.value)} />
          </Field>
          <Field label="status">
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
          Run
        </button>
        <ResultPanel {...updateAction} />
      </PageSection>

      <PageSection title="Delete task" description="DELETE .../deleteTask/{id}">
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="id">
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
            Run
          </button>
        </div>
        <ResultPanel {...deleteAction} />
      </PageSection>

      <PageSection title="Assign user to task" description="PUT .../assignUser{taskId}/{userId}">
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="taskId">
            <input className={inputClass} value={assignTaskId} onChange={(e) => setAssignTaskId(e.target.value)} />
          </Field>
          <Field label="userId">
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
            Run
          </button>
        </div>
        <ResultPanel {...assignAction} />
      </PageSection>

      <PageSection title="Remove user from task" description="PUT .../removeUserFromTask/{taskId}">
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="taskId">
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
            Run
          </button>
        </div>
        <ResultPanel {...unassignAction} />
      </PageSection>
    </div>
  );
}
