package com.trustcart.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 1800)
    private String description;
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
    private BigDecimal price = BigDecimal.ZERO;
    private int stock = 0;
    private boolean ecoFriendly;
    private String sustainabilityTag;
    private boolean trustCartShield = true;
    private boolean authenticItemChecked = true;
    private boolean verifiedReviewsOnly = true;
    private boolean suspiciousReviewFlag = false;
    private boolean plasticFreePackaging = true;
    private boolean locallySourced = false;
    private boolean lowWasteDelivery = true;
    private int trustScore = 90;
    private int greenScore = 90;
    private int sellerVerificationScore = 25;
    private int productAuthenticityScore = 24;
    private int reviewQualityScore = 23;
    private int deliveryReliabilityScore = 18;
    private int sustainabilityScore = 10;
    private int returnRiskScore = 94;
    @Column(length = 1400)
    private String reviewSummary;
    @Column(length = 1200)
    private String redFlagSummary;
    @Column(length = 1200)
    private String imageUrl;
    private String productOrigin;
    private String warrantyPolicy;
    private boolean subscriptionEligible;
    private int subscriptionDiscountPercent = 5;
    private String photoAltText;
    private String status = "APPROVED";
    private boolean tryOnEligible = false;
    private String tryOnGender;
    @Column(length = 1000)
    private String tryOnAssetUrl;
    private String stockStatus = "In Stock";
    private String estimatedDelivery = "ETA: 1-2 days";
    private String fulfilledBy = "SELLER";
    private String fulfillmentStatus = "SELLER_MANAGED";
    private int trustCartStock = 0;
    @Column(length = 1200)
    private String fulfillmentNote = "Seller stores, packs, and ships this product.";
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(optional = false)
    private Seller seller;

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
    public int getSubscriptionDiscountPercent() { return subscriptionDiscountPercent; }
    public void setSubscriptionDiscountPercent(int subscriptionDiscountPercent) { this.subscriptionDiscountPercent = subscriptionDiscountPercent; }
    public String getPhotoAltText() { return photoAltText; }
    public void setPhotoAltText(String photoAltText) { this.photoAltText = photoAltText; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isTryOnEligible() { return tryOnEligible; }
    public void setTryOnEligible(boolean tryOnEligible) { this.tryOnEligible = tryOnEligible; }
    public String getTryOnGender() { return tryOnGender; }
    public void setTryOnGender(String tryOnGender) { this.tryOnGender = tryOnGender; }
    public String getTryOnAssetUrl() { return tryOnAssetUrl; }
    public void setTryOnAssetUrl(String tryOnAssetUrl) { this.tryOnAssetUrl = tryOnAssetUrl; }
    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public String getFulfilledBy() { return fulfilledBy; }
    public void setFulfilledBy(String fulfilledBy) { this.fulfilledBy = fulfilledBy; }
    public String getFulfillmentStatus() { return fulfillmentStatus; }
    public void setFulfillmentStatus(String fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; }
    public int getTrustCartStock() { return trustCartStock; }
    public void setTrustCartStock(int trustCartStock) { this.trustCartStock = trustCartStock; }
    public String getFulfillmentNote() { return fulfillmentNote; }
    public void setFulfillmentNote(String fulfillmentNote) { this.fulfillmentNote = fulfillmentNote; }
    public boolean isFulfilledByTrustCart() { return "TRUSTCART".equalsIgnoreCase(fulfilledBy); }
    public String getFulfillmentLabel() { return isFulfilledByTrustCart() ? "Fulfilled by TrustCart" : "Fulfilled by Seller"; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }
}
