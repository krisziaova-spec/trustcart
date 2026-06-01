package com.trustcart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String storeName;

    @Email
    @Column(unique = true)
    private String email;

    private String phone;
    private String password;
    private String businessType;
    private String sustainabilityBadge;

    private int reliabilityScore;
    private int responseRateScore;
    private int complaintRateScore;
    private int returnRateScore;
    private int greenComplianceScore;

    private boolean businessVerified = false;
    private boolean identityVerified = false;
    private boolean documentVerified = false;
    private boolean productComplianceChecked = false;
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
    private boolean storeLocationVerified = false;

    @Column(length = 1000)
    private String locationProofUrl;

    @Enumerated(EnumType.STRING)
    private SellerStatus status = SellerStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public Seller() {
    }

    public Seller(String storeName, String email, String phone, String businessType, String sustainabilityBadge, int reliabilityScore, SellerStatus status) {
        this.storeName = storeName;
        this.email = email;
        this.phone = phone;
        this.businessType = businessType;
        this.sustainabilityBadge = sustainabilityBadge;
        this.reliabilityScore = reliabilityScore;
        this.status = status;
        if (status == SellerStatus.APPROVED) {
            markVerifiedDefaults();
        }
    }

    public void markVerifiedDefaults() {
        this.businessVerified = true;
        this.identityVerified = true;
        this.documentVerified = true;
        this.productComplianceChecked = true;
        this.storeLocationVerified = true;
        this.reliabilityScore = Math.max(this.reliabilityScore, 92);
        this.responseRateScore = Math.max(this.responseRateScore, 96);
        this.complaintRateScore = Math.max(this.complaintRateScore, 95);
        this.returnRateScore = Math.max(this.returnRateScore, 94);
        this.greenComplianceScore = Math.max(this.greenComplianceScore, 88);
        if (this.verificationNote == null || this.verificationNote.isBlank()) {
            this.verificationNote = "Business identity, product compliance, location proof, and sustainability claims checked for platform approval.";
        }
        if (this.approvedBy == null || this.approvedBy.isBlank()) {
            this.approvedBy = "TrustCart Seller Verification";
        }
        if (this.approvedAt == null) {
            this.approvedAt = LocalDateTime.now();
        }
    }

    public String getPassportStatus() {
        return businessVerified && identityVerified && documentVerified && productComplianceChecked && storeLocationVerified ? "Complete" : "Verification Needed";
    }

    public String getPublicLocationLabel() {
        if (storeCity == null || storeCity.isBlank()) return "Verified area hidden";
        String provincePart = storeProvince == null || storeProvince.isBlank() ? "" : ", " + storeProvince;
        return storeCity + provincePart + " area";
    }

    public String getProtectedPickupLabel() {
        if (!pickupAvailable) return "Pickup not available";
        return "Pickup via TrustCart partner hub only - exact store address is hidden until platform-approved pickup confirmation.";
    }

    public String getDeliveryPromise() {
        if (serviceRadiusKm != null && serviceRadiusKm <= 5) return "Nearby delivery eligible";
        return "Delivery coverage subject to seller radius";
    }

    public String getLocationIntegrityNote() {
        return storeLocationVerified ? "Store location verified for platform use; exact address hidden from buyers to prevent off-platform transactions." : "Store location needs verification before pickup can be enabled.";
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
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
    public SellerStatus getStatus() { return status; }
    public void setStatus(SellerStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
