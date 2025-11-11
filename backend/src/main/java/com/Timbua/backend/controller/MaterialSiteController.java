package com.Timbua.backend.controller;

import com.Timbua.backend.model.MaterialSite;
import com.Timbua.backend.service.MaterialSiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/material-sites")
@CrossOrigin(origins = "*")
@Tag(name = "Material Sites", description = "APIs for managing construction material sites and sources")
public class MaterialSiteController {

    @Autowired
    private MaterialSiteService materialSiteService;

    @Operation(summary = "Get all material sites", description = "Retrieve a list of all construction material sites with their details")
    @GetMapping
    public ResponseEntity<List<MaterialSite>> getAllMaterialSites() {
        List<MaterialSite> materialSites = materialSiteService.getAllMaterialSites();
        return ResponseEntity.ok(materialSites);
    }

    @Operation(summary = "Get material site by ID", description = "Retrieve a specific material site by its unique ID")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialSite> getMaterialSiteById(
            @Parameter(description = "ID of the material site to retrieve") @PathVariable Long id) {
        Optional<MaterialSite> materialSite = materialSiteService.getMaterialSiteById(id);
        return materialSite.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new material site", description = "Add a new construction material site to the database")
    @PostMapping
    public ResponseEntity<MaterialSite> createMaterialSite(
            @Parameter(description = "Material site object to create") @RequestBody MaterialSite materialSite) {
        MaterialSite savedMaterialSite = materialSiteService.saveMaterialSite(materialSite);
        return ResponseEntity.ok(savedMaterialSite);
    }

    @Operation(summary = "Create multiple material sites", description = "Add multiple construction material sites in batch")
    @PostMapping("/batch")
    public ResponseEntity<List<MaterialSite>> createMaterialSites(
            @Parameter(description = "List of material site objects to create") @RequestBody List<MaterialSite> materialSites) {
        List<MaterialSite> savedMaterialSites = materialSiteService.saveAllMaterialSites(materialSites);
        return ResponseEntity.ok(savedMaterialSites);
    }

    @Operation(summary = "Update material site", description = "Update an existing material site's information")
    @PutMapping("/{id}")
    public ResponseEntity<MaterialSite> updateMaterialSite(
            @Parameter(description = "ID of the material site to update") @PathVariable Long id,
            @Parameter(description = "Updated material site object") @RequestBody MaterialSite materialSite) {
        if (!materialSiteService.getMaterialSiteById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        materialSite.setId(id);
        MaterialSite updatedMaterialSite = materialSiteService.saveMaterialSite(materialSite);
        return ResponseEntity.ok(updatedMaterialSite);
    }

    @Operation(summary = "Delete material site", description = "Remove a material site from the database")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterialSite(
            @Parameter(description = "ID of the material site to delete") @PathVariable Long id) {
        if (!materialSiteService.getMaterialSiteById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        materialSiteService.deleteMaterialSite(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Search by material", description = "Find material sites by material type (e.g., sand, bricks, cement)")
    @GetMapping("/search/material")
    public ResponseEntity<List<MaterialSite>> getMaterialSitesByMaterial(
            @Parameter(description = "Material type to search for") @RequestParam String material) {
        List<MaterialSite> materialSites = materialSiteService.getMaterialSitesByMaterial(material);
        return ResponseEntity.ok(materialSites);
    }

    @Operation(summary = "Search by location", description = "Find material sites by location name")
    @GetMapping("/search/location")
    public ResponseEntity<List<MaterialSite>> getMaterialSitesByLocation(
            @Parameter(description = "Location to search for") @RequestParam String location) {
        List<MaterialSite> materialSites = materialSiteService.getMaterialSitesByLocation(location);
        return ResponseEntity.ok(materialSites);
    }

    @Operation(summary = "Search by owner", description = "Find material sites by owner name or type")
    @GetMapping("/search/owner")
    public ResponseEntity<List<MaterialSite>> getMaterialSitesByOwner(
            @Parameter(description = "Owner to search for") @RequestParam String owner) {
        List<MaterialSite> materialSites = materialSiteService.getMaterialSitesByOwner(owner);
        return ResponseEntity.ok(materialSites);
    }

    @Operation(summary = "Search by county", description = "Find material sites by county name")
    @GetMapping("/search/county")
    public ResponseEntity<List<MaterialSite>> getMaterialSitesByCounty(
            @Parameter(description = "County to search for") @RequestParam String county) {
        List<MaterialSite> materialSites = materialSiteService.getMaterialSitesByCounty(county);
        return ResponseEntity.ok(materialSites);
    }

    @Operation(summary = "Search by sub-county", description = "Find material sites by sub-county name")
    @GetMapping("/search/sub-county")
    public ResponseEntity<List<MaterialSite>> getMaterialSitesBySubCounty(
            @Parameter(description = "Sub-county to search for") @RequestParam String subCounty) {
        List<MaterialSite> materialSites = materialSiteService.getMaterialSitesBySubCounty(subCounty);
        return ResponseEntity.ok(materialSites);
    }

    @Operation(summary = "Search by county and sub-county", description = "Find material sites by both county and sub-county")
    @GetMapping("/search/county-sub-county")
    public ResponseEntity<List<MaterialSite>> getMaterialSitesByCountyAndSubCounty(
            @Parameter(description = "County to search for") @RequestParam String county,
            @Parameter(description = "Sub-county to search for") @RequestParam String subCounty) {
        List<MaterialSite> materialSites = materialSiteService.getMaterialSitesByCountyAndSubCounty(county, subCounty);
        return ResponseEntity.ok(materialSites);
    }

    @Operation(summary = "Get all material types", description = "Retrieve a list of all distinct material types available")
    @GetMapping("/materials")
    public ResponseEntity<List<String>> getDistinctMaterials() {
        List<String> materials = materialSiteService.getDistinctMaterials();
        return ResponseEntity.ok(materials);
    }

    @Operation(summary = "Get all locations", description = "Retrieve a list of all distinct locations where materials are available")
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getDistinctLocations() {
        List<String> locations = materialSiteService.getDistinctLocations();
        return ResponseEntity.ok(locations);
    }

    @Operation(summary = "Get all counties", description = "Retrieve a list of all distinct counties")
    @GetMapping("/counties")
    public ResponseEntity<List<String>> getDistinctCounties() {
        List<String> counties = materialSiteService.getDistinctCounties();
        return ResponseEntity.ok(counties);
    }

    @Operation(summary = "Get all sub-counties", description = "Retrieve a list of all distinct sub-counties")
    @GetMapping("/sub-counties")
    public ResponseEntity<List<String>> getDistinctSubCounties() {
        List<String> subCounties = materialSiteService.getDistinctSubCounties();
        return ResponseEntity.ok(subCounties);
    }

    @Operation(summary = "Get sub-counties by county", description = "Retrieve a list of sub-counties for a specific county")
    @GetMapping("/sub-counties/by-county")
    public ResponseEntity<List<String>> getSubCountiesByCounty(
            @Parameter(description = "County to get sub-counties for") @RequestParam String county) {
        List<String> subCounties = materialSiteService.getSubCountiesByCounty(county);
        return ResponseEntity.ok(subCounties);
    }
}
