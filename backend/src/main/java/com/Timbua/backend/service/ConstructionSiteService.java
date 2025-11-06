
package com.Timbua.backend.service;

import com.Timbua.backend.model.ConstructionSite;
import com.Timbua.backend.repository.ConstructionSiteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ConstructionSiteService {

    private final ConstructionSiteRepository repository;

    public ConstructionSiteService(ConstructionSiteRepository repository) {
        this.repository = repository;
    }

    public ConstructionSite saveSite(ConstructionSite site) {
        return repository.save(site);
    }

    public List<ConstructionSite> getAllSites() {
        return repository.findAll();
    }

    public Optional<ConstructionSite> getSiteById(Long id) {
        return repository.findById(id);
    }

    public void deleteSite(Long id) {
        repository.deleteById(id);
    }

    public ConstructionSite updateSite(Long id, ConstructionSite updated) {
        return repository.findById(id).map(site -> {
            site.setName(updated.getName());
            site.setLocation(updated.getLocation());
            site.setCoordinates(updated.getCoordinates());
            site.setType(updated.getType());
            site.setEstimatedCost(updated.getEstimatedCost());
            site.setStatus(updated.getStatus());
            site.setStartDate(updated.getStartDate());
            site.setEndDate(updated.getEndDate());
            site.setProgress(updated.getProgress());
            site.setContractorId(updated.getContractorId());
            site.setDocuments(updated.getDocuments());
            return repository.save(site);
        }).orElseThrow(() -> new RuntimeException("Construction site not found"));
    }
}
