package com.Timbua.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contractors")
public class Contractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String email;

    private String password; // Will be encrypted

    @Column(nullable = false)
    private String contactPerson;

    private String phoneNumber;

    @Column(unique = true)
    private String businessRegistrationNumber;

    private String physicalAddress;

    private String specialization; // e.g., Residential, Commercial, Industrial

    private Integer yearsOfExperience;

    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CONTRACTOR;

    private Boolean isVerified = false;

    private LocalDateTime registrationDate;

    private LocalDate verificationDate;

    // Relationships - FIXED: Added fetch type and JsonIgnore for circular references
    @OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Added to prevent circular serialization
    private List<ContractorDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "contractor", fetch = FetchType.LAZY)
    @JsonIgnore // Added to prevent circular serialization
    private List<ConstructionSite> constructionSites;

    @OneToMany(mappedBy = "contractor", fetch = FetchType.LAZY)
    @JsonIgnore // Added to prevent circular serialization
    private List<QuotationRequest> quotationRequests;

    public enum Status {
        PENDING,
        UNDER_REVIEW,
        VERIFIED,
        REJECTED,
        SUSPENDED
    }

    public enum Role {
        CONTRACTOR,
        SUPPLIER,
        ADMIN
    }

    // Constructors
    public Contractor() {
        this.registrationDate = LocalDateTime.now();
        this.role = Role.CONTRACTOR;
    }

    public Contractor(String companyName, String email, String password, String contactPerson) {
        this.companyName = companyName;
        this.email = email;
        this.password = password;
        this.contactPerson = contactPerson;
        this.registrationDate = LocalDateTime.now();
        this.role = Role.CONTRACTOR;
    }

    // Getters and Setters (same as before)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBusinessRegistrationNumber() { return businessRegistrationNumber; }
    public void setBusinessRegistrationNumber(String businessRegistrationNumber) { this.businessRegistrationNumber = businessRegistrationNumber; }

    public String getPhysicalAddress() { return physicalAddress; }
    public void setPhysicalAddress(String physicalAddress) { this.physicalAddress = physicalAddress; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public LocalDate getVerificationDate() { return verificationDate; }
    public void setVerificationDate(LocalDate verificationDate) { this.verificationDate = verificationDate; }

    public List<ContractorDocument> getDocuments() { return documents; }
    public void setDocuments(List<ContractorDocument> documents) { this.documents = documents; }

    public List<ConstructionSite> getConstructionSites() { return constructionSites; }
    public void setConstructionSites(List<ConstructionSite> constructionSites) { this.constructionSites = constructionSites; }

    public List<QuotationRequest> getQuotationRequests() { return quotationRequests; }
    public void setQuotationRequests(List<QuotationRequest> quotationRequests) { this.quotationRequests = quotationRequests; }
}
