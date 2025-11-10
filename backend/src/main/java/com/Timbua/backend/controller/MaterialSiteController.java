 package com.Timbua.backend.controller;

import com.Timbua.backend.model.MaterialSite;
import com.Timbua.backend.service.MaterialSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/material-sites")
@CrossOrigin(origins = "*")
public class MaterialSiteController {

    @Autowired
    private MaterialSiteService materialSiteService;

    @GetMapping
    public ResponseEntity<List<MaterialSite>> getAllMaterialSites() {
        List<MaterialSite> materialSites = materialSiteService.getAllMaterialSites();
        return ResponseEntity.ok(materialSites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialSite> getMaterialSiteById(@PathVariable Long id) {
        Optional<MaterialSite> materialSite = materialSiteService.getMaterialSiteById(id);
        return materialSite.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MaterialSite> createMaterialSite(@RequestBody MaterialSite materialSite) {
        MaterialSite savedMaterialSite = materialSiteService.saveMaterialSite(materialSite);
        return ResponseEntity.ok(savedMaterialSite);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<MaterialSite>> createMaterialSites(@RequestBody List<MaterialSite> materialSites) {
        List<MaterialSite> savedMaterialSites = materialSiteService.saveAllMaterialSites(materialSites);
        return ResponseEntity.ok(savedMaterialSites);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialSite> updateMaterialSite(@PathVariable Long id, @RequestBody MaterialSite materialSite) {
        if (!materialSiteService.getMaterialSiteById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        materialSite.setId(id);
        MaterialSite updatedMaterialSite = materialSiteService.saveMaterialSite(materialSite);
        return ResponseEntity.ok(updatedMaterialSite);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterialSite(@PathVariable Long id) {
        if (!materialSiteService.getMaterialSiteById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        materialSiteService.deleteMaterialSite(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/material")
    public ResponseEntity<List<MaterialSite>> getMaterialSitesByMaterial(@RequestParam String material) {
        List<MaterialSite> materialSites = materialSiteService.getMaterialSitesByMaterial(material);
        return ResponseEntity.ok(materialSites);
    }

    @GetMapping("/search/location")
    public ResponseEntity<List<MaterialSite>> getMaterialSitesByLocation(@RequestParam String location) {
        List<MaterialSite> materialSites = materialSiteService.getMaterialSitesByLocation(location);
        return ResponseEntity.ok(materialSites);
    }

    @GetMapping("/search/owner")
    public ResponseEntity<List<MaterialSite>> getMaterialSitesByOwner(@RequestParam String owner) {
        List<MaterialSite> materialSites = materialSiteService.getMaterialSitesByOwner(owner);
        return ResponseEntity.ok(materialSites);
    }

    @GetMapping("/materials")
    public ResponseEntity<List<String>> getDistinctMaterials() {
        List<String> materials = materialSiteService.getDistinctMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/locations")
    public ResponseEntity<List<String>> getDistinctLocations() {
        List<String> locations = materialSiteService.getDistinctLocations();
        return ResponseEntity.ok(locations);
    }
}
