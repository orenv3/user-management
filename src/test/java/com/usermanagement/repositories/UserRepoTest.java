package com.usermanagement.repositories;

import com.usermanagement.entities.Users;
import com.usermanagement.requestObjects.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserRepoTest {

    @Autowired
    private UserRepo userRepoUnderTest;


    @Test
    void findByEmail() {
        // Given
        CreateUserRequest userRequest = new CreateUserRequest(
                "oren", "email1",
                true, true,
                "pass");
        Users user = new Users(userRequest);

        // When
        user = userRepoUnderTest.save(user);
        Optional<Users> expected = userRepoUnderTest.findByEmail(user.getEmail());

        // Then
        assertTrue(expected.isPresent());
        assertEquals(user.getId(), expected.get().getId());
        assertEquals(user.getEmail(), expected.get().getEmail());
        assertEquals(user.getName(), expected.get().getName());
    }

    @Test
    void findByEmailNotExistUser() {
        // Given
        CreateUserRequest userRequest = new CreateUserRequest(
                "oren", "email1",
                true, true,
                "pass");
        Users user = new Users(userRequest);

        // When
        user = userRepoUnderTest.save(user);
        Optional<Users> exists = userRepoUnderTest.findByEmail(user.getEmail());
        Optional<Users> empty = userRepoUnderTest.findByEmail("noSuchEmail@.gogo.com");

        // Then
        assertTrue(exists.isPresent());
        assertEquals(user.getEmail(), exists.get().getEmail());
        assertTrue(empty.isEmpty());
    }






}