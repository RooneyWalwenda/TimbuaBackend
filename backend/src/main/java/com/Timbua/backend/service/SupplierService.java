package com.Timbua.backend.service;

import com.Timbua.backend.dto.SupplierRequestDTO;
import com.Timbua.backend.dto.SupplierResponseDTO;
import com.Timbua.backend.model.Material;
import com.Timbua.backend.model.Supplier;
import com.Timbua.backend.model.SupplierDocument;
import com.Timbua.backend.repository.MaterialRepository;
import com.Timbua.backend.repository.SupplierDocumentRepository;
import com.Timbua.backend.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierService.class);

    private final SupplierRepository supplierRepository;
    private final SupplierDocumentRepository documentRepository;
    private final MaterialRepository materialRepository;
    private final PasswordEncoder passwordEncoder;

    public SupplierService(SupplierRepository supplierRepository,
                           SupplierDocumentRepository documentRepository,
                           MaterialRepository materialRepository,
                           PasswordEncoder passwordEncoder) {
        this.supplierRepository = supplierRepository;
        this.documentRepository = documentRepository;
        this.materialRepository = materialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Basic supplier registration with password encryption and role assignment
     */
    @Transactional
    public SupplierResponseDTO registerSupplier(SupplierRequestDTO requestDTO) {
        logger.info("Registering new supplier with email: {}", requestDTO.getEmail());

        // Validate input
        validateSupplierRegistration(requestDTO);

        // Convert DTO to Entity
        Supplier supplier = convertToEntity(requestDTO);

        // Encrypt password
        supplier.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        // Set initial status and role
        supplier.setStatus(Supplier.Status.PENDING);
        supplier.setRole(Supplier.Role.SUPPLIER); // Set the role explicitly
        supplier.setVerified(false);
        supplier.setCreatedAt(LocalDateTime.now());

        Supplier savedSupplier = supplierRepository.save(supplier);
        logger.info("Supplier registered successfully with ID: {}", savedSupplier.getId());

        // Return DTO (without password)
        return convertToDTO(savedSupplier);
    }

    /**
     * Complete supplier registration with materials catalog
     */
    @Transactional
    public SupplierResponseDTO registerSupplierWithMaterials(SupplierRequestDTO requestDTO, List<Material> materials) {
        logger.info("Registering supplier with materials, email: {}", requestDTO.getEmail());

        // Validate email and business registration number
        validateSupplierRegistration(requestDTO);

        // Validate that at least one material is provided
        if (materials == null || materials.isEmpty()) {
            throw new IllegalArgumentException("At least one material must be provided during registration");
        }

        // Convert DTO to Entity
        Supplier supplier = convertToEntity(requestDTO);

        // Encrypt password
        supplier.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        // Set supplier status, verification and role
        supplier.setStatus(Supplier.Status.PENDING);
        supplier.setRole(Supplier.Role.SUPPLIER); // Set the role explicitly
        supplier.setVerified(false);
        supplier.setCreatedAt(LocalDateTime.now());

        // Temporarily clear materials to avoid cascade issues
        supplier.setMaterials(new ArrayList<>());

        // Save supplier first to get the auto-generated ID
        Supplier savedSupplier = supplierRepository.save(supplier);

        // Set the supplier for each material and save them
        for (Material material : materials) {
            material.setSupplier(savedSupplier);
            // Set default values if not provided
            if (!material.isAvailable()) {
                material.setAvailable(true);
            }
        }

        // Save all materials
        List<Material> savedMaterials = materialRepository.saveAll(materials);

        // Set the materials back to the supplier for the response
        savedSupplier.setMaterials(savedMaterials);

        logger.info("Supplier registered successfully with {} materials, ID: {}",
                savedMaterials.size(), savedSupplier.getId());

        return convertToDTO(savedSupplier);
    }

    /**
     * Validate supplier registration data from DTO
     */
    private void validateSupplierRegistration(SupplierRequestDTO requestDTO) {
        if (requestDTO.getEmail() == null || requestDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (requestDTO.getPassword() == null || requestDTO.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (requestDTO.getCompanyName() == null || requestDTO.getCompanyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }

        if (requestDTO.getBusinessRegistrationNumber() == null || requestDTO.getBusinessRegistrationNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Business registration number is required");
        }

        // Check uniqueness
        if (supplierRepository.existsByEmail(requestDTO.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Supplier with this email already exists");
        }

        if (supplierRepository.existsByBusinessRegistrationNumber(requestDTO.getBusinessRegistrationNumber())) {
            throw new IllegalArgumentException("Supplier with this business registration number already exists");
        }
    }

    /**
     * Enhanced supplier verification with status validation
     */
    @Transactional
    public SupplierResponseDTO verifySupplier(Long id, boolean approved) {
        logger.info("Verifying supplier ID: {}, approved: {}", id, approved);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // Validate current status
        if (supplier.getStatus() == Supplier.Status.VERIFIED && approved) {
            throw new IllegalStateException("Supplier is already verified");
        }

        if (supplier.getStatus() == Supplier.Status.REJECTED && !approved) {
            throw new IllegalStateException("Supplier is already rejected");
        }

        if (approved) {
            supplier.setStatus(Supplier.Status.VERIFIED);
            supplier.setVerified(true);
            supplier.setVerificationDate(LocalDate.now());
            logger.info("Supplier ID: {} verified successfully", id);
        } else {
            supplier.setStatus(Supplier.Status.REJECTED);
            supplier.setVerified(false);
            logger.warn("Supplier ID: {} rejected", id);
        }

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return convertToDTO(updatedSupplier);
    }

    /**
     * Update supplier status with validation
     */
    public SupplierResponseDTO updateSupplierStatus(Long id, Supplier.Status newStatus) {
        logger.info("Updating supplier ID: {} status to: {}", id, newStatus);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // Validate status transition
        validateSupplierStatusTransition(supplier.getStatus(), newStatus);

        supplier.setStatus(newStatus);

        // Handle verification flags
        if (newStatus == Supplier.Status.VERIFIED) {
            supplier.setVerified(true);
            supplier.setVerificationDate(LocalDate.now());
        } else if (newStatus == Supplier.Status.REJECTED || newStatus == Supplier.Status.SUSPENDED) {
            supplier.setVerified(false);
        }

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return convertToDTO(updatedSupplier);
    }

    /**
     * Validate supplier status transitions
     */
    private void validateSupplierStatusTransition(Supplier.Status currentStatus, Supplier.Status newStatus) {
        // Add business rules for status transitions
        if (currentStatus == Supplier.Status.REJECTED && newStatus == Supplier.Status.VERIFIED) {
            throw new IllegalStateException("Cannot verify a rejected supplier without review");
        }
        // Add more business rules as needed
    }

    /**
     * Update supplier profile (excludes sensitive fields)
     */
    public SupplierResponseDTO updateSupplier(Long id, SupplierRequestDTO requestDTO) {
        logger.info("Updating supplier profile ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // Don't update email, role, or verification status here
        supplier.setCompanyName(requestDTO.getCompanyName());
        supplier.setContactPerson(requestDTO.getContactPerson());
        supplier.setPhone(requestDTO.getPhone());
        supplier.setWebsite(requestDTO.getWebsite());
        supplier.setDescription(requestDTO.getDescription());
        supplier.setYearsInBusiness(requestDTO.getYearsInBusiness());
        supplier.setLogoUrl(requestDTO.getLogoUrl());
        supplier.setBusinessRegistrationNumber(requestDTO.getBusinessRegistrationNumber());

        // Only update password if provided (and not empty)
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().trim().isEmpty()) {
            supplier.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        Supplier savedSupplier = supplierRepository.save(supplier);
        logger.info("Supplier profile updated successfully for ID: {}", id);
        return convertToDTO(savedSupplier);
    }

    /**
     * Update supplier password with validation
     */
    public SupplierResponseDTO updatePassword(Long id, String currentPassword, String newPassword) {
        logger.info("Updating password for supplier ID: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, supplier.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long");
        }

        supplier.setPassword(passwordEncoder.encode(newPassword));
        Supplier updatedSupplier = supplierRepository.save(supplier);

        logger.info("Password updated successfully for supplier ID: {}", id);
        return convertToDTO(updatedSupplier);
    }

    /**
     * Convert DTO to Entity
     */
    private Supplier convertToEntity(SupplierRequestDTO requestDTO) {
        Supplier supplier = new Supplier();
        supplier.setCompanyName(requestDTO.getCompanyName());
        supplier.setBusinessRegistrationNumber(requestDTO.getBusinessRegistrationNumber());
        supplier.setContactPerson(requestDTO.getContactPerson());
        supplier.setEmail(requestDTO.getEmail());
        supplier.setPassword(requestDTO.getPassword()); // Will be hashed in service
        supplier.setPhone(requestDTO.getPhone());
        supplier.setWebsite(requestDTO.getWebsite());
        supplier.setDescription(requestDTO.getDescription());
        supplier.setYearsInBusiness(requestDTO.getYearsInBusiness());
        supplier.setLogoUrl(requestDTO.getLogoUrl());
        return supplier;
    }

    /**
     * Convert Entity to DTO
     */
    private SupplierResponseDTO convertToDTO(Supplier supplier) {
        return new SupplierResponseDTO(supplier);
    }

    // Standard CRUD operations with security - ALL RETURNING DTOS
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getAllSuppliers() {
        logger.debug("Retrieving all suppliers");
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getVerifiedSuppliers() {
        logger.debug("Retrieving verified suppliers");
        List<Supplier> suppliers = supplierRepository.findByIsVerifiedTrue();
        return suppliers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplier(Long id) {
        logger.debug("Retrieving supplier by ID: {}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return convertToDTO(supplier);
    }

    @Transactional(readOnly = true)
    public Optional<SupplierResponseDTO> getSupplierById(Long id) {
        logger.debug("Retrieving supplier by ID: {}", id);
        return supplierRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierEntityById(Long id) {
        logger.debug("Retrieving supplier entity by ID: {}", id);
        return supplierRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierByEmail(String email) {
        logger.debug("Retrieving supplier by email: {}", email);
        return supplierRepository.findByEmail(email);
    }

    /**
     * Search suppliers by material category or type
     */
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getSuppliersByMaterialCategory(String category) {
        logger.debug("Searching suppliers by material category: {}", category);
        List<Supplier> allSuppliers = supplierRepository.findAll();
        List<SupplierResponseDTO> filteredSuppliers = new ArrayList<>();

        for (Supplier supplier : allSuppliers) {
            if (supplier.getMaterials().stream()
                    .anyMatch(material -> material.getCategory() != null &&
                            material.getCategory().equalsIgnoreCase(category))) {
                filteredSuppliers.add(convertToDTO(supplier));
            }
        }

        return filteredSuppliers;
    }

    /**
     * Check if supplier exists and is verified
     */
    @Transactional(readOnly = true)
    public boolean isSupplierVerified(Long id) {
        return supplierRepository.findById(id)
                .map(supplier -> supplier.isVerified() &&
                        supplier.getStatus() == Supplier.Status.VERIFIED)
                .orElse(false);
    }

    /**
     * Get suppliers with specific material availability
     */
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> getSuppliersWithAvailableMaterials() {
        logger.debug("Retrieving suppliers with available materials");
        List<Supplier> suppliers = supplierRepository.findAll();
        List<SupplierResponseDTO> suppliersWithAvailableMaterials = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            boolean hasAvailableMaterials = supplier.getMaterials().stream()
                    .anyMatch(Material::isAvailable);

            if (hasAvailableMaterials) {
                suppliersWithAvailableMaterials.add(convertToDTO(supplier));
            }
        }

        return suppliersWithAvailableMaterials;
    }

    // Document management
    public SupplierDocument saveDocument(SupplierDocument doc) {
        logger.debug("Saving document for supplier ID: {}", doc.getSupplier().getId());
        SupplierDocument savedDocument = documentRepository.save(doc);
        logger.info("Document saved successfully with ID: {}", savedDocument.getId());
        return savedDocument;
    }

    public List<SupplierDocument> getDocumentsForSupplier(Long supplierId) {
        logger.debug("Retrieving documents for supplier ID: {}", supplierId);
        return documentRepository.findBySupplierId(supplierId);
    }

    /**
     * Add material to existing supplier
     */
    @Transactional
    public Material addMaterialToSupplier(Long supplierId, Material material) {
        logger.info("Adding material to supplier ID: {}", supplierId);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierId));

        material.setSupplier(supplier);
        if (!material.isAvailable()) {
            material.setAvailable(true); // Default to available
        }

        Material savedMaterial = materialRepository.save(material);
        logger.info("Material added successfully with ID: {} to supplier ID: {}",
                savedMaterial.getId(), supplierId);
        return savedMaterial;
    }

    /**
     * Get materials for a specific supplier
     */
    @Transactional(readOnly = true)
    public List<Material> getSupplierMaterials(Long supplierId) {
        logger.debug("Retrieving materials for supplier ID: {}", supplierId);
        // Validate supplier exists
        if (!supplierRepository.existsById(supplierId)) {
            throw new RuntimeException("Supplier not found with id: " + supplierId);
        }
        return materialRepository.findBySupplierId(supplierId);
    }

    /**
     * Delete supplier with cleanup
     */
    @Transactional
    public void deleteSupplier(Long id) {
        logger.warn("Deleting supplier ID: {}", id);

        // Check if supplier exists
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        // First delete materials to maintain referential integrity
        List<Material> materials = materialRepository.findBySupplierId(id);
        if (!materials.isEmpty()) {
            materialRepository.deleteAll(materials);
            logger.debug("Deleted {} materials for supplier ID: {}", materials.size(), id);
        }

        // Delete documents
        List<SupplierDocument> documents = documentRepository.findBySupplierId(id);
        if (!documents.isEmpty()) {
            documentRepository.deleteAll(documents);
            logger.debug("Deleted {} documents for supplier ID: {}", documents.size(), id);
        }

        supplierRepository.deleteById(id);
        logger.info("Supplier deleted successfully with ID: {}", id);
    }
}
