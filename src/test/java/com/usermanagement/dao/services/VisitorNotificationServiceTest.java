package com.usermanagement.dao.services;

import com.usermanagement.config.PrivateAdminPolicy;
import com.usermanagement.config.VisitorNotificationProperties;
import com.usermanagement.entities.VisitorNotification;
import com.usermanagement.repositories.VisitorNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitorNotificationServiceTest {

    @Mock
    private VisitorNotificationRepository visitorNotificationRepository;
    @Mock
    private VisitorNotificationMailSender mailSender;

    private VisitorNotificationService visitorNotificationService;

    @BeforeEach
    void setUp() {
        visitorNotificationService = new VisitorNotificationService(
                visitorNotificationRepository,
                mailSender,
                enabledProperties(),
                new PrivateAdminPolicy("private-admin@test.com")
        );
    }

    @Test
    void handleSuccessfulLogin_whenDisabled_doesNothing() {
        visitorNotificationService = new VisitorNotificationService(
                visitorNotificationRepository,
                mailSender,
                new VisitorNotificationProperties(false, "notify@test.com", "sender@test.com", List.of()),
                new PrivateAdminPolicy("private-admin@test.com")
        );

        visitorNotificationService.handleSuccessfulLogin("203.0.113.42", "user@example.com", "Mozilla/5.0");

        verify(visitorNotificationRepository, never()).save(any());
        verify(mailSender, never()).sendNotification(any(), any(), any(), any());
    }

    @Test
    void handleSuccessfulLogin_newIp_savesAndSendsEmail() {
        when(visitorNotificationRepository.existsByClientIp("203.0.113.42")).thenReturn(false);
        when(visitorNotificationRepository.save(any(VisitorNotification.class))).thenAnswer(inv -> {
            VisitorNotification notification = inv.getArgument(0);
            notification.setId(1L);
            return notification;
        });

        visitorNotificationService.handleSuccessfulLogin("203.0.113.42", "user@example.com", "Mozilla/5.0");

        ArgumentCaptor<VisitorNotification> captor = ArgumentCaptor.forClass(VisitorNotification.class);
        verify(visitorNotificationRepository).save(captor.capture());
        VisitorNotification saved = captor.getValue();
        assertThat(saved.getClientIp()).isEqualTo("203.0.113.42");
        assertThat(saved.getLoginEmail()).isEqualTo("user@example.com");
        assertThat(saved.getUserAgent()).isEqualTo("Mozilla/5.0");
        assertThat(saved.getFirstSeenAt()).isNotNull();
        assertThat(saved.getLastSeenAt()).isEqualTo(saved.getFirstSeenAt());

        verify(mailSender).sendNotification(
                org.mockito.ArgumentMatchers.eq("203.0.113.42"),
                org.mockito.ArgumentMatchers.eq("user@example.com"),
                org.mockito.ArgumentMatchers.eq("Mozilla/5.0"),
                any(Instant.class)
        );
    }

    @Test
    void handleSuccessfulLogin_duplicateIp_updatesLastSeenOnly() {
        VisitorNotification existing = new VisitorNotification();
        existing.setId(7L);
        existing.setClientIp("203.0.113.42");
        existing.setLoginEmail("user@example.com");
        existing.setFirstSeenAt(Instant.parse("2026-01-01T00:00:00Z"));
        existing.setLastSeenAt(Instant.parse("2026-01-01T00:00:00Z"));
        when(visitorNotificationRepository.existsByClientIp("203.0.113.42")).thenReturn(true);
        when(visitorNotificationRepository.findByClientIp("203.0.113.42")).thenReturn(Optional.of(existing));
        when(visitorNotificationRepository.save(existing)).thenReturn(existing);

        visitorNotificationService.handleSuccessfulLogin("203.0.113.42", "admin@example.com", "Mozilla/5.0");

        assertThat(existing.getLoginEmail()).isEqualTo("admin@example.com");
        assertThat(existing.getLastSeenAt()).isAfter(existing.getFirstSeenAt());
        verify(mailSender, never()).sendNotification(any(), any(), any(), any());
    }

    @Test
    void handleSuccessfulLogin_privateAdmin_isSkipped() {
        visitorNotificationService.handleSuccessfulLogin("203.0.113.42", "private-admin@test.com", "Mozilla/5.0");

        verify(visitorNotificationRepository, never()).save(any());
        verify(mailSender, never()).sendNotification(any(), any(), any(), any());
    }

    @Test
    void handleSuccessfulLogin_ignoredIp_isSkipped() {
        visitorNotificationService = new VisitorNotificationService(
                visitorNotificationRepository,
                mailSender,
                new VisitorNotificationProperties(
                        true,
                        "notify@test.com",
                        "sender@test.com",
                        List.of("203.0.113.42")
                ),
                new PrivateAdminPolicy("private-admin@test.com")
        );

        visitorNotificationService.handleSuccessfulLogin("203.0.113.42", "user@example.com", "Mozilla/5.0");

        verify(visitorNotificationRepository, never()).save(any());
        verify(mailSender, never()).sendNotification(any(), any(), any(), any());
    }

    @Test
    void handleSuccessfulLogin_privateIp_isSkipped() {
        visitorNotificationService.handleSuccessfulLogin("127.0.0.1", "user@example.com", "Mozilla/5.0");

        verify(visitorNotificationRepository, never()).save(any());
        verify(mailSender, never()).sendNotification(any(), any(), any(), any());
    }

    private static VisitorNotificationProperties enabledProperties() {
        return new VisitorNotificationProperties(
                true,
                "notify@test.com",
                "sender@test.com",
                List.of()
        );
    }
}
