package com.Timbua.backend.dto;

import com.Timbua.backend.model.Supplier;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Supplier response DTO (without password)")
public class SupplierResponseDTO {

    @Schema(description = "Supplier ID", example = "1")
    private Long id;

    @Schema(description = "Company name", example = "Material Suppliers Ltd")
    private String companyName;

    @Schema(description = "Business registration number", example = "BRN123456")
    private String businessRegistrationNumber;

    @Schema(description = "Contact person", example = "Jane Smith")
    private String contactPerson;

    @Schema(description = "Email address", example = "info@materialsuppliers.com")
    private String email;

    @Schema(description = "Phone number", example = "0723456789")
    private String phone;

    @Schema(description = "Website URL", example = "https://materialsuppliers.com")
    private String website;

    @Schema(description = "Description", example = "Leading supplier of construction materials")
    private String description;

    @Schema(description = "Years in business", example = "10")
    private Integer yearsInBusiness;

    @Schema(description = "Logo URL", example = "https://materialsuppliers.com/logo.png")
    private String logoUrl;

    @Schema(description = "Verification status", example = "PENDING")
    private Supplier.Status status;

    @Schema(description = "User role", example = "SUPPLIER")
    private Supplier.Role role;

    @Schema(description = "Is verified", example = "false")
    private boolean isVerified;

    @Schema(description = "Verification date")
    private LocalDate verificationDate;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Update timestamp")
    private LocalDateTime updatedAt;

    // Constructor from Entity
    public SupplierResponseDTO(Supplier supplier) {
        this.id = supplier.getId();
        this.companyName = supplier.getCompanyName();
        this.businessRegistrationNumber = supplier.getBusinessRegistrationNumber();
        this.contactPerson = supplier.getContactPerson();
        this.email = supplier.getEmail();
        this.phone = supplier.getPhone();
        this.website = supplier.getWebsite();
        this.description = supplier.getDescription();
        this.yearsInBusiness = supplier.getYearsInBusiness();
        this.logoUrl = supplier.getLogoUrl();
        this.status = supplier.getStatus();
        this.role = supplier.getRole();
        this.isVerified = supplier.isVerified();
        this.verificationDate = supplier.getVerificationDate();
        this.createdAt = supplier.getCreatedAt();
        this.updatedAt = supplier.getUpdatedAt();
    }

    // Empty constructor
    public SupplierResponseDTO() {}

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

    public Supplier.Status getStatus() { return status; }
    public void setStatus(Supplier.Status status) { this.status = status; }

    public Supplier.Role getRole() { return role; }
    public void setRole(Supplier.Role role) { this.role = role; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public LocalDate getVerificationDate() { return verificationDate; }
    public void setVerificationDate(LocalDate verificationDate) { this.verificationDate = verificationDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
