### This is a Spring Boot application that exposes a REST API.
### The propose of this Application is to manage users, user's tasks and user's.comments.

##### The API should allow clients to perform the following actions:

The user must to login and retrieve an oauth2 bearer token, 
in order to use the below actions!
  * Retrieve the list of tasks
  * Add a new task to the list
  * Update the details of a task
  * Mark a task as completed
  * Remove a task from the list
  * Retrieve the list of users
  * Add a new user to the list
  * Update the details of a user
  * Remove a user from the list
  * Add comment to task

Description (more details):
1. In order to use the application, the user must to login and retrieve an oauth2 bearer token.
2. A user can only fetch and see his tasks.
2. When a user see the task, he may see **all** comments 
(other users comments - even if he did not create them).
3. Regular user can:
   1. View his tasks and their comments,
   2. Create a comment on his tasks,
   3. Mark a comment as completed.
4. Admin can:
   1. Create users,
   2. Activate/deactivate user,
   3. Create tasks,
   4. Assign/re-assign tasks,
   5. Comment on tasks
   6. Mark completed tasks as archived
5. An archived task is not visible to the user.
6. I used an in-memory database (H2) to store the tasks and users.
7. Password hashing and authentication/authorization for the user API using Spring Security

>#### Task table:
> * ID (generated automatically)
> * Title
> * Description
> * Status with fixed possibilities(pending/completed/archived)
> * Assignee (user to whom the task is assigned)

>#### Comment table:
> * ID (generated automatically)
> * UserId (foreign key)
> * TaskId (foreign key)
> * TimeStamp (Date)
> * Comment (Text)

> #### User table:
> * ID (generated automatically)
> * Name
> * Email
> * IsAdmin (true,false)
> * Active (true,false)
> * Password (hashed)



### This application rules and features:
* ErrorHandling - @ControllerAdvice enabled.
* Input validation on CRUD queries.
* Create task with the same title is forbidden
* Creating user with the same email is forbidden 
* Task status in creation - if(status == null || status.isBlank()) is PENDING by default
* Task status update - if(status == null || status.isBlank()) do nothing
* Login and registration (authentication)- first need to register user. You will get register's token in the response in order to set it and work with it.
* Swagger for API tests:
  * localhost:8080/swagger-ui.html: swagger works with bearer token 
  * set the given token in the swagger

### Run this application
In order to run this application you should run it on CLI/CMD - follow the command below:
`java -jar user-management-0.1.jar`.

#### ** You have to set Auth token of **admin user** to work with this app **

** THE FIRST ADMIN user will always be:
* user: orenv@vinogura
* password: 1234

you must search the word: "createAdmin: AuthResponse" in the console logs:
  ![log](./logs.png)

* The token of the admin user: "orenv@vinogura" will be in the console(like in the printScreen above):
  * looks like: "createAdmin: AuthResponse(token=eyJhbGciOiJIUzI1NiJ9..... " 
  * search the admin details: in the console search for the words -  "createAdmin:", "AuthResponse","createAdmin: AuthResponse" etc.