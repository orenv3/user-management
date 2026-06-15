import { useEffect, useState } from "react";
import { apiGet, apiPut } from "../api/client";
import type { TaskTableResponse } from "../api/types";
import { useAuth } from "../auth/AuthContext";
import DataTable from "../components/DataTable";
import { Field, btnPrimary, inputClass } from "../components/Field";
import PageSection from "../components/PageSection";
import ResultPanel from "../components/ResultPanel";
import { hints } from "../content/hints";
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

      <PageSection
        title="Assigned tasks"
        description={hints.myTasks.assigned}
        devDescription={hints.myTasks.devAssigned}
      >
        <Field label="assignee (user id)" hint={hints.myTasks.assignee}>
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
          {hints.myTasks.loadTasks}
        </button>
        <div className="mt-4">
          <DataTable columns={columns} rows={tasks} />
        </div>
        <ResultPanel {...listAction} />
      </PageSection>

      <PageSection
        title="Mark task complete"
        description={hints.myTasks.complete}
        devDescription={hints.myTasks.devComplete}
      >
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="taskId" hint={hints.myTasks.taskId} help={hints.common.idFromTable}>
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
            {hints.myTasks.markComplete}
          </button>
        </div>
        <ResultPanel {...completeAction} />
      </PageSection>
    </div>
  );
}
