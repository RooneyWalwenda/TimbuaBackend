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
@Tag(name = "Authentication", description = "APIs for user authentication, login, and account verification")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user by email and password. System checks both contractor and supplier tables. Returns user details with role for frontend routing."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful - returns user details and role",
                    content = @Content(schema = @Schema(implementation = AuthLoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid credentials or account not active",
                    content = @Content(schema = @Schema(implementation = AuthLoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - missing or invalid parameters",
                    content = @Content(schema = @Schema(implementation = AuthLoginResponse.class))
            )
    })
    public ResponseEntity<AuthLoginResponse> login(@RequestBody AuthLoginRequest loginRequest) {
        try {
            logger.info("Login attempt for email: {}", loginRequest.getEmail());

            // Validate request
            if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
                AuthLoginResponse errorResponse = new AuthLoginResponse(false, "Email is required", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                AuthLoginResponse errorResponse = new AuthLoginResponse(false, "Password is required", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            AuthLoginResponse response = authService.authenticateUser(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );

            if (response.isSuccess()) {
                logger.info("Successful login for email: {}, role: {}", loginRequest.getEmail(), response.getRole());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Failed login attempt for email: {} - {}", loginRequest.getEmail(), response.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", loginRequest.getEmail(), e);
            AuthLoginResponse errorResponse = new AuthLoginResponse(false, "Internal server error during login", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/check-email")
    @Operation(
            summary = "Check email availability",
            description = "Check if email exists in either contractor or supplier database. Useful for registration form validation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email check completed successfully"
            )
    })
    public ResponseEntity<EmailCheckResponse> checkEmailExists(@RequestParam String email) {
        try {
            logger.debug("Checking email existence: {}", email);

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.ok(new EmailCheckResponse(false, "Email parameter is required"));
            }

            boolean exists = authService.checkEmailExists(email);
            String message = exists ? "Email already registered" : "Email available";

            logger.debug("Email check result for {}: {}", email, exists ? "exists" : "available");
            return ResponseEntity.ok(new EmailCheckResponse(exists, message));

        } catch (Exception e) {
            logger.error("Error checking email existence: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EmailCheckResponse(false, "Error checking email availability"));
        }
    }

    @GetMapping("/validate-token")
    @Operation(
            summary = "Validate authentication token",
            description = "Validate JWT token and return user information. Useful for session persistence and frontend routing."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token is valid",
                    content = @Content(schema = @Schema(implementation = AuthLoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token is invalid or expired",
                    content = @Content(schema = @Schema(implementation = AuthLoginResponse.class))
            )
    })
    public ResponseEntity<AuthLoginResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            logger.debug("Validating token");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                AuthLoginResponse errorResponse = new AuthLoginResponse(false, "Invalid authorization header", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // For now, using placeholder token validation
            // Replace with proper JWT validation when implemented
            if (authService.validateToken(token)) {
                String role = authService.getRoleFromToken(token);
                Long userId = authService.getUserIdFromToken(token);

                AuthLoginResponse response = new AuthLoginResponse();
                response.setSuccess(true);
                response.setMessage("Token is valid");
                response.setRole(role);
                response.setToken(token);

                logger.debug("Token validation successful for user ID: {}, role: {}", userId, role);
                return ResponseEntity.ok(response);
            } else {
                AuthLoginResponse errorResponse = new AuthLoginResponse(false, "Invalid or expired token", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

        } catch (Exception e) {
            logger.error("Error validating token", e);
            AuthLoginResponse errorResponse = new AuthLoginResponse(false, "Error validating token", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "User logout",
            description = "Invalidate user session/token. Currently placeholder for future JWT blacklisting implementation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful"
            )
    })
    public ResponseEntity<LogoutResponse> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            logger.info("User logout request");

            // In a real implementation, you would:
            // 1. Extract token from header
            // 2. Add token to blacklist
            // 3. Clear session data

            String message = "Logout successful";
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                logger.debug("Logging out token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
                // Future: Add token to blacklist
            }

            return ResponseEntity.ok(new LogoutResponse(true, message));

        } catch (Exception e) {
            logger.error("Error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogoutResponse(false, "Error during logout"));
        }
    }

    @GetMapping("/health")
    @Operation(
            summary = "Authentication service health check",
            description = "Check if authentication service is running and responsive"
    )
    public ResponseEntity<HealthResponse> healthCheck() {
        try {
            // Simple health check - you can add more sophisticated checks
            // like database connectivity, etc.
            return ResponseEntity.ok(new HealthResponse("OK", "Authentication service is healthy"));
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new HealthResponse("ERROR", "Authentication service is unavailable"));
        }
    }

    // Inner DTO classes for specific responses
    public static class EmailCheckResponse {
        private boolean exists;
        private String message;

        public EmailCheckResponse() {}

        public EmailCheckResponse(boolean exists, String message) {
            this.exists = exists;
            this.message = message;
        }

        // Getters and Setters
        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class LogoutResponse {
        private boolean success;
        private String message;

        public LogoutResponse() {}

        public LogoutResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class HealthResponse {
        private String status;
        private String message;

        public HealthResponse() {}

        public HealthResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        // Getters and Setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}