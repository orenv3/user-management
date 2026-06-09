package com.usermanagement.dao.services;

import com.usermanagement.config.PrivateAdminPolicy;
import com.usermanagement.entities.Users;
import com.usermanagement.errorHandler.ProtectedUserException;
import com.usermanagement.mappers.EntityMapper;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;
    
    @Mock
    private EntityMapper entityMapper;

    private PrivateAdminPolicy privateAdminPolicy;
    private AutoCloseable autoCloseable;
    private UserService userServiceUnderTest;


    @BeforeEach
    public void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        privateAdminPolicy = new PrivateAdminPolicy("");
        userServiceUnderTest = new UserService(userRepo, entityMapper, privateAdminPolicy);

        when(userRepo.findAll()).thenReturn(this.getUserList());
        when(userRepo.findByEmail(any())).thenReturn(Optional.ofNullable(this.getUserPrivilge()));
        when(userRepo.getReferenceById(3L)).thenReturn(this.getUserPrivilge());
        when(userRepo.existsById(3L)).thenReturn(false);
        
        when(entityMapper.toUserResponse(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            return new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.isAdmin(),
                    user.getActive()
            );
        });
        
        when(entityMapper.toUserResponseList(anyList())).thenAnswer(invocation -> {
            List<Users> users = invocation.getArgument(0);
            return users.stream()
                    .map(user -> new UserResponse(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.isAdmin(),
                            user.getActive()
                    ))
                    .toList();
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void updateUser() {
        long userId = 3L;
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                userId,
                "updatedName",
                "updated@email.com",
                false,
                true,
                null
        );
        Users existingUser = getUserPrivilge();
        Users updatedUser = new Users(new CreateUserRequest(
                updateRequest.name(),
                updateRequest.email(),
                updateRequest.isAdmin(),
                updateRequest.active(),
                "password"
        ));
        updatedUser.setId(userId);

        when(userRepo.getReferenceById(userId)).thenReturn(existingUser);
        when(userRepo.save(any(Users.class))).thenReturn(updatedUser);
        
        UserResponse expectedResponse = new UserResponse(
                userId,
                updateRequest.name(),
                updateRequest.email(),
                updateRequest.isAdmin(),
                updateRequest.active()
        );
        when(entityMapper.toUserResponse(updatedUser)).thenReturn(expectedResponse);

        UserResponse result = userServiceUnderTest.updateUser(updateRequest);

        verify(userRepo).getReferenceById(userId);
        verify(userRepo).save(any(Users.class));
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.name()).isEqualTo(updateRequest.name());
        assertThat(result.email()).isEqualTo(updateRequest.email());
    }

    @Test
    void getAllUserList() {
        List<UserResponse> usersListUnderTest = userServiceUnderTest.getAllUserList();

        verify(userRepo).findAll();
        verify(entityMapper).toUserResponseList(anyList());
        assertThat(usersListUnderTest).isNotNull();
        assertThat(usersListUnderTest.size()).isEqualTo(5);

        Set<String> checkEmailDuplication = new HashSet<>();
        Optional<UserResponse> isDuplicate = usersListUnderTest.stream()
                .filter(usr -> !(checkEmailDuplication.add(usr.email())))
                .findFirst();

        assertThat(isDuplicate).isEmpty();
    }

    @Test
    void getAllUserList_excludesPrivateAdmin() {
        privateAdminPolicy = new PrivateAdminPolicy("oren@email1");
        userServiceUnderTest = new UserService(userRepo, entityMapper, privateAdminPolicy);

        List<UserResponse> usersListUnderTest = userServiceUnderTest.getAllUserList();

        assertThat(usersListUnderTest).hasSize(4);
        assertThat(usersListUnderTest.stream().map(UserResponse::email)).doesNotContain("oren@email1");
    }

    @Test
    void updateUser_rejectsPrivateAdmin() {
        privateAdminPolicy = new PrivateAdminPolicy("oren@email1");
        userServiceUnderTest = new UserService(userRepo, entityMapper, privateAdminPolicy);
        Users privateAdmin = getUserList().get(0);
        when(userRepo.getReferenceById(1L)).thenReturn(privateAdmin);

        UpdateUserRequest updateRequest = new UpdateUserRequest(1L, "new", null, null, null, null);

        assertThatThrownBy(() -> userServiceUnderTest.updateUser(updateRequest))
                .isInstanceOf(ProtectedUserException.class);
    }

    @Test
    void deleteUser_rejectsPrivateAdmin() {
        privateAdminPolicy = new PrivateAdminPolicy("oren@email1");
        userServiceUnderTest = new UserService(userRepo, entityMapper, privateAdminPolicy);
        Users privateAdmin = getUserList().get(0);
        when(userRepo.getReferenceById(1L)).thenReturn(privateAdmin);

        assertThatThrownBy(() -> userServiceUnderTest.deleteUser(1L))
                .isInstanceOf(ProtectedUserException.class);

        verify(userRepo, never()).deleteById(1L);
    }

    @Test
    void getUserById() {
        long userId = 3L;

        Users userUnderTest = userServiceUnderTest.getUserById(userId);

        verify(userRepo).getReferenceById(userId);
        assertThat(userUnderTest.getId()).isEqualTo(userId);
        assertThat(userUnderTest.getEmail()).isEqualTo(getUserPrivilge().getEmail());
    }

    @Test
    void findUserByEmail() {
        long userId = 3L;
        Users userUnderTest = userServiceUnderTest.getUserById(userId);
        String emailUnderTest = userUnderTest.getEmail();

        Optional<Users> foundUser = userServiceUnderTest.findUserByEmail(emailUnderTest);

        verify(userRepo).getReferenceById(userId);
        verify(userRepo).findByEmail(emailUnderTest);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(userId);
        assertThat(foundUser.get().getEmail()).isEqualTo(emailUnderTest);
    }

    @Test
    void deleteUser() {
        long userIdToDelete = 3L;
        when(userRepo.existsById(userIdToDelete)).thenReturn(false);
        when(userRepo.getReferenceById(userIdToDelete)).thenReturn(getUserPrivilge());

        String result = userServiceUnderTest.deleteUser(userIdToDelete);

        verify(userRepo).deleteById(userIdToDelete);
        verify(userRepo).existsById(userIdToDelete);
        assertThat(result).isEqualTo("Deleted: true");
    }

    private List<Users> getUserList() {
        CreateUserRequest userRequest1 = new CreateUserRequest(
                "oren", "oren@email1",
                true, true,
                "pass");
        Users user1 = new Users(userRequest1);
        user1.setId(1L);

        CreateUserRequest userRequest2 = new CreateUserRequest(
                "avivit", "avivit@email1",
                true, true,
                "pass");
        Users user2 = new Users(userRequest2);
        user2.setId(2L);

        CreateUserRequest userRequest3 = new CreateUserRequest(
                "maya", "maya@email1",
                false, true,
                "pass");
        Users user3 = new Users(userRequest3);
        user3.setId(3L);

        CreateUserRequest userRequest4 = new CreateUserRequest(
                "Daniel", "Daniel@email1",
                false, true,
                "pass");
        Users user4 = new Users(userRequest4);
        user4.setId(4L);

        CreateUserRequest userRequest5 = new CreateUserRequest(
                "raz", "raz@email1",
                false, true,
                "pass");
        Users user5 = new Users(userRequest5);
        user5.setId(5L);

        return List.of(user1, user2, user3, user4, user5);
    }

    private Users getUserPrivilge() {
        CreateUserRequest userRequest3 = new CreateUserRequest(
                "maya", "maya@email1",
                false, true,
                "pass");

        Users user = new Users(userRequest3);
        user.setId(3L);
        return user;
    }
}
