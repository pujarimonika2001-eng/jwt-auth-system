package com.onedao.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onedao.dto.LoginRequest;
import com.onedao.dto.OtpRequest;
import com.onedao.dto.SignUpRequest;
import com.onedao.exception.InvalidCredentialsException;
import com.onedao.exception.OtpException;
import com.onedao.service.AuthService;
import com.onedao.service.CountryService;
import com.onedao.service.OtpService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final CountryService countryService;
    private final OtpService otpService;

    public AuthController(AuthService authService,
                          CountryService countryService,
                          OtpService otpService) {
        this.authService = authService;
        this.countryService = countryService;
        this.otpService = otpService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request,
                                         HttpServletRequest httpRequest) {
        countryService.validate(getClientIp(httpRequest)); // block restricted countries
        String message = authService.signUp(request);
        String otp = otpService.generate(request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(message + ". Your OTP is " + otp + " (valid for 5 minutes)");
    }

    @PostMapping("/auth/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody OtpRequest request) {
        otpService.verify(request.getUsername(), request.getOtp());
        return ResponseEntity.ok("OTP verified successfully");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);

            if (otpService.isPending(request.getUsername())) {
                throw new OtpException("Please verify OTP before login");
            }
            return ResponseEntity.ok(Map.of("token", token));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(URI.create("/api/error"))
                    .build();
        }
    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(authService.logout(token));
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}