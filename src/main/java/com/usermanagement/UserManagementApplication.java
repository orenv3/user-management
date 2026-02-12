package com.usermanagement;


import com.usermanagement.dao.services.TaskService;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.security.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManagementApplication.class, args);
	}


	@Bean
	CommandLineRunner commandLineRunner(AuthenticationService service, TaskService srv){
		return args -> {

				CreateUserRequest createAdmin =
						new CreateUserRequest("oren", "orenv@vinogura",
								true, true, "1234");
				System.out.println("createAdmin: " + service.registerUser(createAdmin));

				CreateUserRequest createUser1 =
						new CreateUserRequest("user", "user",
								false, true, "pass");
				System.out.println("createUser1: " + service.registerUser(createUser1));

				CreateUserRequest createUser2 =
						new CreateUserRequest("user2", "user2",
								false, true, "pass");
				System.out.println("createUser2: " + service.registerUser(createUser2));

			CreateTaskRequest task1 = new CreateTaskRequest("task1","task1",null);
			CreateTaskRequest task2 = new CreateTaskRequest("task2","task2",null);
			CreateTaskRequest task3 = new CreateTaskRequest("task3","task3",null);
			CreateTaskRequest task4 = new CreateTaskRequest("task4","task4",null);
			System.out.println(srv.createTask(task1));
			System.out.println(srv.createTask(task2));
			System.out.println(srv.createTask(task3));
			System.out.println(srv.createTask(task4));
			srv.assignUserToTask(1,2);
			srv.assignUserToTask(2,2);
			srv.assignUserToTask(3,3);
			srv.assignUserToTask(4,3);



		};
	}

}


