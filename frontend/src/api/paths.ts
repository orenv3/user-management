export function buildAssignUserPath(taskId: number, userId: number): string {
  return `/api/task/admin/assignUser${taskId}/${userId}`;
}
