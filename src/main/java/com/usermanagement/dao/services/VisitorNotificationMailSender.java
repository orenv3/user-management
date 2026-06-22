package com.usermanagement.dao.services;

import com.usermanagement.config.VisitorNotificationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class VisitorNotificationMailSender {

    private static final Logger log = LoggerFactory.getLogger(VisitorNotificationMailSender.class);

    private final JavaMailSender mailSender;
    private final VisitorNotificationProperties properties;

    public VisitorNotificationMailSender(JavaMailSender mailSender, VisitorNotificationProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Async("visitorNotificationExecutor")
    public void sendNotification(String clientIp, String loginEmail, String userAgent, Instant seenAt) {
        String notifyFrom = properties.from();
        String notifyTo = properties.to();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(notifyFrom);
            message.setTo(notifyTo);
            message.setSubject("New demo visitor logged in");
            message.setText(buildBody(clientIp, loginEmail, userAgent, seenAt));
            mailSender.send(message);
            log.info(
                    "Visitor notification email sent. notifyFrom={} notifyTo={} visitorIp={} loginAccount={}",
                    notifyFrom,
                    notifyTo,
                    clientIp,
                    loginEmail
            );
        } catch (Exception ex) {
            log.error(
                    "Failed to send visitor notification email. notifyFrom={} notifyTo={} visitorIp={} loginAccount={}",
                    notifyFrom,
                    notifyTo,
                    clientIp,
                    loginEmail,
                    ex
            );
        }
    }

    private static String buildBody(String clientIp, String loginEmail, String userAgent, Instant seenAt) {
        return """
                New demo visitor logged in.

                IP: %s
                Account: %s
                Time (UTC): %s
                User-Agent: %s
                """.formatted(
                clientIp,
                loginEmail == null || loginEmail.isBlank() ? "(unknown)" : loginEmail.trim(),
                seenAt,
                userAgent == null || userAgent.isBlank() ? "(unknown)" : userAgent.trim()
        );
    }
}
