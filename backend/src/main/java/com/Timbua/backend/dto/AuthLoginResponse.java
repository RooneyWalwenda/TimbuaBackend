package com.Timbua.backend.dto;

import com.Timbua.backend.model.Contractor;
import com.Timbua.backend.model.Supplier;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response with user details and role for authentication")
public class AuthLoginResponse {

    @Schema(description = "Success status", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Login successful")
    private String message;

    @Schema(description = "User role", example = "CONTRACTOR")
    private String role;

    @Schema(description = "JWT token for authenticated requests")
    private String token;

    @Schema(description = "Contractor details (if contractor)")
    private Contractor contractor;

    @Schema(description = "Supplier details (if supplier)")
    private Supplier supplier;

    // Constructors
    public AuthLoginResponse() {}

    public AuthLoginResponse(boolean success, String message, String role) {
        this.success = success;
        this.message = message;
        this.role = role;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Contractor getContractor() { return contractor; }
    public void setContractor(Contractor contractor) { this.contractor = contractor; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
}