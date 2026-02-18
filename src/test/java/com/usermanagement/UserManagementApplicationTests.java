package com.usermanagement;

import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.security.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Integration test to verify that the Spring application context loads successfully.
 * Uses profile "test" so CommandLineRunner is not loaded.
 * Mocks AuthenticationService and EntityMapper to avoid requiring full security and MapStruct setup in test.
 */
@SpringBootTest
class UserManagementApplicationTests {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private EntityMapper entityMapper;

    @Test
    void contextLoads() {
        // Verifies the application context can be loaded without configuration errors.
    }
}

