package com.Timbua.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "construction_sites")
public class ConstructionSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @Embedded
    private Coordinates coordinates;

    private String type;
    private Double estimatedCost;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate startDate;
    private LocalDate endDate;
    private Double progress;

    // Updated: Replace contractorId with proper relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id")
    @JsonIgnoreProperties({"constructionSites", "documents", "quotationRequests"})
    private Contractor contractor;

    @ElementCollection
    @CollectionTable(name = "construction_documents", joinColumns = @JoinColumn(name = "site_id"))
    @Column(name = "document_url")
    private List<String> documents;

    public enum Status {
        PLANNING, ACTIVE, COMPLETED, ON_HOLD
    }

    public ConstructionSite() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }

    // Updated: Contractor relationship getter and setter
    public Contractor getContractor() { return contractor; }
    public void setContractor(Contractor contractor) { this.contractor = contractor; }

    // Keep backward compatibility for contractorId
    public Long getContractorId() {
        return contractor != null ? contractor.getId() : null;
    }

    public void setContractorId(Long contractorId) {
        // This method is kept for backward compatibility but won't set the relationship
        // Use setContractor() instead for proper relationship setting
    }

    public List<String> getDocuments() { return documents; }
    public void setDocuments(List<String> documents) { this.documents = documents; }
}