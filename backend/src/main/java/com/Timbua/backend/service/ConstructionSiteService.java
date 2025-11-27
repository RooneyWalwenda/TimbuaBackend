package com.Timbua.backend.service;

import com.Timbua.backend.model.ConstructionSite;
import com.Timbua.backend.model.Contractor;
import com.Timbua.backend.repository.ConstructionSiteRepository;
import com.Timbua.backend.repository.ContractorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ConstructionSiteService {

    private final ConstructionSiteRepository repository;
    private final ContractorRepository contractorRepository; // ADDED

    public ConstructionSiteService(ConstructionSiteRepository repository, 
                                  ContractorRepository contractorRepository) { // UPDATED
        this.repository = repository;
        this.contractorRepository = contractorRepository; // ADDED
    }

    // NEW METHOD: Save site with contractor relationship
    public ConstructionSite saveSiteWithContractor(Long contractorId, ConstructionSite site) {
        Contractor contractor = contractorRepository.findById(contractorId)
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + contractorId));
        
        site.setContractor(contractor); // Use setContractor, not setContractorId
        return repository.save(site);
    }

    // Keep existing method for backward compatibility
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

    // FIXED: Remove setContractorId call
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
            
            // FIXED: Use setContractor instead of setContractorId
            if (updated.getContractor() != null) {
                site.setContractor(updated.getContractor());
            }
            
            site.setDocuments(updated.getDocuments());
            return repository.save(site);
        }).orElseThrow(() -> new RuntimeException("Construction site not found"));
    }

    // NEW METHOD: Get sites by contractor
    public List<ConstructionSite> getSitesByContractorId(Long contractorId) {
        return repository.findByContractorId(contractorId);
    }
}
