package com.usermanagement.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(VisitorNotificationProperties.class)
public class VisitorNotificationConfiguration {
}
