package com.Timbua.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Supplier registration/update request")
public class SupplierRequestDTO {

    @NotBlank(message = "Company name is required")
    @Schema(description = "Company name", example = "Material Suppliers Ltd")
    private String companyName;

    @NotBlank(message = "Business registration number is required")
    @Schema(description = "Business registration number", example = "BRN123456")
    private String businessRegistrationNumber;

    @Schema(description = "Contact person", example = "Jane Smith")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "info@materialsuppliers.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "securePassword123")
    private String password;

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

    // Getters and setters
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
}
