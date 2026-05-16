package com.usermanagement;


import com.usermanagement.dao.services.TaskService;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.security.AuthResponse;
import com.usermanagement.security.AuthenticationService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class UserManagementApplication {
	private static final Logger log = LoggerFactory.getLogger(UserManagementApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UserManagementApplication.class, args);
	}


	@Bean
	@Profile("!test")
	CommandLineRunner commandLineRunner(AuthenticationService service, TaskService srv){
//IMPORTANT: remove the System.out.println and use the log.info
//IMPORTANT: Admin token should send to my email
//TODO: Admin user should be created only once and should be used for testing
		return args -> {

				CreateUserRequest createAdmin =
						new CreateUserRequest("oren", "orenv@vinogura",
								true, true, "1234");
								AuthResponse adminResponse = service.registerUser(createAdmin);
								log.info("createAdmin1: " + adminResponse);
				System.out.println("createAdmin1: " + adminResponse);

				CreateUserRequest createAdminForTest =
				new CreateUserRequest("admin", "admin@vinogura",
						true, true, "1234");
						AuthResponse adminResponseForTest = service.registerUser(createAdminForTest);
						log.info("createAdmin2: " + adminResponseForTest);
		System.out.println("createAdmin2: " + adminResponseForTest);

				CreateUserRequest createUser1 =
						new CreateUserRequest("user", "user",
								false, true, "pass");
								AuthResponse user1Response = service.registerUser(createUser1);
								log.info("createUser1: " + user1Response);
				System.out.println("createUser1: " + user1Response);

				CreateUserRequest createUser2 =
						new CreateUserRequest("user2", "user2",
								false, true, "pass");
								AuthResponse user2Response = service.registerUser(createUser2);
								log.info("createUser2: " + user2Response);
				System.out.println("createUser2: " + user2Response);

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


