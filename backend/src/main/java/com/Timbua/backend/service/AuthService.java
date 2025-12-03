package com.Timbua.backend.service;

import com.Timbua.backend.dto.AuthLoginResponse;
import com.Timbua.backend.dto.ContractorResponseDTO;
import com.Timbua.backend.dto.SupplierResponseDTO;
import com.Timbua.backend.model.Contractor;
import com.Timbua.backend.model.Supplier;
import com.Timbua.backend.model.SuperAdmin;
import com.Timbua.backend.repository.ContractorRepository;
import com.Timbua.backend.repository.SupplierRepository;
import com.Timbua.backend.repository.SuperAdminRepository;
import com.Timbua.backend.model.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final ContractorRepository contractorRepository;
    private final SupplierRepository supplierRepository;
    private final SuperAdminRepository superAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(ContractorRepository contractorRepository,
                       SupplierRepository supplierRepository,
                       SuperAdminRepository superAdminRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.contractorRepository = contractorRepository;
        this.supplierRepository = supplierRepository;
        this.superAdminRepository = superAdminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // -----------------------------
    // Super Admin Signup
    // -----------------------------
    public AuthLoginResponse registerSuperAdmin(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return createErrorResponse("Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            return createErrorResponse("Password is required");
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (checkEmailExists(normalizedEmail) ||
                superAdminRepository.findByEmail(normalizedEmail).isPresent()) {
            return createErrorResponse("Email already exists");
        }

        SuperAdmin admin = new SuperAdmin();
        admin.setEmail(normalizedEmail);
        admin.setPassword(passwordEncoder.encode(password));

        superAdminRepository.save(admin);

        AuthLoginResponse response = new AuthLoginResponse();
        response.setSuccess(true);
        response.setMessage("Super Admin registered successfully");
        response.setRole("SUPER_ADMIN");
        response.setToken(generateJwtToken(admin.getEmail(), "SUPER_ADMIN"));
        return response;
    }

    // -----------------------------
    // User Login
    // -----------------------------
    public AuthLoginResponse authenticateUser(String email, String password) {
        if (email == null || email.trim().isEmpty()) return createErrorResponse("Email is required");
        if (password == null || password.trim().isEmpty()) return createErrorResponse("Password is required");

        String normalizedEmail = email.trim().toLowerCase();

        // SuperAdmin login
        Optional<SuperAdmin> adminOpt = superAdminRepository.findByEmail(normalizedEmail);
        if (adminOpt.isPresent()) {
            SuperAdmin admin = adminOpt.get();
            if (passwordEncoder.matches(password, admin.getPassword())) {
                return createSuccessResponse(admin);
            } else {
                return createErrorResponse("Invalid Super Admin password");
            }
        }

        // Contractor login
        Optional<Contractor> contractorOpt = contractorRepository.findByEmail(normalizedEmail);
        if (contractorOpt.isPresent()) {
            Contractor contractor = contractorOpt.get();
            if (contractor.getStatus() == Contractor.Status.SUSPENDED)
                return createErrorResponse("Contractor account has been suspended");
            if (contractor.getStatus() == Contractor.Status.REJECTED)
                return createErrorResponse("Contractor registration was rejected");

            if (passwordEncoder.matches(password, contractor.getPassword())) {
                return createSuccessResponse(contractor);
            } else {
                return createErrorResponse("Invalid password for contractor account");
            }
        }

        // Supplier login
        Optional<Supplier> supplierOpt = supplierRepository.findByEmail(normalizedEmail);
        if (supplierOpt.isPresent()) {
            Supplier supplier = supplierOpt.get();
            if (supplier.getStatus() == Supplier.Status.SUSPENDED)
                return createErrorResponse("Supplier account has been suspended");
            if (supplier.getStatus() == Supplier.Status.REJECTED)
                return createErrorResponse("Supplier registration was rejected");

            if (passwordEncoder.matches(password, supplier.getPassword())) {
                return createSuccessResponse(supplier);
            } else {
                return createErrorResponse("Invalid password for supplier account");
            }
        }

        return createErrorResponse("No account found with email: " + email);
    }

    // -----------------------------
    // Check email existence
    // -----------------------------
    public boolean checkEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String normalizedEmail = email.trim().toLowerCase();
        return contractorRepository.findByEmail(normalizedEmail).isPresent() ||
                supplierRepository.findByEmail(normalizedEmail).isPresent();
    }

    // -----------------------------
    // Success responses - UPDATED TO USE DTOS
    // -----------------------------
    private AuthLoginResponse createSuccessResponse(SuperAdmin admin) {
        AuthLoginResponse response = new AuthLoginResponse();
        response.setSuccess(true);
        response.setMessage("Welcome Super Admin");
        response.setRole("SUPER_ADMIN");
        response.setContractor(null);
        response.setSupplier(null);
        response.setToken(generateJwtToken(admin.getEmail(), "SUPER_ADMIN"));
        return response;
    }

    private AuthLoginResponse createSuccessResponse(Contractor contractor) {
        AuthLoginResponse response = new AuthLoginResponse();
        response.setSuccess(true);
        response.setMessage("Welcome Contractor! Login successful.");
        response.setRole("CONTRACTOR");

        // Convert Contractor entity to ContractorResponseDTO
        ContractorResponseDTO contractorDTO = new ContractorResponseDTO(contractor);
        response.setContractor(contractorDTO);

        response.setSupplier(null);
        response.setToken(generateJwtToken(contractor.getEmail(), "CONTRACTOR"));
        return response;
    }

    private AuthLoginResponse createSuccessResponse(Supplier supplier) {
        AuthLoginResponse response = new AuthLoginResponse();
        response.setSuccess(true);
        response.setMessage("Welcome Supplier! Login successful.");
        response.setRole("SUPPLIER");

        // Convert Supplier entity to SupplierResponseDTO
        SupplierResponseDTO supplierDTO = new SupplierResponseDTO(supplier);
        response.setSupplier(supplierDTO);

        response.setContractor(null);
        response.setToken(generateJwtToken(supplier.getEmail(), "SUPPLIER"));
        return response;
    }

    // -----------------------------
    // Error response
    // -----------------------------
    private AuthLoginResponse createErrorResponse(String message) {
        AuthLoginResponse response = new AuthLoginResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    // -----------------------------
    // JWT token generation
    // -----------------------------
    private String generateJwtToken(String email, String role) {
        return jwtUtil.generateToken(email, role);
    }
}
