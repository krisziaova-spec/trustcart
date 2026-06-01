package com.trustcart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column(length = 1400)
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @DecimalMin("1.00")
    private BigDecimal price;

    @Min(0)
    private int stock;

    private boolean ecoFriendly;
    private String sustainabilityTag;

    private boolean trustCartShield = true;
    private boolean authenticItemChecked = true;
    private boolean verifiedReviewsOnly = true;
    private boolean suspiciousReviewFlag = false;
    private boolean plasticFreePackaging = false;
    private boolean locallySourced = false;
    private boolean lowWasteDelivery = false;

    private int trustScore;
    private int greenScore;
    private int sellerVerificationScore;
    private int productAuthenticityScore;
    private int reviewQualityScore;
    private int deliveryReliabilityScore;
    private int sustainabilityScore;
    private int returnRiskScore;

    @Column(length = 1400)
    private String reviewSummary;

    @Column(length = 1200)
    private String redFlagSummary;

    private String imageUrl;
    private String productOrigin;
    private String warrantyPolicy;

    private boolean subscriptionEligible = false;
    private Integer subscriptionDiscountPercent = 5;
    private String photoAltText;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(optional = false)
    private Seller seller;

    public Product() {
    }

    public Product(String name, String description, ProductCategory category, BigDecimal price, int stock,
                   boolean ecoFriendly, String sustainabilityTag, int trustScore, String reviewSummary,
                   String imageUrl, ProductStatus status, Seller seller) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.ecoFriendly = ecoFriendly;
        this.sustainabilityTag = sustainabilityTag;
        this.trustScore = trustScore;
        this.reviewSummary = reviewSummary;
        this.imageUrl = imageUrl;
        this.status = status;
        this.seller = seller;
        applyDefaultTrustBreakdown();
    }

    public void applyDefaultTrustBreakdown() {
        if (sellerVerificationScore == 0) sellerVerificationScore = 25;
        if (productAuthenticityScore == 0) productAuthenticityScore = 25;
        if (reviewQualityScore == 0) reviewQualityScore = 22;
        if (deliveryReliabilityScore == 0) deliveryReliabilityScore = 18;
        if (sustainabilityScore == 0) sustainabilityScore = ecoFriendly ? 10 : 6;
        if (trustScore == 0) trustScore = Math.min(100, sellerVerificationScore + productAuthenticityScore + reviewQualityScore + deliveryReliabilityScore + sustainabilityScore);
        if (greenScore == 0) greenScore = ecoFriendly ? 88 : 65;
        if (returnRiskScore == 0) returnRiskScore = 94;
        if (redFlagSummary == null || redFlagSummary.isBlank()) {
            redFlagSummary = suspiciousReviewFlag ? "Review pattern needs admin review." : "No major red flags detected in verified buyer feedback.";
        }
        if (warrantyPolicy == null || warrantyPolicy.isBlank()) {
            warrantyPolicy = "7-day buyer protection with digital refund request tracking.";
        }
        if (productOrigin == null || productOrigin.isBlank()) {
            productOrigin = locallySourced ? "Philippines / Local MSME source" : "Verified marketplace source";
        }
    }

    public String getShieldLabel() {
        return trustCartShield ? "TrustCart Shield Verified" : "Pending Shield Review";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public boolean isEcoFriendly() { return ecoFriendly; }
    public void setEcoFriendly(boolean ecoFriendly) { this.ecoFriendly = ecoFriendly; }
    public String getSustainabilityTag() { return sustainabilityTag; }
    public void setSustainabilityTag(String sustainabilityTag) { this.sustainabilityTag = sustainabilityTag; }
    public boolean isTrustCartShield() { return trustCartShield; }
    public void setTrustCartShield(boolean trustCartShield) { this.trustCartShield = trustCartShield; }
    public boolean isAuthenticItemChecked() { return authenticItemChecked; }
    public void setAuthenticItemChecked(boolean authenticItemChecked) { this.authenticItemChecked = authenticItemChecked; }
    public boolean isVerifiedReviewsOnly() { return verifiedReviewsOnly; }
    public void setVerifiedReviewsOnly(boolean verifiedReviewsOnly) { this.verifiedReviewsOnly = verifiedReviewsOnly; }
    public boolean isSuspiciousReviewFlag() { return suspiciousReviewFlag; }
    public void setSuspiciousReviewFlag(boolean suspiciousReviewFlag) { this.suspiciousReviewFlag = suspiciousReviewFlag; }
    public boolean isPlasticFreePackaging() { return plasticFreePackaging; }
    public void setPlasticFreePackaging(boolean plasticFreePackaging) { this.plasticFreePackaging = plasticFreePackaging; }
    public boolean isLocallySourced() { return locallySourced; }
    public void setLocallySourced(boolean locallySourced) { this.locallySourced = locallySourced; }
    public boolean isLowWasteDelivery() { return lowWasteDelivery; }
    public void setLowWasteDelivery(boolean lowWasteDelivery) { this.lowWasteDelivery = lowWasteDelivery; }
    public int getTrustScore() { return trustScore; }
    public void setTrustScore(int trustScore) { this.trustScore = trustScore; }
    public int getGreenScore() { return greenScore; }
    public void setGreenScore(int greenScore) { this.greenScore = greenScore; }
    public int getSellerVerificationScore() { return sellerVerificationScore; }
    public void setSellerVerificationScore(int sellerVerificationScore) { this.sellerVerificationScore = sellerVerificationScore; }
    public int getProductAuthenticityScore() { return productAuthenticityScore; }
    public void setProductAuthenticityScore(int productAuthenticityScore) { this.productAuthenticityScore = productAuthenticityScore; }
    public int getReviewQualityScore() { return reviewQualityScore; }
    public void setReviewQualityScore(int reviewQualityScore) { this.reviewQualityScore = reviewQualityScore; }
    public int getDeliveryReliabilityScore() { return deliveryReliabilityScore; }
    public void setDeliveryReliabilityScore(int deliveryReliabilityScore) { this.deliveryReliabilityScore = deliveryReliabilityScore; }
    public int getSustainabilityScore() { return sustainabilityScore; }
    public void setSustainabilityScore(int sustainabilityScore) { this.sustainabilityScore = sustainabilityScore; }
    public int getReturnRiskScore() { return returnRiskScore; }
    public void setReturnRiskScore(int returnRiskScore) { this.returnRiskScore = returnRiskScore; }
    public String getReviewSummary() { return reviewSummary; }
    public void setReviewSummary(String reviewSummary) { this.reviewSummary = reviewSummary; }
    public String getRedFlagSummary() { return redFlagSummary; }
    public void setRedFlagSummary(String redFlagSummary) { this.redFlagSummary = redFlagSummary; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getProductOrigin() { return productOrigin; }
    public void setProductOrigin(String productOrigin) { this.productOrigin = productOrigin; }
    public String getWarrantyPolicy() { return warrantyPolicy; }
    public void setWarrantyPolicy(String warrantyPolicy) { this.warrantyPolicy = warrantyPolicy; }
    public boolean isSubscriptionEligible() { return subscriptionEligible; }
    public void setSubscriptionEligible(boolean subscriptionEligible) { this.subscriptionEligible = subscriptionEligible; }
    public Integer getSubscriptionDiscountPercent() { return subscriptionDiscountPercent == null ? 5 : subscriptionDiscountPercent; }
    public void setSubscriptionDiscountPercent(Integer subscriptionDiscountPercent) { this.subscriptionDiscountPercent = subscriptionDiscountPercent == null ? 5 : subscriptionDiscountPercent; }
    public String getPhotoAltText() { return photoAltText == null || photoAltText.isBlank() ? name : photoAltText; }
    public void setPhotoAltText(String photoAltText) { this.photoAltText = photoAltText; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }
}
