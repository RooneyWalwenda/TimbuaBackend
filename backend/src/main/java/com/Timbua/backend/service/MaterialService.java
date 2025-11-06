package com.Timbua.backend.service;

import com.Timbua.backend.model.Material;
import com.Timbua.backend.model.Supplier;
import com.Timbua.backend.repository.MaterialRepository;
import com.Timbua.backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final SupplierRepository supplierRepository;

    public MaterialService(MaterialRepository materialRepository, SupplierRepository supplierRepository) {
        this.materialRepository = materialRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public Material addMaterial(Long supplierId, Material material) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        if (!supplier.isVerified()) {
            throw new IllegalArgumentException("Supplier must be verified to add materials");
        }
        material.setSupplier(supplier);
        return materialRepository.save(material);
    }

    public List<Material> getMaterialsBySupplier(Long supplierId) {
        return materialRepository.findBySupplierId(supplierId);
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Transactional
    public Material updateMaterial(Long id, Material updated) {
        Material m = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        m.setName(updated.getName());
        m.setCategory(updated.getCategory());
        m.setPrice(updated.getPrice());
        m.setCurrency(updated.getCurrency());
        m.setUnit(updated.getUnit());
        m.setLocation(updated.getLocation());
        m.setMinOrder(updated.getMinOrder());
        m.setAvailable(updated.isAvailable());
        m.setDeliveryTime(updated.getDeliveryTime());
        m.setContact(updated.getContact());
        m.setRating(updated.getRating());
        m.setSupplierLat(updated.getSupplierLat());
        m.setSupplierLng(updated.getSupplierLng());
        return materialRepository.save(m);
    }

    public void deleteMaterial(Long id) {
        materialRepository.deleteById(id);
    }
}