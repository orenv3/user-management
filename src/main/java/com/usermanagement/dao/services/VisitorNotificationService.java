package com.usermanagement.dao.services;

import com.usermanagement.config.PrivateAdminPolicy;
import com.usermanagement.config.VisitorNotificationProperties;
import com.usermanagement.entities.VisitorNotification;
import com.usermanagement.repositories.VisitorNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class VisitorNotificationService {

    private static final Logger log = LoggerFactory.getLogger(VisitorNotificationService.class);

    private final VisitorNotificationRepository visitorNotificationRepository;
    private final VisitorNotificationMailSender mailSender;
    private final VisitorNotificationProperties properties;
    private final PrivateAdminPolicy privateAdminPolicy;

    public void handleSuccessfulLogin(String clientIp, String loginEmail, String userAgent) {
        String skipReason = skipReason(clientIp, loginEmail);
        if (skipReason != null) {
            log.info(
                    "Visitor notification skipped. reason={} visitorIp={} loginAccount={}",
                    skipReason,
                    clientIp,
                    loginEmail
            );
            return;
        }

        Instant now = Instant.now();
        if (visitorNotificationRepository.existsByClientIp(clientIp)) {
            visitorNotificationRepository.findByClientIp(clientIp).ifPresent(existing -> {
                existing.setLastSeenAt(now);
                if (loginEmail != null && !loginEmail.isBlank()) {
                    existing.setLoginEmail(loginEmail.trim());
                }
                visitorNotificationRepository.save(existing);
            });
            log.info("Visitor notification skipped. reason=already_notified visitorIp={} loginAccount={}", clientIp, loginEmail);
            return;
        }

        try {
            VisitorNotification notification = new VisitorNotification();
            notification.setClientIp(clientIp);
            notification.setLoginEmail(trimToNull(loginEmail));
            notification.setUserAgent(trimUserAgent(userAgent));
            notification.setFirstSeenAt(now);
            notification.setLastSeenAt(now);
            visitorNotificationRepository.save(notification);
            mailSender.sendNotification(clientIp, loginEmail, userAgent, now);
            log.info(
                    "New visitor recorded for notification. notifyFrom={} notifyTo={} visitorIp={} loginAccount={}",
                    properties.from(),
                    properties.to(),
                    clientIp,
                    loginEmail
            );
        } catch (DataIntegrityViolationException ex) {
            visitorNotificationRepository.findByClientIp(clientIp).ifPresent(existing -> {
                existing.setLastSeenAt(now);
                if (loginEmail != null && !loginEmail.isBlank()) {
                    existing.setLoginEmail(loginEmail.trim());
                }
                visitorNotificationRepository.save(existing);
            });
            log.info("Visitor notification skipped. reason=concurrent_duplicate visitorIp={} loginAccount={}", clientIp, loginEmail);
        }
    }

    private String skipReason(String clientIp, String loginEmail) {
        if (!properties.enabled()) {
            return "disabled";
        }
        if (isBlank(properties.to()) || isBlank(properties.from())) {
            return "missing_notify_to_or_from";
        }
        if (clientIp == null || clientIp.isBlank()) {
            return "missing_client_ip";
        }
        if (privateAdminPolicy.isPrivateAdmin(loginEmail)) {
            return "private_admin_login";
        }
        if (isIgnoredIp(clientIp.trim())) {
            return "ignored_ip";
        }
        if (isPrivateOrLoopback(clientIp.trim())) {
            return "private_or_loopback_ip";
        }
        return null;
    }

    private boolean isIgnoredIp(String clientIp) {
        List<String> ignoreIps = properties.ignoreIps();
        if (ignoreIps == null || ignoreIps.isEmpty()) {
            return false;
        }
        return ignoreIps.stream()
                .filter(ip -> ip != null && !ip.isBlank())
                .map(String::trim)
                .anyMatch(clientIp::equals);
    }

    private static boolean isPrivateOrLoopback(String clientIp) {
        try {
            InetAddress address = InetAddress.getByName(clientIp);
            return address.isLoopbackAddress()
                    || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress();
        } catch (UnknownHostException ex) {
            return true;
        }
    }

    private static String trimUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return null;
        }
        String trimmed = userAgent.trim();
        return trimmed.length() <= 500 ? trimmed : trimmed.substring(0, 500);
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
