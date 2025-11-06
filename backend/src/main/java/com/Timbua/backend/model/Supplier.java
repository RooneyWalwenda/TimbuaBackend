package com.Timbua.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email"}),
                @UniqueConstraint(columnNames = {"business_registration_number"})
        })
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="company_name", nullable = false)
    private String companyName;

    @Column(name="business_registration_number", nullable = false)
    private String businessRegistrationNumber;

    @Column(name="contact_person")
    private String contactPerson;

    @Column(nullable = false)
    private String email;

    private String password; // Added password field

    private String phone;
    private String website;
    private String description;
    private Integer yearsInBusiness;
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.SUPPLIER; // Added role field

    @Column(name = "is_verified")
    private boolean isVerified = false;

    private LocalDate verificationDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Material> materials = new ArrayList<>();

    public enum Status {
        PENDING,
        VERIFIED,
        SUSPENDED,
        REJECTED
    }

    public enum Role {
        CONTRACTOR,
        SUPPLIER,
        ADMIN
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBusinessRegistrationNumber() { return businessRegistrationNumber; }
    public void setBusinessRegistrationNumber(String businessRegistrationNumber) { this.businessRegistrationNumber = businessRegistrationNumber; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getYearsInBusiness() { return yearsInBusiness; }
    public void setYearsInBusiness(Integer yearsInBusiness) { this.yearsInBusiness = yearsInBusiness; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public LocalDate getVerificationDate() { return verificationDate; }
    public void setVerificationDate(LocalDate verificationDate) { this.verificationDate = verificationDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Material> getMaterials() { return materials; }
    public void setMaterials(List<Material> materials) { this.materials = materials; }
}