package com.usermanagement.dao.services;

import com.usermanagement.entities.User;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.requestObjects.UpdateUserRequest;
import com.usermanagement.responseObjects.UserResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;
    private AutoCloseable autoCloseable;
    private UserService userServiceUnderTest;


    @BeforeEach
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userServiceUnderTest = new UserService(userRepo);

        when(userRepo.findAll()).thenReturn(this.getUserList());
        when(userRepo.findByEmail(any())).thenReturn(Optional.ofNullable(this.getUserPrivilge()));
        when(userRepo.getReferenceById(3L)).thenReturn(this.getUserPrivilge());
        when(userRepo.existsById(3L)).thenReturn(false);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void updateUser() {
        // Given
        long userId = 3L;
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                userId,
                "updatedName",
                "updated@email.com",
                false,
                true,
                null
        );
        User existingUser = getUserPrivilge();
        User updatedUser = new User(new CreateUserRequest(
                updateRequest.name(),
                updateRequest.email(),
                updateRequest.isAdmin(),
                updateRequest.active(),
                "password"
        ));
        updatedUser.setId(userId);

        when(userRepo.getReferenceById(userId)).thenReturn(existingUser);
        when(userRepo.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserResponse result = userServiceUnderTest.updateUser(updateRequest);

        // Then
        verify(userRepo).getReferenceById(userId);
        verify(userRepo).save(any(User.class));
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo(updateRequest.name());
        assertThat(result.email()).isEqualTo(updateRequest.email());
    }

    @Test
    void getAllUserList() {
        // When
        List<UserResponse> usersListUnderTest = userServiceUnderTest.getAllUserList();

        // Then verify
        verify(userRepo).findAll();
        assertThat(usersListUnderTest).isNotNull();
        assertThat(usersListUnderTest.size()).isEqualTo(5);

        // Verify that user's email is unique
        Set<String> checkEmailDuplication = new HashSet<>();
        Optional<UserResponse> isDuplicate = usersListUnderTest.stream()
                .filter(usr -> !(checkEmailDuplication.add(usr.email())))
                .findFirst();

        assertThat(isDuplicate).isEmpty();
    }

    @Test
    void getUserById() {
        // Given
        long userId = 3L;

        // When
        User userUnderTest = userServiceUnderTest.getUserById(userId);

        // Then
        verify(userRepo).getReferenceById(userId);
        assertThat(userUnderTest.getId()).isEqualTo(userId);
        assertThat(userUnderTest.getEmail()).isEqualTo(getUserPrivilge().getEmail());
    }

    @Test
    void findUserByEmail() {
        // Given
        long userId = 3L;
        User userUnderTest = userServiceUnderTest.getUserById(userId);
        String emailUnderTest = userUnderTest.getEmail();

        // When
        Optional<User> foundUser = userServiceUnderTest.findUserByEmail(emailUnderTest);

        // Then
        verify(userRepo).getReferenceById(userId);
        verify(userRepo).findByEmail(emailUnderTest);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(userId);
        assertThat(foundUser.get().getEmail()).isEqualTo(emailUnderTest);
    }

    @Test
    void deleteUser() {
        // Given
        long userIdToDelete = 3L;
        User userToDelete = getUserPrivilge();
        when(userRepo.getReferenceById(userIdToDelete)).thenReturn(userToDelete);
        when(userRepo.existsById(userIdToDelete)).thenReturn(false);

        // When
        String result = userServiceUnderTest.deleteUser(userIdToDelete);

        // Then
        verify(userRepo).getReferenceById(userIdToDelete);
        verify(userRepo).deleteById(userIdToDelete);
        verify(userRepo).existsById(userIdToDelete);
        assertThat(result).isEqualTo("Deleted: true");
    }





    /**
     * Helper methods to create test data.
     */




    private List<User> getUserList() {
        CreateUserRequest userRequest1 = new CreateUserRequest(
                "oren", "oren@email1",
                true, true,
                "pass");
        User user1 = new User(userRequest1);
        user1.setId(1L);

        CreateUserRequest userRequest2 = new CreateUserRequest(
                "avivit", "avivit@email1",
                true, true,
                "pass");
        User user2 = new User(userRequest2);
        user2.setId(2L);

        CreateUserRequest userRequest3 = new CreateUserRequest(
                "maya", "maya@email1",
                false, true,
                "pass");
        User user3 = new User(userRequest3);
        user3.setId(3L);

        CreateUserRequest userRequest4 = new CreateUserRequest(
                "Daniel", "Daniel@email1",
                false, true,
                "pass");
        User user4 = new User(userRequest4);
        user4.setId(4L);

        CreateUserRequest userRequest5 = new CreateUserRequest(
                "raz", "raz@email1",
                false, true,
                "pass");
        User user5 = new User(userRequest5);
        user5.setId(5L);

        return List.of(user1, user2, user3, user4, user5);
    }

    private User getUserPrivilge() {
        CreateUserRequest userRequest3 = new CreateUserRequest(
                "maya", "maya@email1",
                false, true,
                "pass");

        User user = new User(userRequest3);
        user.setId(3L);
        return user;
    }
}