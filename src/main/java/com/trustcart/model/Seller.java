package com.trustcart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Seller {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String storeName;
    @Column(unique = true)
    private String email;
    private String phone;
    private String password;
    private String businessType;
    private String sustainabilityBadge;
    private int reliabilityScore = 90;
    private int responseRateScore = 95;
    private int complaintRateScore = 95;
    private int returnRateScore = 94;
    private int greenComplianceScore = 90;
    private boolean businessVerified = true;
    private boolean identityVerified = true;
    private boolean documentVerified = true;
    private boolean productComplianceChecked = true;
    private boolean invitedOrApprovedOnly = true;
    @Column(length = 1000)
    private String documentProofUrl;
    @Column(length = 1000)
    private String ecoCommitment;
    @Column(length = 1000)
    private String verificationNote;
    private String approvedBy;
    @Column(length = 1000)
    private String storeExactAddress;
    private String storeCity;
    private String storeProvince;
    private Double latitude;
    private Double longitude;
    private Integer serviceRadiusKm = 5;
    private boolean pickupAvailable = true;
    private boolean storeLocationVerified = true;
    @Column(length = 1000)
    private String locationProofUrl;
    private String status = "APPROVED";
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime approvedAt = LocalDateTime.now();

    public String getPublicLocationLabel() {
        String city = storeCity == null || storeCity.isBlank() ? "Verified seller area" : storeCity;
        String province = storeProvince == null || storeProvince.isBlank() ? "" : ", " + storeProvince;
        return city + province + " area";
    }

    public String getProtectedPickupLabel() {
        return pickupAvailable ? "Pickup via TrustCart partner hub only" : "Delivery only";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public String getSustainabilityBadge() { return sustainabilityBadge; }
    public void setSustainabilityBadge(String sustainabilityBadge) { this.sustainabilityBadge = sustainabilityBadge; }
    public int getReliabilityScore() { return reliabilityScore; }
    public void setReliabilityScore(int reliabilityScore) { this.reliabilityScore = reliabilityScore; }
    public int getResponseRateScore() { return responseRateScore; }
    public void setResponseRateScore(int responseRateScore) { this.responseRateScore = responseRateScore; }
    public int getComplaintRateScore() { return complaintRateScore; }
    public void setComplaintRateScore(int complaintRateScore) { this.complaintRateScore = complaintRateScore; }
    public int getReturnRateScore() { return returnRateScore; }
    public void setReturnRateScore(int returnRateScore) { this.returnRateScore = returnRateScore; }
    public int getGreenComplianceScore() { return greenComplianceScore; }
    public void setGreenComplianceScore(int greenComplianceScore) { this.greenComplianceScore = greenComplianceScore; }
    public boolean isBusinessVerified() { return businessVerified; }
    public void setBusinessVerified(boolean businessVerified) { this.businessVerified = businessVerified; }
    public boolean isIdentityVerified() { return identityVerified; }
    public void setIdentityVerified(boolean identityVerified) { this.identityVerified = identityVerified; }
    public boolean isDocumentVerified() { return documentVerified; }
    public void setDocumentVerified(boolean documentVerified) { this.documentVerified = documentVerified; }
    public boolean isProductComplianceChecked() { return productComplianceChecked; }
    public void setProductComplianceChecked(boolean productComplianceChecked) { this.productComplianceChecked = productComplianceChecked; }
    public boolean isInvitedOrApprovedOnly() { return invitedOrApprovedOnly; }
    public void setInvitedOrApprovedOnly(boolean invitedOrApprovedOnly) { this.invitedOrApprovedOnly = invitedOrApprovedOnly; }
    public String getDocumentProofUrl() { return documentProofUrl; }
    public void setDocumentProofUrl(String documentProofUrl) { this.documentProofUrl = documentProofUrl; }
    public String getEcoCommitment() { return ecoCommitment; }
    public void setEcoCommitment(String ecoCommitment) { this.ecoCommitment = ecoCommitment; }
    public String getVerificationNote() { return verificationNote; }
    public void setVerificationNote(String verificationNote) { this.verificationNote = verificationNote; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public String getStoreExactAddress() { return storeExactAddress; }
    public void setStoreExactAddress(String storeExactAddress) { this.storeExactAddress = storeExactAddress; }
    public String getStoreCity() { return storeCity; }
    public void setStoreCity(String storeCity) { this.storeCity = storeCity; }
    public String getStoreProvince() { return storeProvince; }
    public void setStoreProvince(String storeProvince) { this.storeProvince = storeProvince; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Integer getServiceRadiusKm() { return serviceRadiusKm; }
    public void setServiceRadiusKm(Integer serviceRadiusKm) { this.serviceRadiusKm = serviceRadiusKm; }
    public boolean isPickupAvailable() { return pickupAvailable; }
    public void setPickupAvailable(boolean pickupAvailable) { this.pickupAvailable = pickupAvailable; }
    public boolean isStoreLocationVerified() { return storeLocationVerified; }
    public void setStoreLocationVerified(boolean storeLocationVerified) { this.storeLocationVerified = storeLocationVerified; }
    public String getLocationProofUrl() { return locationProofUrl; }
    public void setLocationProofUrl(String locationProofUrl) { this.locationProofUrl = locationProofUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
}
