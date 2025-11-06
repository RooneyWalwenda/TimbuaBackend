package com.Timbua.backend.service;

import com.Timbua.backend.model.Contractor;
import com.Timbua.backend.model.ContractorDocument;
import com.Timbua.backend.repository.ContractorDocumentRepository;
import com.Timbua.backend.repository.ContractorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContractorService {

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

    // Self-registration (for contractors signing up)
    public Contractor registerContractor(Contractor contractor) {
        // Validate uniqueness
        if (contractorRepository.existsByEmail(contractor.getEmail())) {
            throw new IllegalArgumentException("Contractor with this email already exists");
        }
        if (contractorRepository.existsByBusinessRegistrationNumber(contractor.getBusinessRegistrationNumber())) {
            throw new IllegalArgumentException("Contractor with this business registration number already exists");
        }

        // Encrypt password
        contractor.setPassword(passwordEncoder.encode(contractor.getPassword()));

        // Set initial status
        contractor.setStatus(Contractor.Status.PENDING);
        contractor.setIsVerified(false);

        return contractorRepository.save(contractor);
    }

    // Admin verification
    public Contractor verifyContractor(Long id, boolean approved, String remarks) {
        Contractor contractor = getContractorById(id)
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + id));

        if (approved) {
            contractor.setStatus(Contractor.Status.VERIFIED);
            contractor.setIsVerified(true);
            contractor.setVerificationDate(LocalDate.now());
        } else {
            contractor.setStatus(Contractor.Status.REJECTED);
            contractor.setIsVerified(false);
        }

        return contractorRepository.save(contractor);
    }

    // Document management
    public ContractorDocument uploadDocument(Long contractorId, ContractorDocument document) {
        Contractor contractor = getContractorById(contractorId)
                .orElseThrow(() -> new RuntimeException("Contractor not found"));

        document.setContractor(contractor);
        return documentRepository.save(document);
    }

    public List<ContractorDocument> getContractorDocuments(Long contractorId) {
        return documentRepository.findByContractorId(contractorId);
    }

    public ContractorDocument updateDocumentStatus(Long documentId, ContractorDocument.Status status) {
        ContractorDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setStatus(status);
        return documentRepository.save(document);
    }

    // Standard CRUD operations
    public List<Contractor> getAllContractors() {
        return contractorRepository.findAll();
    }

    public List<Contractor> getContractorsByStatus(Contractor.Status status) {
        return contractorRepository.findByStatus(status);
    }

    public List<Contractor> getVerifiedContractors() {
        return contractorRepository.findByIsVerifiedTrue();
    }

    public Optional<Contractor> getContractorById(Long id) {
        return contractorRepository.findById(id);
    }

    public Optional<Contractor> getContractorByEmail(String email) {
        return contractorRepository.findByEmail(email);
    }

    public Contractor updateContractor(Long id, Contractor contractorDetails) {
        return contractorRepository.findById(id)
                .map(existingContractor -> {
                    // Don't update email and password here
                    existingContractor.setCompanyName(contractorDetails.getCompanyName());
                    existingContractor.setContactPerson(contractorDetails.getContactPerson());
                    existingContractor.setPhoneNumber(contractorDetails.getPhoneNumber());
                    existingContractor.setPhysicalAddress(contractorDetails.getPhysicalAddress());
                    existingContractor.setSpecialization(contractorDetails.getSpecialization());
                    existingContractor.setYearsOfExperience(contractorDetails.getYearsOfExperience());
                    existingContractor.setLicenseNumber(contractorDetails.getLicenseNumber());
                    return contractorRepository.save(existingContractor);
                })
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + id));
    }

    public void deleteContractor(Long id) {
        contractorRepository.deleteById(id);
    }
}