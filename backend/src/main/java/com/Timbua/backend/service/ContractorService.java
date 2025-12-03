package com.Timbua.backend.service;

import com.Timbua.backend.dto.ContractorRequestDTO;
import com.Timbua.backend.dto.ContractorResponseDTO;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ContractorResponseDTO registerContractor(ContractorRequestDTO requestDTO) {
        logger.info("Registering new contractor with email: {}", requestDTO.getEmail());

        // Validate input
        validateContractorRegistration(requestDTO);

        // Convert DTO to Entity
        Contractor contractor = convertToEntity(requestDTO);

        // Encrypt password
        contractor.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        // Set initial status and role
        contractor.setStatus(Contractor.Status.PENDING);
        contractor.setRole(Contractor.Role.CONTRACTOR); // Set the role explicitly
        contractor.setIsVerified(false);
        contractor.setRegistrationDate(LocalDateTime.now());

        Contractor savedContractor = contractorRepository.save(contractor);
        logger.info("Contractor registered successfully with ID: {}", savedContractor.getId());

        // Return DTO (without password)
        return convertToDTO(savedContractor);
    }

    /**
     * Validate contractor registration data from DTO
     */
    private void validateContractorRegistration(ContractorRequestDTO requestDTO) {
        if (requestDTO.getEmail() == null || requestDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (requestDTO.getPassword() == null || requestDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (requestDTO.getCompanyName() == null || requestDTO.getCompanyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }

        if (requestDTO.getContactPerson() == null || requestDTO.getContactPerson().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact person is required");
        }

        // Check uniqueness
        if (contractorRepository.existsByEmail(requestDTO.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Contractor with this email already exists");
        }

        if (requestDTO.getBusinessRegistrationNumber() != null &&
                contractorRepository.existsByBusinessRegistrationNumber(requestDTO.getBusinessRegistrationNumber())) {
            throw new IllegalArgumentException("Contractor with this business registration number already exists");
        }
    }

    /**
     * Admin verification with status management
     */
    public ContractorResponseDTO verifyContractor(Long id, boolean approved, String remarks) {
        logger.info("Verifying contractor ID: {}, approved: {}, remarks: {}", id, approved, remarks);

        Contractor contractor = contractorRepository.findById(id)
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

        Contractor updatedContractor = contractorRepository.save(contractor);
        return convertToDTO(updatedContractor);
    }

    /**
     * Update contractor status with validation
     */
    public ContractorResponseDTO updateContractorStatus(Long id, Contractor.Status newStatus) {
        logger.info("Updating contractor ID: {} status to: {}", id, newStatus);

        Contractor contractor = contractorRepository.findById(id)
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

        Contractor updatedContractor = contractorRepository.save(contractor);
        return convertToDTO(updatedContractor);
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

        Contractor contractor = contractorRepository.findById(contractorId)
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
    public ContractorResponseDTO updateContractor(Long id, ContractorRequestDTO requestDTO) {
        logger.info("Updating contractor profile ID: {}", id);

        Contractor contractor = contractorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + id));

        // Don't update email, role, or verification status here
        contractor.setCompanyName(requestDTO.getCompanyName());
        contractor.setContactPerson(requestDTO.getContactPerson());
        contractor.setPhoneNumber(requestDTO.getPhoneNumber());
        contractor.setPhysicalAddress(requestDTO.getPhysicalAddress());
        contractor.setSpecialization(requestDTO.getSpecialization());
        contractor.setYearsOfExperience(requestDTO.getYearsOfExperience());
        contractor.setLicenseNumber(requestDTO.getLicenseNumber());
        contractor.setBusinessRegistrationNumber(requestDTO.getBusinessRegistrationNumber());

        // Only update password if provided (and not empty)
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().trim().isEmpty()) {
            contractor.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        Contractor updatedContractor = contractorRepository.save(contractor);
        logger.info("Contractor profile updated successfully for ID: {}", id);

        return convertToDTO(updatedContractor);
    }

    /**
     * Update contractor password with validation
     */
    public ContractorResponseDTO updatePassword(Long id, String currentPassword, String newPassword) {
        logger.info("Updating password for contractor ID: {}", id);

        Contractor contractor = contractorRepository.findById(id)
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
        return convertToDTO(updatedContractor);
    }

    /**
     * Convert DTO to Entity
     */
    private Contractor convertToEntity(ContractorRequestDTO requestDTO) {
        Contractor contractor = new Contractor();
        contractor.setCompanyName(requestDTO.getCompanyName());
        contractor.setEmail(requestDTO.getEmail());
        contractor.setPassword(requestDTO.getPassword()); // Will be hashed in service
        contractor.setContactPerson(requestDTO.getContactPerson());
        contractor.setPhoneNumber(requestDTO.getPhoneNumber());
        contractor.setBusinessRegistrationNumber(requestDTO.getBusinessRegistrationNumber());
        contractor.setPhysicalAddress(requestDTO.getPhysicalAddress());
        contractor.setSpecialization(requestDTO.getSpecialization());
        contractor.setYearsOfExperience(requestDTO.getYearsOfExperience());
        contractor.setLicenseNumber(requestDTO.getLicenseNumber());
        return contractor;
    }

    /**
     * Convert Entity to DTO
     */
    private ContractorResponseDTO convertToDTO(Contractor contractor) {
        return new ContractorResponseDTO(contractor);
    }

    // Standard CRUD operations - ALL RETURNING DTOS
    @Transactional(readOnly = true)
    public List<ContractorResponseDTO> getAllContractors() {
        logger.debug("Retrieving all contractors");
        List<Contractor> contractors = contractorRepository.findAll();
        return contractors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContractorResponseDTO> getContractorsByStatus(Contractor.Status status) {
        logger.debug("Retrieving contractors by status: {}", status);
        List<Contractor> contractors = contractorRepository.findByStatus(status);
        return contractors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContractorResponseDTO> getVerifiedContractors() {
        logger.debug("Retrieving verified contractors");
        List<Contractor> contractors = contractorRepository.findByIsVerifiedTrue();
        return contractors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ContractorResponseDTO> getContractorById(Long id) {
        logger.debug("Retrieving contractor by ID: {}", id);
        return contractorRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<Contractor> getContractorEntityById(Long id) {
        logger.debug("Retrieving contractor entity by ID: {}", id);
        return contractorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Contractor> getContractorByEmail(String email) {
        logger.debug("Retrieving contractor by email: {}", email);
        return contractorRepository.findByEmail(email);
    }

    /**
     * Search contractors by specialization
     */
    @Transactional(readOnly = true)
    public List<ContractorResponseDTO> getContractorsBySpecialization(String specialization) {
        logger.debug("Searching contractors by specialization: {}", specialization);
        List<Contractor> contractors = contractorRepository.findBySpecialization(specialization);
        return contractors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if contractor exists and is verified
     */
    @Transactional(readOnly = true)
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
