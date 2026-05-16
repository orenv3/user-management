### This is a Spring Boot application that exposes a REST API.
### The propose of this Application is to manage users, user's tasks and user's.comments.

##### The API should allow clients to perform the following actions:

<!-- The following is draft - not sure

Notification
* chech/fix compexity(input/output as interfaces. cause of the array - may get other input types)
* Dockerized them
* cloud it
* micro them
*  Redis  save players
        BUT NOT SURE |==> send via MQ all the specific Objs for Statistic AND Notifications AND save in regular SQL if needed

player app:
Test the app with this configuration:
* https://docs.spring.io/spring-data/redis/reference/redis.html

The above is draft - not sure -->

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
6. I used an postgresql as a database and in-memory (H2) for tests.
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

### Automated tests

Tests run with Maven (`mvn test`). The `test` Spring profile disables the startup `CommandLineRunner` seed and uses an in-memory H2 database (see `src/test/resources/application.properties`). Coverage is split into **repository** slices (`@DataJpaTest`), **unit** tests (Mockito), and **full-stack** checks (`@SpringBootTest` + `MockMvc`).

| README rule / feature | What the test asserts | Test location (class) |
|------------------------|------------------------|------------------------|
| Login and bearer token | `POST /api/auth/login` returns a JWT; protected routes return 401 without a token | `ApplicationApiIntegrationTest` |
| Inactive / deactivated user | User with `active=false` cannot authenticate | `ApplicationApiIntegrationTest` |
| Admin vs regular user (Spring Security) | Regular user gets 403 on admin-only task create | `ApplicationApiIntegrationTest` |
| User sees only own tasks | `GET .../user/allTaskList/{id}` returns only tasks assigned to that user | `ApplicationApiIntegrationTest` |
| Archived task not visible | After admin sets task status to `ARCHIVED`, it disappears from the user’s task list | `ApplicationApiIntegrationTest` |
| All comments on a user’s tasks | Assignee’s comment list includes an admin comment on the same task | `ApplicationApiIntegrationTest` |
| User may comment only on own assigned tasks | Comment on another user’s task returns an error payload (service rule) | `ApplicationApiIntegrationTest` |
| Same task title forbidden | Duplicate create throws / API returns error body | `TaskServiceTest`, `ApplicationApiIntegrationTest` |
| Same email forbidden (registration) | `registerUser` throws when email already exists | `AuthenticationServiceTest` |
| Task status on create: default `PENDING` | `CreateTaskRequest` normalizes null/blank status; API create with null status is `PENDING` | `CreateTaskRequestTest`, `ApplicationApiIntegrationTest` |
| Task status on update: blank does nothing | `UpdateTaskRequest.updateTaskParameters` leaves status; API update without status keeps `PENDING` | `UpdateTaskRequestTest`, `ApplicationApiIntegrationTest` |
| Input validation (`@Valid`) | Invalid create-user / create-task bodies return 400 | `ApplicationApiIntegrationTest` |
| Admin activate/deactivate | After admin sets user `active=false`, that user cannot log in | `ApplicationApiIntegrationTest` |
| Task repository queries | Assignee listing, archive filter, mark complete | `TaskRepoTest` |
| Comment repository query | Comments by task titles | `CommentRepoTest` |
| User repository | Find by email | `UserRepoTest` |
| User service (mocked) | Update, list, delete, find flows | `UserServiceTest` |
| Application context | Spring Boot context loads under `test` profile | `UserManagementApplicationTests` |

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