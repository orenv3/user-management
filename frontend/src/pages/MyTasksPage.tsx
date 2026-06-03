import { useEffect, useState } from "react";
import { apiGet, apiPut } from "../api/client";
import type { TaskTableResponse } from "../api/types";
import { useAuth } from "../auth/AuthContext";
import DataTable from "../components/DataTable";
import { Field, btnPrimary, inputClass } from "../components/Field";
import PageSection from "../components/PageSection";
import ResultPanel from "../components/ResultPanel";
import { useAction } from "../hooks/useAction";

export default function MyTasksPage() {
  const { me } = useAuth();
  const [assignee, setAssignee] = useState("");
  const [tasks, setTasks] = useState<TaskTableResponse[]>([]);
  const listAction = useAction();
  const completeAction = useAction();
  const [taskId, setTaskId] = useState("");

  useEffect(() => {
    if (me) setAssignee(String(me.userId));
  }, [me]);

  useEffect(() => {
    if (!assignee) return;
    apiGet<TaskTableResponse[]>(`/api/task/user/allTaskList/${assignee}`)
      .then(setTasks)
      .catch(() => {});
  }, [assignee]);

  const columns = [
    { key: "id", header: "ID", render: (t: TaskTableResponse) => t.task_id },
    { key: "title", header: "Title", render: (t: TaskTableResponse) => t.task_title },
    { key: "desc", header: "Description", render: (t: TaskTableResponse) => t.task_description },
    { key: "status", header: "Status", render: (t: TaskTableResponse) => t.task_status },
    { key: "err", header: "Err", render: (t: TaskTableResponse) => t.err || "—" },
  ];

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-semibold">My tasks</h1>

      <PageSection title="Assigned tasks" description="GET /api/task/user/allTaskList/{assignee}">
        <Field label="assignee (user id)">
          <input className={inputClass} value={assignee} onChange={(e) => setAssignee(e.target.value)} />
        </Field>
        <button
          type="button"
          className={`${btnPrimary} mt-3`}
          onClick={() =>
            listAction.run(() => apiGet<TaskTableResponse[]>(`/api/task/user/allTaskList/${assignee}`)).then(
              (data) => setTasks(data as TaskTableResponse[])
            )
          }
        >
          Run
        </button>
        <div className="mt-4">
          <DataTable columns={columns} rows={tasks} />
        </div>
        <ResultPanel {...listAction} />
      </PageSection>

      <PageSection title="Mark task complete" description="PUT /api/task/user/updateComplete?taskId=">
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="taskId">
            <input className={inputClass} value={taskId} onChange={(e) => setTaskId(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            onClick={() =>
              completeAction.run(() =>
                apiPut(`/api/task/user/updateComplete?taskId=${taskId}`)
              )
            }
          >
            Run
          </button>
        </div>
        <ResultPanel {...completeAction} />
      </PageSection>
    </div>
  );
}
