package com.usermanagement.dao.services;

import com.usermanagement.config.FrontendDisplayProperties;
import com.usermanagement.config.PrivateAdminPolicy;
import com.usermanagement.responseObjects.PublicConfigResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FrontendConfigService {

    private final FrontendDisplayProperties frontendDisplayProperties;
    private final PrivateAdminPolicy privateAdminPolicy;

    public PublicConfigResponse getPublicConfig() {
        return new PublicConfigResponse(
                blankToNull(frontendDisplayProperties.demoVideoUrl()),
                blankToNull(frontendDisplayProperties.projectRepoUrl()),
                blankToNull(privateAdminPolicy.configuredEmail()),
                blankToNull(frontendDisplayProperties.seedUserName()),
                blankToNull(frontendDisplayProperties.seedUserEmail()),
                blankToNull(frontendDisplayProperties.seedUserPassword()),
                blankToNull(frontendDisplayProperties.seedAdminName()),
                blankToNull(frontendDisplayProperties.seedAdminEmail()),
                blankToNull(frontendDisplayProperties.seedAdminPassword())
        );
    }

    private static String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
