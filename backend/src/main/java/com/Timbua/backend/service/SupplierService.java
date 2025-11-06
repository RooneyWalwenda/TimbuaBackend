package com.Timbua.backend.service;

import com.Timbua.backend.model.Material;
import com.Timbua.backend.model.Supplier;
import com.Timbua.backend.model.SupplierDocument;
import com.Timbua.backend.repository.MaterialRepository;
import com.Timbua.backend.repository.SupplierDocumentRepository;
import com.Timbua.backend.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierDocumentRepository documentRepository;
    private final MaterialRepository materialRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository,
                           SupplierDocumentRepository documentRepository,
                           MaterialRepository materialRepository) {
        this.supplierRepository = supplierRepository;
        this.documentRepository = documentRepository;
        this.materialRepository = materialRepository;
    }

    // Keep your existing registerSupplier method
    @Transactional
    public Supplier registerSupplier(Supplier supplier) {
        if (supplierRepository.existsByEmail(supplier.getEmail())) {
            throw new IllegalArgumentException("Supplier with this email already exists");
        }
        if (supplierRepository.existsByBusinessRegistrationNumber(supplier.getBusinessRegistrationNumber())) {
            throw new IllegalArgumentException("Supplier with this business registration number already exists");
        }
        supplier.setStatus(Supplier.Status.PENDING);
        supplier.setVerified(false);
        return supplierRepository.save(supplier);
    }

    // NEW METHOD: Register supplier with materials
    @Transactional
    public Supplier registerSupplierWithMaterials(Supplier supplier) {
        // Validate email and business registration number
        if (supplierRepository.existsByEmail(supplier.getEmail())) {
            throw new IllegalArgumentException("Supplier with this email already exists");
        }
        if (supplierRepository.existsByBusinessRegistrationNumber(supplier.getBusinessRegistrationNumber())) {
            throw new IllegalArgumentException("Supplier with this business registration number already exists");
        }

        // Validate that at least one material is provided
        if (supplier.getMaterials() == null || supplier.getMaterials().isEmpty()) {
            throw new IllegalArgumentException("At least one material must be provided during registration");
        }

        // Set supplier status and verification
        supplier.setStatus(Supplier.Status.PENDING);
        supplier.setVerified(false);

        // Temporarily store the materials and clear them from supplier to avoid cascade issues
        List<Material> materials = new ArrayList<>(supplier.getMaterials());
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

        return savedSupplier;
    }

    public Supplier getSupplier(Long id) {
        return supplierRepository.findById(id).orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<Supplier> getVerifiedSuppliers() {
        return supplierRepository.findByIsVerifiedTrue();
    }

    @Transactional
    public Supplier verifySupplier(Long id, boolean approved) {
        Supplier supplier = getSupplier(id);
        if (approved) {
            supplier.setStatus(Supplier.Status.VERIFIED);
            supplier.setVerified(true);
            supplier.setVerificationDate(LocalDate.now());
        } else {
            supplier.setStatus(Supplier.Status.REJECTED);
            supplier.setVerified(false);
        }
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Long id, Supplier updated) {
        Supplier supplier = getSupplier(id);
        supplier.setCompanyName(updated.getCompanyName());
        supplier.setContactPerson(updated.getContactPerson());
        supplier.setPhone(updated.getPhone());
        supplier.setWebsite(updated.getWebsite());
        supplier.setDescription(updated.getDescription());
        supplier.setYearsInBusiness(updated.getYearsInBusiness());
        supplier.setLogoUrl(updated.getLogoUrl());
        return supplierRepository.save(supplier);
    }

    // Documents
    public SupplierDocument saveDocument(SupplierDocument doc) {
        return documentRepository.save(doc);
    }

    public List<SupplierDocument> getDocumentsForSupplier(Long supplierId) {
        return documentRepository.findBySupplierId(supplierId);
    }
}