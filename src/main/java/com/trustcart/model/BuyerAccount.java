package com.trustcart.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class BuyerAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(unique = true)
    private String email;
    private String phone;
    @Column(length = 1000)
    private String defaultAddress;
    private String password;
    private String preferredCity;
    private Double preferredLatitude;
    private Double preferredLongitude;
    private Integer preferredRadiusKm = 5;
    private boolean nearbySellerFirst = true;
    private boolean pickupInterested = false;
    private Integer loyaltyPointsBalance = 0;
    private Integer lifetimeLoyaltyPoints = 0;
    private BigDecimal lifetimeSpend = BigDecimal.ZERO;
    private String loyaltyTier = "Starter Green Member";

    // TrustCart safety controls. Admin can block or deactivate buyers after verified reports.
    private String status = "ACTIVE"; // ACTIVE, DEACTIVATED, BLOCKED
    private Integer reportCount = 0;
    @Column(length = 1500)
    private String adminSafetyNote;
    private LocalDateTime deactivatedAt;
    private LocalDateTime blockedAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status) || status == null || status.isBlank();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDefaultAddress() { return defaultAddress; }
    public void setDefaultAddress(String defaultAddress) { this.defaultAddress = defaultAddress; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPreferredCity() { return preferredCity; }
    public void setPreferredCity(String preferredCity) { this.preferredCity = preferredCity; }
    public Double getPreferredLatitude() { return preferredLatitude; }
    public void setPreferredLatitude(Double preferredLatitude) { this.preferredLatitude = preferredLatitude; }
    public Double getPreferredLongitude() { return preferredLongitude; }
    public void setPreferredLongitude(Double preferredLongitude) { this.preferredLongitude = preferredLongitude; }
    public Integer getPreferredRadiusKm() { return preferredRadiusKm; }
    public void setPreferredRadiusKm(Integer preferredRadiusKm) { this.preferredRadiusKm = preferredRadiusKm; }
    public boolean isNearbySellerFirst() { return nearbySellerFirst; }
    public void setNearbySellerFirst(boolean nearbySellerFirst) { this.nearbySellerFirst = nearbySellerFirst; }
    public boolean isPickupInterested() { return pickupInterested; }
    public void setPickupInterested(boolean pickupInterested) { this.pickupInterested = pickupInterested; }
    public Integer getLoyaltyPointsBalance() { return loyaltyPointsBalance; }
    public void setLoyaltyPointsBalance(Integer loyaltyPointsBalance) { this.loyaltyPointsBalance = loyaltyPointsBalance; }
    public Integer getLifetimeLoyaltyPoints() { return lifetimeLoyaltyPoints; }
    public void setLifetimeLoyaltyPoints(Integer lifetimeLoyaltyPoints) { this.lifetimeLoyaltyPoints = lifetimeLoyaltyPoints; }
    public BigDecimal getLifetimeSpend() { return lifetimeSpend; }
    public void setLifetimeSpend(BigDecimal lifetimeSpend) { this.lifetimeSpend = lifetimeSpend; }
    public String getLoyaltyTier() { return loyaltyTier; }
    public void setLoyaltyTier(String loyaltyTier) { this.loyaltyTier = loyaltyTier; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getReportCount() { return reportCount; }
    public void setReportCount(Integer reportCount) { this.reportCount = reportCount; }
    public String getAdminSafetyNote() { return adminSafetyNote; }
    public void setAdminSafetyNote(String adminSafetyNote) { this.adminSafetyNote = adminSafetyNote; }
    public LocalDateTime getDeactivatedAt() { return deactivatedAt; }
    public void setDeactivatedAt(LocalDateTime deactivatedAt) { this.deactivatedAt = deactivatedAt; }
    public LocalDateTime getBlockedAt() { return blockedAt; }
    public void setBlockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
