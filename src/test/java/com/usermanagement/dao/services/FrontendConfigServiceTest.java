package com.usermanagement.dao.services;

import com.usermanagement.config.FrontendDisplayProperties;
import com.usermanagement.config.PrivateAdminPolicy;
import com.usermanagement.responseObjects.PublicConfigResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FrontendConfigServiceTest {

    @Test
    void getPublicConfig_trimsBlankValuesToNull() {
        FrontendConfigService service = new FrontendConfigService(
                new FrontendDisplayProperties(
                        "  https://youtu.be/demo  ",
                        "",
                        " user ",
                        null,
                        " pass ",
                        null,
                        " admin@example.com ",
                        "  "
                ),
                new PrivateAdminPolicy(" private@test.com ")
        );

        PublicConfigResponse config = service.getPublicConfig();

        assertThat(config.demoVideoUrl()).isEqualTo("https://youtu.be/demo");
        assertThat(config.projectRepoUrl()).isNull();
        assertThat(config.privateAdminEmail()).isEqualTo("private@test.com");
        assertThat(config.seedUserName()).isEqualTo("user");
        assertThat(config.seedUserEmail()).isNull();
        assertThat(config.seedUserPassword()).isEqualTo("pass");
        assertThat(config.seedAdminEmail()).isEqualTo("admin@example.com");
        assertThat(config.seedAdminPassword()).isNull();
    }
}
