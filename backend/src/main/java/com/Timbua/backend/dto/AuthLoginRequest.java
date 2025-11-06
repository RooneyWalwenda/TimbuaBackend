package com.Timbua.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request payload for user authentication")
public class AuthLoginRequest {

    @Schema(description = "User email address", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "User password", example = "password123", required = true)
    private String password;

    // Constructors
    public AuthLoginRequest() {}

    public AuthLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}