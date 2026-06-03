import { useEffect, useState } from "react";
import { apiGet, apiPost } from "../api/client";
import type { CommentsResponse, UserTaskCommentRequest } from "../api/types";
import { useAuth } from "../auth/AuthContext";
import DataTable from "../components/DataTable";
import { Field, btnPrimary, inputClass } from "../components/Field";
import PageSection from "../components/PageSection";
import ResultPanel from "../components/ResultPanel";
import { useAction } from "../hooks/useAction";

export default function MyCommentsPage() {
  const { me } = useAuth();
  const [userId, setUserId] = useState("");
  const commentAction = useAction();
  const listAction = useAction();
  const nativeAction = useAction();

  const [comment, setComment] = useState("");
  const [taskId, setTaskId] = useState("");
  const [commentUserId, setCommentUserId] = useState("");

  useEffect(() => {
    if (me) {
      setUserId(String(me.userId));
      setCommentUserId(String(me.userId));
    }
  }, [me]);

  const columns = [
    {
      key: "ts",
      header: "Timestamp",
      render: (c: CommentsResponse) => (c.timestamp ? String(c.timestamp) : "—"),
    },
    { key: "comment", header: "Comment", render: (c: CommentsResponse) => c.comment },
    { key: "userId", header: "User", render: (c: CommentsResponse) => c.userId },
    { key: "taskId", header: "Task", render: (c: CommentsResponse) => c.taskId },
    { key: "title", header: "Task title", render: (c: CommentsResponse) => c.title },
    { key: "err", header: "Err", render: (c: CommentsResponse) => c.err || "—" },
  ];

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-semibold">My comments</h1>

      <PageSection title="Comment on my task" description="POST /api/comment/user/commentMyTask">
        <div className="grid gap-4 max-w-xl">
          <Field label="comment (max 120)">
            <textarea
              className={inputClass}
              rows={3}
              value={comment}
              onChange={(e) => setComment(e.target.value)}
            />
          </Field>
          <Field label="taskId">
            <input className={inputClass} value={taskId} onChange={(e) => setTaskId(e.target.value)} />
          </Field>
          <Field label="userId">
            <input className={inputClass} value={commentUserId} onChange={(e) => setCommentUserId(e.target.value)} />
          </Field>
        </div>
        <button
          type="button"
          className={`${btnPrimary} mt-4`}
          onClick={() => {
            const body: UserTaskCommentRequest = {
              comment,
              taskId: Number(taskId),
              userId: Number(commentUserId),
            };
            commentAction.run(() => apiPost("/api/comment/user/commentMyTask", body));
          }}
        >
          Run
        </button>
        <ResultPanel {...commentAction} />
      </PageSection>

      <PageSection title="My comments (JPQL)" description="GET .../userCommentList/{userId}">
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="userId">
            <input className={inputClass} value={userId} onChange={(e) => setUserId(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            onClick={() =>
              listAction.run(() =>
                apiGet<CommentsResponse[]>(`/api/comment/user/userCommentList/${userId}`)
              )
            }
          >
            Run
          </button>
        </div>
        {listAction.result !== undefined && (
          <div className="mt-4">
            <DataTable columns={columns} rows={(listAction.result as CommentsResponse[]) ?? []} />
          </div>
        )}
        <ResultPanel loading={listAction.loading} error={listAction.error} result={listAction.result} />
      </PageSection>

      <PageSection
        title="My comments (native query)"
        description="GET .../userCommentListViaNativeQuery/{userId}"
      >
        <div className="flex flex-wrap gap-4 items-end">
          <Field label="userId">
            <input className={inputClass} value={userId} onChange={(e) => setUserId(e.target.value)} />
          </Field>
          <button
            type="button"
            className={btnPrimary}
            onClick={() =>
              nativeAction.run(() =>
                apiGet<CommentsResponse[]>(
                  `/api/comment/user/userCommentListViaNativeQuery/${userId}`
                )
              )
            }
          >
            Run
          </button>
        </div>
        {nativeAction.result !== undefined && (
          <div className="mt-4">
            <DataTable columns={columns} rows={(nativeAction.result as CommentsResponse[]) ?? []} />
          </div>
        )}
        <ResultPanel loading={nativeAction.loading} error={nativeAction.error} result={nativeAction.result} />
      </PageSection>
    </div>
  );
}
