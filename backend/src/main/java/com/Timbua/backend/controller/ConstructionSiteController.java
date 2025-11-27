package com.Timbua.backend.controller;

import com.Timbua.backend.model.ConstructionSite;
import com.Timbua.backend.service.ConstructionSiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@CrossOrigin(origins = "*")
public class ConstructionSiteController {

    private final ConstructionSiteService service;

    public ConstructionSiteController(ConstructionSiteService service) {
        this.service = service;
    }

    // ✅ Create site linked to contractor (USE THIS)
    @PostMapping("/contractor/{contractorId}")
    public ResponseEntity<ConstructionSite> createSiteForContractor(
            @PathVariable Long contractorId, 
            @RequestBody ConstructionSite site) {
        ConstructionSite savedSite = service.saveSiteWithContractor(contractorId, site);
        return ResponseEntity.ok(savedSite);
    }

    // Get all sites for a specific contractor
    @GetMapping("/contractor/{contractorId}")
    public ResponseEntity<List<ConstructionSite>> getSitesByContractor(@PathVariable Long contractorId) {
        List<ConstructionSite> sites = service.getSitesByContractorId(contractorId);
        return ResponseEntity.ok(sites);
    }

    // Get all sites
    @GetMapping
    public ResponseEntity<List<ConstructionSite>> getAllSites() {
        List<ConstructionSite> sites = service.getAllSites();
        return ResponseEntity.ok(sites);
    }

    // Get site by ID
    @GetMapping("/{id}")
    public ResponseEntity<ConstructionSite> getSiteById(@PathVariable Long id) {
        return service.getSiteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update site
    @PutMapping("/{id}")
    public ResponseEntity<ConstructionSite> updateSite(@PathVariable Long id, @RequestBody ConstructionSite site) {
        ConstructionSite updatedSite = service.updateSite(id, site);
        return ResponseEntity.ok(updatedSite);
    }

    // Update site progress
    @PatchMapping("/{id}/progress")
    public ResponseEntity<ConstructionSite> updateSiteProgress(@PathVariable Long id, @RequestParam Double progress) {
        ConstructionSite updatedSite = service.updateSiteProgress(id, progress);
        return ResponseEntity.ok(updatedSite);
    }

    // Update site status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ConstructionSite> updateSiteStatus(
            @PathVariable Long id, 
            @RequestParam ConstructionSite.Status status) {
        ConstructionSite updatedSite = service.updateSiteStatus(id, status);
        return ResponseEntity.ok(updatedSite);
    }

    // Link existing site to contractor
    @PatchMapping("/{siteId}/link-contractor/{contractorId}")
    public ResponseEntity<ConstructionSite> linkSiteToContractor(
            @PathVariable Long siteId, 
            @PathVariable Long contractorId) {
        ConstructionSite updatedSite = service.linkSiteToContractor(siteId, contractorId);
        return ResponseEntity.ok(updatedSite);
    }

    // Delete site
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        service.deleteSite(id);
        return ResponseEntity.noContent().build();
    }
}
