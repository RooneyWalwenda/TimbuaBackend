package com.Timbua.backend.controller;

import com.Timbua.backend.dto.AuthLoginRequest;
import com.Timbua.backend.dto.AuthLoginResponse;
import com.Timbua.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "APIs for login, signup, and account verification")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // --------------------------------------------------------------------
    // SUPER ADMIN SIGNUP
    // --------------------------------------------------------------------
    @PostMapping("/super-admin/signup")
    @Operation(summary = "Register Super Admin", description = "Create a new Super Admin account")
    public ResponseEntity<AuthLoginResponse> registerSuperAdmin(@RequestBody AuthLoginRequest request) {

        logger.info("Super Admin signup attempt for email: {}", request.getEmail());

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthLoginResponse(false, "Email is required", null));
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthLoginResponse(false, "Password is required", null));
        }

        AuthLoginResponse response = authService.registerSuperAdmin(
                request.getEmail(),
                request.getPassword()
        );

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // --------------------------------------------------------------------
    // LOGIN (Super Admin, Contractor, Supplier handled in service)
    // --------------------------------------------------------------------
    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user across super admin, contractor, and supplier tables."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthLoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid login",
                    content = @Content(schema = @Schema(implementation = AuthLoginResponse.class)))
    })
    public ResponseEntity<AuthLoginResponse> login(@RequestBody AuthLoginRequest loginRequest) {

        try {
            logger.info("Login attempt for email: {}", loginRequest.getEmail());

            if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new AuthLoginResponse(false, "Email is required", null));
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new AuthLoginResponse(false, "Password is required", null));
            }

            AuthLoginResponse response =
                    authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

            if (response.isSuccess()) {
                logger.info("Login successful for email: {} as {}", loginRequest.getEmail(), response.getRole());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Login failed for email: {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            logger.error("Login error for email {}", loginRequest.getEmail(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthLoginResponse(false, "Internal server error", null));
        }
    }

    // --------------------------------------------------------------------
    // CHECK EMAIL
    // --------------------------------------------------------------------
    @GetMapping("/check-email")
    @Operation(summary = "Check email", description = "Check if email already exists in system.")
    public ResponseEntity<EmailCheckResponse> checkEmailExists(@RequestParam String email) {

        try {
            logger.debug("Checking email: {}", email);

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.ok(new EmailCheckResponse(false, "Email is required"));
            }

            boolean exists = authService.checkEmailExists(email);
            String message = exists ? "Email already registered" : "Email available";

            return ResponseEntity.ok(new EmailCheckResponse(exists, message));

        } catch (Exception e) {
            logger.error("Error checking email {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailCheckResponse(false, "Error checking email"));
        }
    }


    // --------------------------------------------------------------------
    // TOKEN VALIDATION (Placeholder since you use no JWT yet)
    // --------------------------------------------------------------------
    @GetMapping("/validate-token")
    @Operation(summary = "Validate token", description = "Placeholder token validation")
    public ResponseEntity<AuthLoginResponse> validateToken(@RequestHeader("Authorization") String token) {

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthLoginResponse(false, "Missing token", null));
        }

        // No real JWT implemented, returns placeholder
        AuthLoginResponse response = new AuthLoginResponse(true, "Token is valid (placeholder)", null);

        return ResponseEntity.ok(response);
    }


    // --------------------------------------------------------------------
    // INTERNAL RESPONSE CLASS FOR EMAIL CHECK
    // --------------------------------------------------------------------
    public static class EmailCheckResponse {
        private boolean exists;
        private String message;

        public EmailCheckResponse(boolean exists, String message) {
            this.exists = exists;
            this.message = message;
        }

        public boolean isExists() {
            return exists;
        }

        public String getMessage() {
            return message;
        }
    }
}
