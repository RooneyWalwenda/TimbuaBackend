package com.Timbua.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "quotation_requests")
public class QuotationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long materialId;       // Material requested
    private Long supplierId;       // Supplier who owns the material
    private Long contractorId;     // Contractor making the request
    private Long siteId;           // Site where materials are needed

    private String material;
    private double quantity;
    private String unit;
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        RECEIVED,
        ACCEPTED,
        REJECTED
    }

    @OneToMany(mappedBy = "quotationRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quote> quotes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Long getContractorId() { return contractorId; }
    public void setContractorId(Long contractorId) { this.contractorId = contractorId; }

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
