
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

    @PostMapping
    public ResponseEntity<ConstructionSite> createSite(@RequestBody ConstructionSite site) {
        return ResponseEntity.ok(service.saveSite(site));
    }

    @GetMapping
    public ResponseEntity<List<ConstructionSite>> getAllSites() {
        return ResponseEntity.ok(service.getAllSites());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConstructionSite> getSiteById(@PathVariable Long id) {
        return service.getSiteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConstructionSite> updateSite(@PathVariable Long id, @RequestBody ConstructionSite site) {
        return ResponseEntity.ok(service.updateSite(id, site));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        service.deleteSite(id);
        return ResponseEntity.noContent().build();
    }
}
