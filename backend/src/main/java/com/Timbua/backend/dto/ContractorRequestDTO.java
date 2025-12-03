package com.Timbua.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Contractor registration/update request")
public class ContractorRequestDTO {

    @NotBlank(message = "Company name is required")
    @Schema(description = "Company name", example = "BuildMaster Ltd")
    private String companyName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "contact@buildmaster.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "securePassword123")
    private String password;

    @NotBlank(message = "Contact person is required")
    @Schema(description = "Contact person name", example = "John Doe")
    private String contactPerson;

    @Schema(description = "Phone number", example = "0712345678")
    private String phoneNumber;

    @Schema(description = "Business registration number", example = "REG123456")
    private String businessRegistrationNumber;

    @Schema(description = "Physical address", example = "Nairobi, Kenya")
    private String physicalAddress;

    @Schema(description = "Specialization", example = "Residential Construction")
    private String specialization;

    @NotNull(message = "Years of experience is required")
    @Schema(description = "Years of experience", example = "5")
    private Integer yearsOfExperience;

    @Schema(description = "License number", example = "LIC789012")
    private String licenseNumber;

    // Getters and setters
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
}
