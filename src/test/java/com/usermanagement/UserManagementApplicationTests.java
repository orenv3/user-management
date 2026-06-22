package com.usermanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test to verify that the Spring application context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class UserManagementApplicationTests {

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context can be loaded
        // without any configuration errors.
    }
}

