package com.onedao.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    // Any logged-in user
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        return "Welcome " + authentication.getName();
    }

    // Administrator only (enforced in SecurityConfig)
    @GetMapping("/settings")
    public String settings() {
        return "Settings page: Administrator access granted";
    }
}