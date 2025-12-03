package com.Timbua.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotation_requests")
public class QuotationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long materialId;       // Material requested

    // CHANGED: Removed single supplierId and added many-to-many relationship
    @ManyToMany
    @JoinTable(
            name = "quotation_request_suppliers",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    @JsonIgnoreProperties({"materials", "password", "documents", "invitedSuppliers"})  // Exclude sensitive/bidirectional fields
    private List<Supplier> invitedSuppliers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id")
    @JsonIgnoreProperties({"quotationRequests", "password", "documents", "constructionSites"})  // Prevent circular reference
    private Contractor contractor;

    private Long siteId;           // Site where materials are needed

    private String material;
    private double quantity;
    private String unit;
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,      // Request created, waiting for quotes
        QUOTED,       // At least one quote received
        ACCEPTED,     // Quote accepted, order created
        CANCELLED     // Request cancelled
    }

    @OneToMany(mappedBy = "quotationRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("quotationRequest")  // Prevent circular reference
    private List<Quote> quotes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    // CHANGED: Getter/Setter for multiple suppliers
    public List<Supplier> getInvitedSuppliers() { return invitedSuppliers; }
    public void setInvitedSuppliers(List<Supplier> invitedSuppliers) { this.invitedSuppliers = invitedSuppliers; }

    // Helper method to add a single supplier
    public void addInvitedSupplier(Supplier supplier) {
        if (!invitedSuppliers.contains(supplier)) {
            invitedSuppliers.add(supplier);
        }
    }

    // Helper method to remove a supplier
    public void removeInvitedSupplier(Supplier supplier) {
        invitedSuppliers.remove(supplier);
    }

    public Contractor getContractor() { return contractor; }
    public void setContractor(Contractor contractor) { this.contractor = contractor; }

    public Long getContractorId() {
        return contractor != null ? contractor.getId() : null;
    }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public List<Quote> getQuotes() { return quotes; }
    public void setQuotes(List<Quote> quotes) { this.quotes = quotes; }
}
