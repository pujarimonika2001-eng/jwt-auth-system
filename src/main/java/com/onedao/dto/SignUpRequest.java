package com.onedao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignUpRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 8, max = 50, message = "Username must be 8 to 50 characters")
    private String username;

    // Must be a valid email shape and contain both '@' and '_'
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^(?=.*_)[^@\\s]+@[^@\\s]+\\.[^@\\s]+$",
             message = "Email must be valid and contain '@' and '_'")
    private String email;

    // 8-12 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special character
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,12}$",
             message = "Password must be 8-12 characters with at least 1 uppercase, 1 lowercase, 1 number and 1 special character")
    private String password;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "^(Standard User|Administrator)$",
             message = "User type must be 'Standard User' or 'Administrator'")
    private String userType;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}