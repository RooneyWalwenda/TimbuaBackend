package com.Timbua.backend.service;

import com.Timbua.backend.model.MaterialSite;
import com.Timbua.backend.repository.MaterialSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialSiteService {

    @Autowired
    private MaterialSiteRepository materialSiteRepository;

    public List<MaterialSite> getAllMaterialSites() {
        return materialSiteRepository.findAll();
    }

    public Optional<MaterialSite> getMaterialSiteById(Long id) {
        return materialSiteRepository.findById(id);
    }

    public MaterialSite saveMaterialSite(MaterialSite materialSite) {
        return materialSiteRepository.save(materialSite);
    }

    public List<MaterialSite> saveAllMaterialSites(List<MaterialSite> materialSites) {
        return materialSiteRepository.saveAll(materialSites);
    }

    public void deleteMaterialSite(Long id) {
        materialSiteRepository.deleteById(id);
    }

    public List<MaterialSite> getMaterialSitesByMaterial(String material) {
        return materialSiteRepository.findByMaterialContainingIgnoreCase(material);
    }

    public List<MaterialSite> getMaterialSitesByLocation(String location) {
        return materialSiteRepository.findByMaterialLocationContainingIgnoreCase(location);
    }

    public List<MaterialSite> getMaterialSitesByOwner(String owner) {
        return materialSiteRepository.findByOwnerOfMaterialContainingIgnoreCase(owner);
    }

    public List<String> getDistinctMaterials() {
        return materialSiteRepository.findDistinctMaterials();
    }

    public List<String> getDistinctLocations() {
        return materialSiteRepository.findDistinctLocations();
    }
}