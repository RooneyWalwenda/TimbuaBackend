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

    // FIXED: Changed to EAGER fetching and removed contractorId field
    @ManyToOne(fetch = FetchType.EAGER) // Changed from LAZY to EAGER
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

    // FIXED: Proper contractor relationship
    public Contractor getContractor() { return contractor; }
    public void setContractor(Contractor contractor) { this.contractor = contractor; }

    // FIXED: Proper contractorId getter that works with JSON serialization
    public Long getContractorId() {
        return contractor != null ? contractor.getId() : null;
    }

    // FIXED: Remove setContractorId to avoid confusion
    // Don't include setContractorId method - use setContractor() instead

    public List<String> getDocuments() { return documents; }
    public void setDocuments(List<String> documents) { this.documents = documents; }
}
