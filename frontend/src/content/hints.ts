export const hints = {
  login: {
    demoTitle: "Try the live demo",
    demoIntro:
      "These are real accounts on this site — not placeholders. Click a role below to fill email and password, then press Login.",
    useManagerAccount: "Use manager account",
    useTeamMemberAccount: "Use team member account",
    managerAccountHelp:
      "Fills a real manager login. Press Login right after — password is included automatically.",
    teamMemberAccountHelp:
      "Fills a real team member login. Press Login right after — password is included automatically.",
    emailHint: "Your work email — or use a ready-made account above.",
    passwordHint: "Your account password — filled for you when you pick a demo role.",
    aboutProject: "Learn what this demo app does and how it was built.",
    swagger: "Technical documentation for developers — you can ignore this.",
  },

  nav: {
    overview: "Your home page — see your role and shortcuts to main areas.",
    users: "Manage team members: add people, change roles, and deactivate accounts.",
    tasks: "Create work items and assign them to team members.",
    comments: "Read and moderate discussions on tasks across the team.",
    analytics: "See how often people log in and use the app.",
    myTasks: "Tasks assigned to you — view details and mark them done.",
    myComments: "Leave notes on your tasks and read past comments.",
    logout: "Sign out securely. You'll need to log in again to continue.",
    swagger: "Developer API docs — not needed for day-to-day use.",
    menu: "Open the navigation menu.",
  },

  overview: {
    session: "Your account",
    userIdHelp: "Your unique number in the system — use it when assigning tasks or comments.",
    roleHelp: "Manager (ADMIN) can manage everyone; Team member (USER) sees only their own work.",
    quickLinks: "Shortcuts to the main areas of the app.",
  },

  common: {
    refreshList: "Reload to see the latest changes.",
    refreshSummary: "Update the numbers with the latest activity.",
    idFromTable: "The number in the ID column of the table above.",
    advancedSection: "Advanced — for large teams or developers.",
    runPaginated: "Load that page of results.",
  },

  users: {
    allUsers: "Everyone registered in the app. The table updates when you add or change people.",
    paginated: "Browse users in pages — useful for large teams.",
    register: "Add a new team member. They'll use the email and password you set to log in.",
    registerDemoIntro:
      "Pre-fill the form with a real demo account. You can edit the fields before creating.",
    update: "Change someone's name, email, role, or password.",
    delete: "Permanently remove a user. Cannot be undone. Protected demo accounts cannot be deleted.",
    name: "Display name (max 15 characters).",
    email: "Login email — must be unique.",
    password: "Initial password — share it securely with the new person.",
    isAdmin: "Managers can manage users and all tasks.",
    active: "Uncheck to block login without deleting the account.",
    updateId: "User ID from the table — required.",
    omitField: "Leave blank to keep the current value.",
    createAccount: "Create account",
    afterCreateAccount:
      "After creating someone, open Tasks, create or pick a task, then assign it using their User ID from the table above. It will appear in their My tasks when they log in.",
    afterCreateAccountHelp:
      "New team members only see work once a task is assigned to them. Use the User ID from the All users table on the Assign user to task section.",
    saveChanges: "Save changes",
    deleteUser: "Delete user",
    devAllUsers: "GET /api/userTable/admin/allUserList",
    devPaginated: "GET /api/userTable/admin/allUserListWithPagination",
    devRegister: "POST /api/auth/admin/registerUser",
    devUpdate: "PUT /api/userTable/admin/updateUser — protected accounts cannot be edited",
    devDelete: "DELETE /api/userTable/admin/deleteUser/{id} — protected accounts cannot be deleted",
  },

  tasks: {
    allTasks: "Every task in the system, who it's assigned to, and its status.",
    paginated: "Browse tasks in pages — useful for large lists.",
    create: "Add new work for the team. Assign it to someone in the section below.",
    update: "Edit title, description, or status of an existing task.",
    delete: "Remove a task permanently. You'll be asked to confirm.",
    assign: "Give a task to a team member so it appears in My tasks for them.",
    unassign: "Unassign someone — the task stays but no one owns it until reassigned.",
    title: "Short name for the task (max 15 characters).",
    description: "What needs to be done (max 40 characters).",
    status: "Usually leave as Pending until someone finishes it.",
    statusLegend: "Pending = not started; Completed = finished; Archived = closed.",
    statusOmit: "Leave blank if you're not changing status.",
    taskId: "Task number from the tasks table.",
    userId: "Person's user ID from the Users page.",
    createTask: "Create task",
    afterCreateTask:
      "After creating a task, assign it to a team member below using the task's ID from the table above and their User ID from the Users page. They'll see it under My tasks when they log in.",
    afterCreateTaskHelp:
      "A new task isn't visible to anyone until you assign it. Use the Task ID from All tasks and the person's User ID from Users.",
    saveChanges: "Save changes",
    deleteTask: "Delete task",
    assignToPerson: "Assign to person",
    removeAssignee: "Remove assignee",
    devAllTasks: "GET /api/task/admin/allTaskList",
    devPaginated: "GET /api/task/admin/allTaskListWithPagination",
    devCreate: "POST /api/task/admin/createTask",
    devUpdate: "PUT /api/task/admin/updateTask",
    devDelete: "DELETE /api/task/admin/deleteTask/{id}",
    devAssign: "PUT /api/task/admin/assignUser{taskId}/{userId}",
    devUnassign: "PUT /api/task/admin/removeUserFromTask/{taskId}",
  },

  myTasks: {
    assigned: "Tasks your manager assigned to you.",
    assignee: "Usually your own ID (filled automatically). Only change if testing.",
    complete: "Tell your manager this task is done.",
    taskId: "The ID of the task you finished (from the table above).",
    loadTasks: "Load tasks",
    markComplete: "Mark complete",
    devAssigned: "GET /api/task/user/allTaskList/{assignee}",
    devComplete: "PUT /api/task/user/updateComplete?taskId=",
  },

  comments: {
    allComments: "Every comment on every task, across the team.",
    create: "Add a note on behalf of the team (e.g. manager reply).",
    update: "Edit the text of an existing comment.",
    commentText: "Your message (max 120 characters).",
    taskId: "Which task this comment belongs to.",
    commentId: "Comment ID from the table.",
    postComment: "Post comment",
    saveComment: "Save comment",
    devAll: "GET /api/comment/admin/allCommentList",
    devCreate: "POST /api/comment/admin/createComment",
    devUpdate: "PUT /api/comment/admin/updateComment",
  },

  myComments: {
    post: "Add a note or question on a task assigned to you.",
    commentText: "What you want to say (max 120 characters).",
    taskId: "Task you're commenting on — must be one assigned to you.",
    userId: "Your user ID (usually pre-filled).",
    listJpql: "View your comment history on tasks.",
    postComment: "Post comment",
    loadComments: "Load comments",
    devPost: "POST /api/comment/user/commentMyTask",
    devListJpql: "GET /api/comment/user/userCommentList/{userId}",
  },

  analytics: {
    summary: "Quick stats: how much the app is used.",
    eventLog: "Detailed list of who did what and when.",
    pageViews: "How many screens were opened.",
    uniqueSessions: "How many separate visits.",
    logins: "Total sign-ins.",
    apiActions: "Buttons clicked and data saved (tasks, users, etc.).",
    eventType: "Filter: page views, logins, or user actions.",
    loadEvents: "Load events",
    devSummary: "GET /api/analytics/admin/summary",
    devEvents: "GET /api/analytics/admin/events",
  },
} as const;

export const adminNavLinks = [
  { to: "/", label: "Overview", hint: hints.nav.overview },
  { to: "/users", label: "Users", hint: hints.nav.users },
  { to: "/tasks", label: "Tasks", hint: hints.nav.tasks },
  { to: "/comments", label: "Comments", hint: hints.nav.comments },
  { to: "/analytics", label: "Analytics", hint: hints.nav.analytics },
] as const;

export const userNavLinks = [
  { to: "/", label: "Overview", hint: hints.nav.overview },
  { to: "/my-tasks", label: "My tasks", hint: hints.nav.myTasks },
  { to: "/my-comments", label: "My comments", hint: hints.nav.myComments },
] as const;
