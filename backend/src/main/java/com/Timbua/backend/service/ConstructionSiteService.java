package com.Timbua.backend.service;

import com.Timbua.backend.model.ConstructionSite;
import com.Timbua.backend.model.Contractor;
import com.Timbua.backend.repository.ConstructionSiteRepository;
import com.Timbua.backend.repository.ContractorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConstructionSiteService {

    private final ConstructionSiteRepository siteRepository;
    private final ContractorRepository contractorRepository;

    public ConstructionSiteService(ConstructionSiteRepository siteRepository, 
                                  ContractorRepository contractorRepository) {
        this.siteRepository = siteRepository;
        this.contractorRepository = contractorRepository;
    }

    /**
     * Create a new construction site linked to a contractor
     */
    public ConstructionSite saveSiteWithContractor(Long contractorId, ConstructionSite site) {
        // Find the contractor
        Contractor contractor = contractorRepository.findById(contractorId)
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + contractorId));
        
        // Link the site to the contractor
        site.setContractor(contractor);
        
        return siteRepository.save(site);
    }

    /**
     * Save site without contractor linking (for backward compatibility)
     */
    public ConstructionSite saveSite(ConstructionSite site) {
        return siteRepository.save(site);
    }

    /**
     * Get all construction sites
     */
    public List<ConstructionSite> getAllSites() {
        return siteRepository.findAll();
    }

    /**
     * Get site by ID
     */
    public Optional<ConstructionSite> getSiteById(Long id) {
        return siteRepository.findById(id);
    }

    /**
     * Get all sites for a specific contractor
     */
    public List<ConstructionSite> getSitesByContractorId(Long contractorId) {
        return siteRepository.findByContractorId(contractorId);
    }

    /**
     * Update construction site
     */
    public ConstructionSite updateSite(Long id, ConstructionSite updatedSite) {
        return siteRepository.findById(id)
                .map(existingSite -> {
                    // Update basic fields
                    existingSite.setName(updatedSite.getName());
                    existingSite.setLocation(updatedSite.getLocation());
                    existingSite.setCoordinates(updatedSite.getCoordinates());
                    existingSite.setType(updatedSite.getType());
                    existingSite.setEstimatedCost(updatedSite.getEstimatedCost());
                    existingSite.setStatus(updatedSite.getStatus());
                    existingSite.setStartDate(updatedSite.getStartDate());
                    existingSite.setEndDate(updatedSite.getEndDate());
                    existingSite.setProgress(updatedSite.getProgress());
                    existingSite.setDocuments(updatedSite.getDocuments());
                    
                    // Update contractor if provided in the updated site
                    if (updatedSite.getContractor() != null) {
                        existingSite.setContractor(updatedSite.getContractor());
                    }
                    
                    return siteRepository.save(existingSite);
                })
                .orElseThrow(() -> new RuntimeException("Construction site not found with id: " + id));
    }

    /**
     * Update site progress
     */
    public ConstructionSite updateSiteProgress(Long id, Double progress) {
        return siteRepository.findById(id)
                .map(site -> {
                    if (progress < 0 || progress > 100) {
                        throw new IllegalArgumentException("Progress must be between 0 and 100");
                    }
                    site.setProgress(progress);
                    
                    // Auto-update status based on progress
                    if (progress >= 100) {
                        site.setStatus(ConstructionSite.Status.COMPLETED);
                    } else if (progress > 0) {
                        site.setStatus(ConstructionSite.Status.ACTIVE);
                    }
                    
                    return siteRepository.save(site);
                })
                .orElseThrow(() -> new RuntimeException("Construction site not found with id: " + id));
    }

    /**
     * Update site status
     */
    public ConstructionSite updateSiteStatus(Long id, ConstructionSite.Status status) {
        return siteRepository.findById(id)
                .map(site -> {
                    site.setStatus(status);
                    return siteRepository.save(site);
                })
                .orElseThrow(() -> new RuntimeException("Construction site not found with id: " + id));
    }

    /**
     * Link an existing site to a contractor
     */
    public ConstructionSite linkSiteToContractor(Long siteId, Long contractorId) {
        ConstructionSite site = siteRepository.findById(siteId)
                .orElseThrow(() -> new RuntimeException("Construction site not found with id: " + siteId));
        
        Contractor contractor = contractorRepository.findById(contractorId)
                .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + contractorId));
        
        site.setContractor(contractor);
        return siteRepository.save(site);
    }

    /**
     * Delete construction site
     */
    public void deleteSite(Long id) {
        if (!siteRepository.existsById(id)) {
            throw new RuntimeException("Construction site not found with id: " + id);
        }
        siteRepository.deleteById(id);
    }

    /**
     * Check if site exists
     */
    public boolean siteExists(Long id) {
        return siteRepository.existsById(id);
    }

    /**
     * Get sites by status
     */
    public List<ConstructionSite> getSitesByStatus(ConstructionSite.Status status) {
        return siteRepository.findAll().stream()
                .filter(site -> site.getStatus() == status)
                .toList();
    }
}
