package com.Timbua.backend.service;

import com.Timbua.backend.dto.AuthLoginResponse;
import com.Timbua.backend.model.Contractor;
import com.Timbua.backend.model.Supplier;
import com.Timbua.backend.repository.ContractorRepository;
import com.Timbua.backend.repository.SupplierRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final ContractorRepository contractorRepository;
    private final SupplierRepository supplierRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(ContractorRepository contractorRepository,
                       SupplierRepository supplierRepository,
                       PasswordEncoder passwordEncoder) {
        this.contractorRepository = contractorRepository;
        this.supplierRepository = supplierRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate user by email and password across both contractor and supplier tables
     * @param email User email address
     * @param password User password (plain text)
     * @return AuthLoginResponse with user details and role if successful, error message if failed
     */
    public AuthLoginResponse authenticateUser(String email, String password) {
        // Validate input parameters
        if (email == null || email.trim().isEmpty()) {
            return createErrorResponse("Email is required");
        }

        if (password == null || password.trim().isEmpty()) {
            return createErrorResponse("Password is required");
        }

        // Normalize email (trim and convert to lowercase)
        String normalizedEmail = email.trim().toLowerCase();

        // First check contractors table
        Optional<Contractor> contractorOpt = contractorRepository.findByEmail(normalizedEmail);
        if (contractorOpt.isPresent()) {
            Contractor contractor = contractorOpt.get();

            // Check if contractor account is active
            if (contractor.getStatus() == Contractor.Status.SUSPENDED) {
                return createErrorResponse("Contractor account has been suspended");
            }

            if (contractor.getStatus() == Contractor.Status.REJECTED) {
                return createErrorResponse("Contractor registration was rejected");
            }

            // Verify password
            if (passwordEncoder.matches(password, contractor.getPassword())) {
                return createSuccessResponse(contractor, "CONTRACTOR",
                        getWelcomeMessage(contractor.getStatus(), "contractor"));
            } else {
                return createErrorResponse("Invalid password for contractor account");
            }
        }

        // Then check suppliers table
        Optional<Supplier> supplierOpt = supplierRepository.findByEmail(normalizedEmail);
        if (supplierOpt.isPresent()) {
            Supplier supplier = supplierOpt.get();

            // Check if supplier account is active
            if (supplier.getStatus() == Supplier.Status.SUSPENDED) {
                return createErrorResponse("Supplier account has been suspended");
            }

            if (supplier.getStatus() == Supplier.Status.REJECTED) {
                return createErrorResponse("Supplier registration was rejected");
            }

            // Verify password
            if (passwordEncoder.matches(password, supplier.getPassword())) {
                return createSuccessResponse(supplier, "SUPPLIER",
                        getWelcomeMessage(supplier.getStatus(), "supplier"));
            } else {
                return createErrorResponse("Invalid password for supplier account");
            }
        }

        return createErrorResponse("No account found with email: " + email);
    }

    /**
     * Check if email exists in either contractor or supplier database
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean checkEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String normalizedEmail = email.trim().toLowerCase();
        return contractorRepository.findByEmail(normalizedEmail).isPresent() ||
                supplierRepository.findByEmail(normalizedEmail).isPresent();
    }

    /**
     * Get contractor by email (for internal use)
     * @param email Contractor email
     * @return Optional containing contractor if found
     */
    public Optional<Contractor> getContractorByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return contractorRepository.findByEmail(email.trim().toLowerCase());
    }

    /**
     * Get supplier by email (for internal use)
     * @param email Supplier email
     * @return Optional containing supplier if found
     */
    public Optional<Supplier> getSupplierByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return supplierRepository.findByEmail(email.trim().toLowerCase());
    }

    /**
     * Create successful login response for contractor
     */
    private AuthLoginResponse createSuccessResponse(Contractor contractor, String role, String message) {
        AuthLoginResponse response = new AuthLoginResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setRole(role);
        response.setContractor(contractor);
        response.setSupplier(null);

        // For security, clear the password in the response
        contractor.setPassword(null);

        // Set placeholder token (implement JWT later)
        response.setToken(generatePlaceholderToken(contractor.getId(), role));

        return response;
    }

    /**
     * Create successful login response for supplier
     */
    private AuthLoginResponse createSuccessResponse(Supplier supplier, String role, String message) {
        AuthLoginResponse response = new AuthLoginResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setRole(role);
        response.setSupplier(supplier);
        response.setContractor(null);

        // For security, clear the password in the response
        supplier.setPassword(null);

        // Set placeholder token (implement JWT later)
        response.setToken(generatePlaceholderToken(supplier.getId(), role));

        return response;
    }

    /**
     * Create error response
     */
    private AuthLoginResponse createErrorResponse(String message) {
        AuthLoginResponse response = new AuthLoginResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setRole(null);
        response.setToken(null);
        response.setContractor(null);
        response.setSupplier(null);
        return response;
    }

    /**
     * Generate appropriate welcome message based on account status
     */
    private String getWelcomeMessage(Enum<?> status, String userType) {
        switch (status.toString()) {
            case "PENDING":
                return String.format("%s login successful. Your account is pending verification.",
                        userType.substring(0, 1).toUpperCase() + userType.substring(1));
            case "UNDER_REVIEW":
                return String.format("%s login successful. Your account is under review.",
                        userType.substring(0, 1).toUpperCase() + userType.substring(1));
            case "VERIFIED":
                return String.format("%s login successful. Welcome back!",
                        userType.substring(0, 1).toUpperCase() + userType.substring(1));
            default:
                return String.format("%s login successful.",
                        userType.substring(0, 1).toUpperCase() + userType.substring(1));
        }
    }

    /**
     * Generate placeholder token (replace with JWT implementation later)
     */
    private String generatePlaceholderToken(Long userId, String role) {
        // This is a placeholder - implement proper JWT token generation
        // For now, return a simple string that can be validated
        return String.format("placeholder-token-%s-%d-%d",
                role.toLowerCase(), userId, System.currentTimeMillis());
    }

    /**
     * Validate placeholder token (for future JWT implementation)
     */
    public boolean validateToken(String token) {
        // Placeholder implementation - replace with proper JWT validation
        return token != null && token.startsWith("placeholder-token-");
    }

    /**
     * Get user role from token (for future JWT implementation)
     */
    public String getRoleFromToken(String token) {
        // Placeholder implementation - replace with proper JWT parsing
        if (token != null && token.startsWith("placeholder-token-")) {
            String[] parts = token.split("-");
            if (parts.length >= 3) {
                return parts[2].toUpperCase();
            }
        }
        return null;
    }

    /**
     * Get user ID from token (for future JWT implementation)
     */
    public Long getUserIdFromToken(String token) {
        // Placeholder implementation - replace with proper JWT parsing
        if (token != null && token.startsWith("placeholder-token-")) {
            String[] parts = token.split("-");
            if (parts.length >= 4) {
                try {
                    return Long.parseLong(parts[3]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
}