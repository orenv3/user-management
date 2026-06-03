export type TaskTableResponse = {
  task_id: number;
  task_title: string;
  task_description: string;
  task_status: string;
  task_assignee: number;
  err: string;
};

export type TaskResponse = {
  id: number;
  title: string;
  description: string;
  status: string;
  assigneeId: number | null;
};

