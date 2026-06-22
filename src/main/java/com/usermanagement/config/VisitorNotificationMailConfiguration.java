package com.usermanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConditionalOnProperty(name = "app.visitor-notify.enabled", havingValue = "true")
public class VisitorNotificationMailConfiguration {

    private static final Logger log = LoggerFactory.getLogger(VisitorNotificationMailConfiguration.class);

    @Bean
    @Primary
    JavaMailSender visitorNotificationJavaMailSender(
            @Value("${spring.mail.host:smtp.gmail.com}") String host,
            @Value("${spring.mail.port:587}") int port,
            @Value("${spring.mail.username:}") String username,
            @Value("${GMAIL_APP_PASSWORD:}") String rawPassword
    ) {
        String password = normalizeAppPassword(rawPassword);
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        log.info(
                "Visitor notification mail configured. smtpHost={} smtpUser={} appPasswordConfigured={}",
                host,
                username,
                !password.isBlank()
        );
        return mailSender;
    }

    static String normalizeAppPassword(String rawPassword) {
        if (rawPassword == null) {
            return "";
        }
        return rawPassword.replace(" ", "").trim();
    }
}
