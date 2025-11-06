package com.Timbua.backend.service;

import com.Timbua.backend.model.Contractor;
import com.Timbua.backend.model.ContractorDocument;
import com.Timbua.backend.repository.ContractorDocumentRepository;
import com.Timbua.backend.repository.ContractorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContractorService {

    private static final Logger logger = LoggerFactory.getLogger(ContractorService.class);

    private final ContractorRepository contractorRepository;
    private final ContractorDocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;

    public ContractorService(ContractorRepository contractorRepository,
                             ContractorDocumentRepository documentRepository,
                             PasswordEncoder passwordEncoder) {
        this.contractorRepository = contractorRepository;
        this.documentRepository = documentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Self-registration for contractors with proper role assignment and validation
     */
    public Contractor registerContractor(Contractor contractor) {
        logger.info("Registering new contractor with email: {}", contractor.getEmail());

        // Validate input
        validateContractorRegistration(contractor);

        // Encrypt password
        contractor.setPassword(passwordEncoder.encode(contractor.getPassword()));

        // Set initial status and role
        contractor.setStatus(Contractor.Status.PENDING);
        contractor.setRole(Contractor.Role.CONTRACTOR); // Set the role explicitly
        contractor.setIsVerified(false);
        contractor.setRegistrationDate(java.time.LocalDateTime.now());

        Contractor savedContractor = contractorRepository.save(contractor);
        logger.info("Contractor registered successfully with ID: {}", savedContractor.getId());

        return savedContractor;
    }

    /**
     * Validate contractor registration data
     */
    private void validateContractorRegistration(Contractor contractor) {
        if (contractor.getEmail() == null || contractor.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (contractor.getPassword() == null || contractor.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (contractor.getCompanyName() == null || contractor.getCompanyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }

        if (contractor.getContactPerson() == null || contractor.getContactPerson().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact person is required");
        }

        // Check uniqueness
        if (contractorRepository.existsByEmail(contractor.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Contractor with this email already exists");
        }

        if (contractor.getBusinessRegistrationNumber() != null &&
                contractorRepository.existsByBusinessRegistrationNumber(contractor.getBusinessRegistrationNumber())) {
            throw new IllegalArgumentException("Contractor with this business registration number already exists");
        }
    }

    /**
     * Admin verification with status management
     */
    public Contractor verifyContractor(Long id, boolean approved, String remarks) {
        logger.info("Verifying contractor ID: {}, approved: {}, remarks: {}", id, approved, remarks);

        Contractor contractor = getContractorById(id)
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + id));

        // Validate current status
        if (contractor.getStatus() == Contractor.Status.VERIFIED && approved) {
            throw new IllegalStateException("Contractor is already verified");
        }

        if (contractor.getStatus() == Contractor.Status.REJECTED && !approved) {
            throw new IllegalStateException("Contractor is already rejected");
        }

        if (approved) {
            contractor.setStatus(Contractor.Status.VERIFIED);
            contractor.setIsVerified(true);
            contractor.setVerificationDate(LocalDate.now());
            logger.info("Contractor ID: {} verified successfully", id);
        } else {
            contractor.setStatus(Contractor.Status.REJECTED);
            contractor.setIsVerified(false);
            logger.warn("Contractor ID: {} rejected. Remarks: {}", id, remarks);
        }

        return contractorRepository.save(contractor);
    }

    /**
     * Update contractor status with validation
     */
    public Contractor updateContractorStatus(Long id, Contractor.Status newStatus) {
        logger.info("Updating contractor ID: {} status to: {}", id, newStatus);

        Contractor contractor = getContractorById(id)
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + id));

        // Validate status transition
        validateStatusTransition(contractor.getStatus(), newStatus);

        contractor.setStatus(newStatus);

        // Handle verification flags
        if (newStatus == Contractor.Status.VERIFIED) {
            contractor.setIsVerified(true);
            contractor.setVerificationDate(LocalDate.now());
        } else if (newStatus == Contractor.Status.REJECTED || newStatus == Contractor.Status.SUSPENDED) {
            contractor.setIsVerified(false);
        }

        return contractorRepository.save(contractor);
    }

    /**
     * Validate status transitions
     */
    private void validateStatusTransition(Contractor.Status currentStatus, Contractor.Status newStatus) {
        // Add business rules for status transitions
        if (currentStatus == Contractor.Status.REJECTED && newStatus == Contractor.Status.VERIFIED) {
            throw new IllegalStateException("Cannot verify a rejected contractor without review");
        }
        // Add more business rules as needed
    }

    /**
     * Document management
     */
    public ContractorDocument uploadDocument(Long contractorId, ContractorDocument document) {
        logger.debug("Uploading document for contractor ID: {}", contractorId);

        Contractor contractor = getContractorById(contractorId)
                .orElseThrow(() -> new RuntimeException("Contractor not found"));

        document.setContractor(contractor);
        ContractorDocument savedDocument = documentRepository.save(document);

        logger.info("Document uploaded successfully for contractor ID: {}, document ID: {}",
                contractorId, savedDocument.getId());
        return savedDocument;
    }

    public List<ContractorDocument> getContractorDocuments(Long contractorId) {
        logger.debug("Retrieving documents for contractor ID: {}", contractorId);
        return documentRepository.findByContractorId(contractorId);
    }

    public ContractorDocument updateDocumentStatus(Long documentId, ContractorDocument.Status status) {
        logger.info("Updating document ID: {} status to: {}", documentId, status);

        ContractorDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setStatus(status);
        return documentRepository.save(document);
    }

    /**
     * Update contractor profile (excludes sensitive fields)
     */
    public Contractor updateContractor(Long id, Contractor contractorDetails) {
        logger.info("Updating contractor profile ID: {}", id);

        return contractorRepository.findById(id)
                .map(existingContractor -> {
                    // Don't update email, password, role, or verification status here
                    existingContractor.setCompanyName(contractorDetails.getCompanyName());
                    existingContractor.setContactPerson(contractorDetails.getContactPerson());
                    existingContractor.setPhoneNumber(contractorDetails.getPhoneNumber());
                    existingContractor.setPhysicalAddress(contractorDetails.getPhysicalAddress());
                    existingContractor.setSpecialization(contractorDetails.getSpecialization());
                    existingContractor.setYearsOfExperience(contractorDetails.getYearsOfExperience());
                    existingContractor.setLicenseNumber(contractorDetails.getLicenseNumber());

                    Contractor updatedContractor = contractorRepository.save(existingContractor);
                    logger.info("Contractor profile updated successfully for ID: {}", id);
                    return updatedContractor;
                })
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + id));
    }

    /**
     * Update contractor password with validation
     */
    public Contractor updatePassword(Long id, String currentPassword, String newPassword) {
        logger.info("Updating password for contractor ID: {}", id);

        Contractor contractor = getContractorById(id)
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + id));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, contractor.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }

        contractor.setPassword(passwordEncoder.encode(newPassword));
        Contractor updatedContractor = contractorRepository.save(contractor);

        logger.info("Password updated successfully for contractor ID: {}", id);
        return updatedContractor;
    }

    /**
     * Get contractor without sensitive data (for public responses)
     */
    public Optional<Contractor> getContractorByIdSafe(Long id) {
        return contractorRepository.findById(id)
                .map(contractor -> {
                    // Clear sensitive data
                    contractor.setPassword(null);
                    return contractor;
                });
    }

    /**
     * Get contractor by email without sensitive data
     */
    public Optional<Contractor> getContractorByEmailSafe(String email) {
        return contractorRepository.findByEmail(email)
                .map(contractor -> {
                    contractor.setPassword(null);
                    return contractor;
                });
    }

    // Standard CRUD operations
    public List<Contractor> getAllContractors() {
        logger.debug("Retrieving all contractors");
        List<Contractor> contractors = contractorRepository.findAll();
        // Clear passwords from response
        contractors.forEach(c -> c.setPassword(null));
        return contractors;
    }

    public List<Contractor> getContractorsByStatus(Contractor.Status status) {
        logger.debug("Retrieving contractors by status: {}", status);
        List<Contractor> contractors = contractorRepository.findByStatus(status);
        contractors.forEach(c -> c.setPassword(null));
        return contractors;
    }

    public List<Contractor> getVerifiedContractors() {
        logger.debug("Retrieving verified contractors");
        List<Contractor> contractors = contractorRepository.findByIsVerifiedTrue();
        contractors.forEach(c -> c.setPassword(null));
        return contractors;
    }

    public Optional<Contractor> getContractorById(Long id) {
        logger.debug("Retrieving contractor by ID: {}", id);
        return contractorRepository.findById(id);
    }

    public Optional<Contractor> getContractorByEmail(String email) {
        logger.debug("Retrieving contractor by email: {}", email);
        return contractorRepository.findByEmail(email);
    }

    /**
     * Search contractors by specialization
     */
    public List<Contractor> getContractorsBySpecialization(String specialization) {
        logger.debug("Searching contractors by specialization: {}", specialization);
        List<Contractor> contractors = contractorRepository.findBySpecialization(specialization);
        contractors.forEach(c -> c.setPassword(null));
        return contractors;
    }

    /**
     * Check if contractor exists and is verified
     */
    public boolean isContractorVerified(Long id) {
        return contractorRepository.findById(id)
                .map(contractor -> contractor.getIsVerified() &&
                        contractor.getStatus() == Contractor.Status.VERIFIED)
                .orElse(false);
    }

    public void deleteContractor(Long id) {
        logger.warn("Deleting contractor ID: {}", id);

        // Check if contractor exists
        if (!contractorRepository.existsById(id)) {
            throw new RuntimeException("Contractor not found with id: " + id);
        }

        // First delete documents to maintain referential integrity
        List<ContractorDocument> documents = documentRepository.findByContractorId(id);
        if (!documents.isEmpty()) {
            documentRepository.deleteAll(documents);
            logger.debug("Deleted {} documents for contractor ID: {}", documents.size(), id);
        }

        contractorRepository.deleteById(id);
        logger.info("Contractor deleted successfully with ID: {}", id);
    }
}