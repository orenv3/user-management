export type ApiErrorResponse = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: Record<string, string> | null;
};

export type AuthResponse = {
  token: string | null;
  email: string;
  role: "ADMIN" | "USER" | string;
  userId: number;
};

export type UserResponse = {
  id: number;
  name: string;
  email: string;
  isAdmin: boolean;
  active: boolean;
};

export type TaskResponse = {
  id: number;
  title: string;
  description: string;
  status: string;
  assigneeId: number | null;
};

export type TaskTableResponse = {
  task_id: number;
  task_title: string;
  task_description: string;
  task_status: string;
  task_assignee: number;
  err: string;
};

export type CommentResponse = {
  id: number;
  timestamp: string;
  comment: string;
  userId: number;
  taskId: number;
};

export type CommentsResponse = {
  timestamp: string;
  comment: string;
  userId: number;
  taskId: number;
  title: string;
  err: string;
};

export type CreateUserRequest = {
  name: string;
  email: string;
  isAdmin: boolean;
  active: boolean;
  password: string;
};

export type UpdateUserRequest = {
  id: number;
  name?: string;
  email?: string;
  isAdmin?: boolean;
  active?: boolean;
  password?: string;
};

export type CreateTaskRequest = {
  title: string;
  description: string;
  status?: string | null;
};

export type UpdateTaskRequest = {
  id: number;
  title?: string;
  description?: string;
  status?: string;
};

export type AdminCreateCommentRequest = {
  comment: string;
  taskId: number;
};

export type UpdateCommentRequest = {
  id: number;
  comment: string;
};

export type UserTaskCommentRequest = {
  comment: string;
  taskId: number;
  userId: number;
};
