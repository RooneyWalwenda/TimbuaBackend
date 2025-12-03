package com.Timbua.backend.dto;

import com.Timbua.backend.model.Contractor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Contractor response DTO (without password)")
public class ContractorResponseDTO {

    @Schema(description = "Contractor ID", example = "1")
    private Long id;

    @Schema(description = "Company name", example = "BuildMaster Ltd")
    private String companyName;

    @Schema(description = "Email address", example = "contact@buildmaster.com")
    private String email;

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

    @Schema(description = "Years of experience", example = "5")
    private Integer yearsOfExperience;

    @Schema(description = "License number", example = "LIC789012")
    private String licenseNumber;

    @Schema(description = "Verification status", example = "PENDING")
    private Contractor.Status status;

    @Schema(description = "User role", example = "CONTRACTOR")
    private Contractor.Role role;

    @Schema(description = "Is verified", example = "false")
    private Boolean isVerified;

    @Schema(description = "Registration date")
    private LocalDateTime registrationDate;

    @Schema(description = "Verification date")
    private LocalDate verificationDate;

    // Constructor from Entity
    public ContractorResponseDTO(Contractor contractor) {
        this.id = contractor.getId();
        this.companyName = contractor.getCompanyName();
        this.email = contractor.getEmail();
        this.contactPerson = contractor.getContactPerson();
        this.phoneNumber = contractor.getPhoneNumber();
        this.businessRegistrationNumber = contractor.getBusinessRegistrationNumber();
        this.physicalAddress = contractor.getPhysicalAddress();
        this.specialization = contractor.getSpecialization();
        this.yearsOfExperience = contractor.getYearsOfExperience();
        this.licenseNumber = contractor.getLicenseNumber();
        this.status = contractor.getStatus();
        this.role = contractor.getRole();
        this.isVerified = contractor.getIsVerified();
        this.registrationDate = contractor.getRegistrationDate();
        this.verificationDate = contractor.getVerificationDate();
    }

    // Empty constructor
    public ContractorResponseDTO() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public Contractor.Status getStatus() { return status; }
    public void setStatus(Contractor.Status status) { this.status = status; }

    public Contractor.Role getRole() { return role; }
    public void setRole(Contractor.Role role) { this.role = role; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public LocalDate getVerificationDate() { return verificationDate; }
    public void setVerificationDate(LocalDate verificationDate) { this.verificationDate = verificationDate; }
}
