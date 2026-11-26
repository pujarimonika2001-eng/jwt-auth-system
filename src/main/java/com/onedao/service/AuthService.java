package com.onedao.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.onedao.dto.LoginRequest;
import com.onedao.dto.SignUpRequest;
import com.onedao.exception.InvalidCredentialsException;
import com.onedao.exception.UserAlreadyExistsException;
import com.onedao.model.User;
import com.onedao.model.UserType;
import com.onedao.repository.UserRepository;
import com.onedao.security.JwtUtil;
import com.onedao.security.TokenBlacklist;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       TokenBlacklist tokenBlacklist) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
    }

    // Sign-up: check duplicates, hash the password, save in memory
    public String signUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()), // BCrypt hash
                UserType.fromLabel(request.getUserType()));

        userRepository.save(user);
        return "User registered successfully";
    }

    // Login: verify password, return a JWT token
    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Login Failed"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Login Failed");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getUserType().name());
    }

    // Logout: blacklist the token so it cannot be used again
    public String logout(String token) {
        String username = jwtUtil.extractUsername(token);
        tokenBlacklist.add(token);
        return username + " Logged Out";
    }
}