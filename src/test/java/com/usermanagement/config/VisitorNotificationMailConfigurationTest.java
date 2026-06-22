package com.usermanagement.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisitorNotificationMailConfigurationTest {

    @Test
    void normalizeAppPassword_stripsSpaces() {
        assertThat(VisitorNotificationMailConfiguration.normalizeAppPassword("abcd efgh ijkl mnop"))
                .isEqualTo("abcdefghijklmnop");
    }

    @Test
    void normalizeAppPassword_handlesNull() {
        assertThat(VisitorNotificationMailConfiguration.normalizeAppPassword(null)).isEmpty();
    }
}
