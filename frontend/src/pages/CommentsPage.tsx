import { useEffect, useState } from "react";
import { apiGet, apiPost, apiPut } from "../api/client";
import type {
  AdminCreateCommentRequest,
  CommentResponse,
  UpdateCommentRequest,
} from "../api/types";
import DataTable from "../components/DataTable";
import HelpHint from "../components/HelpHint";
import { Field, btnPrimary, btnSecondary, inputClass } from "../components/Field";
import PageSection from "../components/PageSection";
import ResultPanel from "../components/ResultPanel";
import { hints } from "../content/hints";
import { useAction } from "../hooks/useAction";

export default function CommentsPage() {
  const [comments, setComments] = useState<CommentResponse[]>([]);
  const createAction = useAction();
  const updateAction = useAction();

  const [newComment, setNewComment] = useState("");
  const [newTaskId, setNewTaskId] = useState("");

  const [updId, setUpdId] = useState("");
  const [updComment, setUpdComment] = useState("");

  const refresh = () =>
    apiGet<CommentResponse[]>("/api/comment/admin/allCommentList")
      .then(setComments)
      .catch(() => {});

  useEffect(() => {
    refresh();
  }, []);

  const columns = [
    { key: "id", header: "ID", render: (c: CommentResponse) => c.id },
    {
      key: "ts",
      header: "Timestamp",
      render: (c: CommentResponse) => (c.timestamp ? String(c.timestamp) : "—"),
    },
    { key: "comment", header: "Comment", render: (c: CommentResponse) => c.comment },
    { key: "userId", header: "User", render: (c: CommentResponse) => c.userId },
    { key: "taskId", header: "Task", render: (c: CommentResponse) => c.taskId },
  ];

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-semibold">Comments</h1>

      <PageSection
        title="All comments"
        description={hints.comments.allComments}
        devDescription={hints.comments.devAll}
      >
        <DataTable columns={columns} rows={comments} />
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
        title="Create comment"
        description={hints.comments.create}
        devDescription={hints.comments.devCreate}
      >
        <div className="grid gap-4 max-w-xl">
          <Field label="comment (max 120)" hint={hints.comments.commentText}>
            <textarea
              className={inputClass}
              rows={3}
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
            />
          </Field>
          <Field label="taskId" hint={hints.comments.taskId} help={hints.common.idFromTable}>
            <input className={inputClass} value={newTaskId} onChange={(e) => setNewTaskId(e.target.value)} />
          </Field>
        </div>
        <button
          type="button"
          className={`${btnPrimary} mt-4`}
          onClick={() => {
            const body: AdminCreateCommentRequest = {
              comment: newComment,
              taskId: Number(newTaskId),
            };
            createAction.run(() => apiPost("/api/comment/admin/createComment", body)).then(() => refresh());
          }}
        >
          {hints.comments.postComment}
        </button>
        <ResultPanel {...createAction} />
      </PageSection>

      <PageSection
        title="Update comment"
        description={hints.comments.update}
        devDescription={hints.comments.devUpdate}
      >
        <div className="grid gap-4 max-w-xl">
          <Field label="id" hint={hints.comments.commentId} help={hints.common.idFromTable}>
            <input className={inputClass} value={updId} onChange={(e) => setUpdId(e.target.value)} />
          </Field>
          <Field label="comment" hint={hints.comments.commentText}>
            <textarea
              className={inputClass}
              rows={3}
              value={updComment}
              onChange={(e) => setUpdComment(e.target.value)}
            />
          </Field>
        </div>
        <button
          type="button"
          className={`${btnPrimary} mt-4`}
          onClick={() => {
            const body: UpdateCommentRequest = { id: Number(updId), comment: updComment };
            updateAction.run(() => apiPut("/api/comment/admin/updateComment", body)).then(() => refresh());
          }}
        >
          {hints.comments.saveComment}
        </button>
        <ResultPanel {...updateAction} />
      </PageSection>
    </div>
  );
}
