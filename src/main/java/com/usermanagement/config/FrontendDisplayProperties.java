package com.usermanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.frontend")
public record FrontendDisplayProperties(
        String demoVideoUrl,
        String projectRepoUrl,
        String seedUserName,
        String seedUserEmail,
        String seedUserPassword,
        String seedAdminName,
        String seedAdminEmail,
        String seedAdminPassword
) {
}
