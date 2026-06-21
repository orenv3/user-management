package com.usermanagement.controllers;

import com.usermanagement.dao.services.FrontendConfigService;
import com.usermanagement.responseObjects.PublicConfigResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "PublicConfigController", description = "Public frontend configuration served at runtime.")
@RequestMapping("/api/config")
@RestController
public class PublicConfigController {

    private final FrontendConfigService frontendConfigService;

    @GetMapping
    @Operation(summary = "Get public frontend configuration")
    public PublicConfigResponse getConfig() {
        return frontendConfigService.getPublicConfig();
    }
}
