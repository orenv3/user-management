package com.usermanagement.configurations;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * React Router SPA fallback:
 * Forward non-API routes to index.html so refresh/deep-links work.
 */
@Controller
public class SpaForwardingController {

    @RequestMapping(value = {"/", "/login", "/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}

