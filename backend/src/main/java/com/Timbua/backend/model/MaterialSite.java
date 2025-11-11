package com.Timbua.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "material_sites")
public class MaterialSite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "questionnaire_no")
    private Integer questionnaireNo;

    @Column(name = "research_assistant_no")
    private String researchAssistantNo;

    @Column(name = "material")
    private String material;

    @Column(name = "material_location")
    private String materialLocation;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "material_used_in")
    private String materialUsedIn;

    @Column(name = "size_of_manufacturing_industry")
    private String sizeOfManufacturingIndustry;

    @Column(name = "period_of_manufacture")
    private String periodOfManufacture;

    @Column(name = "owner_of_material")
    private String ownerOfMaterial;

    @Column(name = "material_usage", length = 1000)
    private String materialUsage;

    @Column(name = "number_of_people_employed")
    private String numberOfPeopleEmployed;

    @Column(name = "similar_locations")
    private String similarLocations;

    @Column(name = "volume_produced_per_day")
    private String volumeProducedPerDay;

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name = "county")
    private String county;

    @Column(name = "sub_county")
    private String subCounty;

    // Default constructor
    public MaterialSite() {}

    // Updated constructor with county and subCounty at the END (to match your DataLoader)
    public MaterialSite(Integer questionnaireNo, String researchAssistantNo, String material,
                        String materialLocation, BigDecimal latitude, BigDecimal longitude,
                        String materialUsedIn, String sizeOfManufacturingIndustry,
                        String periodOfManufacture, String ownerOfMaterial, String materialUsage,
                        String numberOfPeopleEmployed, String similarLocations,
                        String volumeProducedPerDay, String comments, String county, String subCounty) {
        this.questionnaireNo = questionnaireNo;
        this.researchAssistantNo = researchAssistantNo;
        this.material = material;
        this.materialLocation = materialLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.materialUsedIn = materialUsedIn;
        this.sizeOfManufacturingIndustry = sizeOfManufacturingIndustry;
        this.periodOfManufacture = periodOfManufacture;
        this.ownerOfMaterial = ownerOfMaterial;
        this.materialUsage = materialUsage;
        this.numberOfPeopleEmployed = numberOfPeopleEmployed;
        this.similarLocations = similarLocations;
        this.volumeProducedPerDay = volumeProducedPerDay;
        this.comments = comments;
        this.county = county;
        this.subCounty = subCounty;
    }

    // Old constructor for backward compatibility (without county/subCounty)
    public MaterialSite(Integer questionnaireNo, String researchAssistantNo, String material,
                        String materialLocation, BigDecimal latitude, BigDecimal longitude,
                        String materialUsedIn, String sizeOfManufacturingIndustry,
                        String periodOfManufacture, String ownerOfMaterial, String materialUsage,
                        String numberOfPeopleEmployed, String similarLocations,
                        String volumeProducedPerDay, String comments) {
        this(questionnaireNo, researchAssistantNo, material, materialLocation, latitude, longitude,
                materialUsedIn, sizeOfManufacturingIndustry, periodOfManufacture, ownerOfMaterial,
                materialUsage, numberOfPeopleEmployed, similarLocations, volumeProducedPerDay,
                comments, null, null);
    }

    // Remove this incorrect constructor as it's causing issues
    // public MaterialSite(int questionnaireNo, String researchAssistantNo, String blocks, String materialLocation, BigDecimal bigDecimal, BigDecimal bigDecimal1, String inTheCounty, String smallIndustry, String materialUsedIn, String community, String periodOfManufacture, String ownerOfMaterial, Object materialUsage, String numberOfPeopleEmployed, String areaIsOwnedByCommunity, String mombasa, String kisauni) {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getQuestionnaireNo() { return questionnaireNo; }
    public void setQuestionnaireNo(Integer questionnaireNo) { this.questionnaireNo = questionnaireNo; }

    public String getResearchAssistantNo() { return researchAssistantNo; }
    public void setResearchAssistantNo(String researchAssistantNo) { this.researchAssistantNo = researchAssistantNo; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getMaterialLocation() { return materialLocation; }
    public void setMaterialLocation(String materialLocation) { this.materialLocation = materialLocation; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getMaterialUsedIn() { return materialUsedIn; }
    public void setMaterialUsedIn(String materialUsedIn) { this.materialUsedIn = materialUsedIn; }

    public String getSizeOfManufacturingIndustry() { return sizeOfManufacturingIndustry; }
    public void setSizeOfManufacturingIndustry(String sizeOfManufacturingIndustry) { this.sizeOfManufacturingIndustry = sizeOfManufacturingIndustry; }

    public String getPeriodOfManufacture() { return periodOfManufacture; }
    public void setPeriodOfManufacture(String periodOfManufacture) { this.periodOfManufacture = periodOfManufacture; }

    public String getOwnerOfMaterial() { return ownerOfMaterial; }
    public void setOwnerOfMaterial(String ownerOfMaterial) { this.ownerOfMaterial = ownerOfMaterial; }

    public String getMaterialUsage() { return materialUsage; }
    public void setMaterialUsage(String materialUsage) { this.materialUsage = materialUsage; }

    public String getNumberOfPeopleEmployed() { return numberOfPeopleEmployed; }
    public void setNumberOfPeopleEmployed(String numberOfPeopleEmployed) { this.numberOfPeopleEmployed = numberOfPeopleEmployed; }

    public String getSimilarLocations() { return similarLocations; }
    public void setSimilarLocations(String similarLocations) { this.similarLocations = similarLocations; }

    public String getVolumeProducedPerDay() { return volumeProducedPerDay; }
    public void setVolumeProducedPerDay(String volumeProducedPerDay) { this.volumeProducedPerDay = volumeProducedPerDay; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }

    public String getSubCounty() { return subCounty; }
    public void setSubCounty(String subCounty) { this.subCounty = subCounty; }
}
