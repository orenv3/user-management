package com.usermanagement.responseObjects;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PublicConfigResponse(
        String demoVideoUrl,
        String projectRepoUrl,
        String privateAdminEmail,
        String seedUserName,
        String seedUserEmail,
        String seedUserPassword,
        String seedAdminName,
        String seedAdminEmail,
        String seedAdminPassword
) {
}
