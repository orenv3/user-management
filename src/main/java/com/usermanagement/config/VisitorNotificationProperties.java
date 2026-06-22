package com.usermanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.visitor-notify")
public record VisitorNotificationProperties(
        boolean enabled,
        String to,
        String from,
        List<String> ignoreIps
) {
}
