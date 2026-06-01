package com.trustcart.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class BuyerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String fullName;

    @Email
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
    private LocalDateTime createdAt = LocalDateTime.now();

    public BuyerAccount() {
    }

    public BuyerAccount(String fullName, String email, String phone, String password, String preferredCity,
                        Double preferredLatitude, Double preferredLongitude, Integer preferredRadiusKm) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.preferredCity = preferredCity;
        this.preferredLatitude = preferredLatitude;
        this.preferredLongitude = preferredLongitude;
        this.preferredRadiusKm = preferredRadiusKm == null ? 5 : preferredRadiusKm;
    }

    public String getLocationLabel() {
        if (preferredCity == null || preferredCity.isBlank()) return "No target market selected";
        return preferredCity + " within " + (preferredRadiusKm == null ? 5 : preferredRadiusKm) + " km";
    }

    public String getLoyaltyTier() {
        int points = lifetimeLoyaltyPoints == null ? 0 : lifetimeLoyaltyPoints;
        if (points >= 1500) return "Platinum Green Member";
        if (points >= 700) return "Gold Green Member";
        if (points >= 250) return "Silver Green Member";
        return "Starter Green Member";
    }

    public void addLoyaltyPoints(int points) {
        if (points <= 0) return;
        this.loyaltyPointsBalance = (this.loyaltyPointsBalance == null ? 0 : this.loyaltyPointsBalance) + points;
        this.lifetimeLoyaltyPoints = (this.lifetimeLoyaltyPoints == null ? 0 : this.lifetimeLoyaltyPoints) + points;
    }

    public void redeemLoyaltyPoints(int points) {
        if (points <= 0) return;
        this.loyaltyPointsBalance = Math.max(0, (this.loyaltyPointsBalance == null ? 0 : this.loyaltyPointsBalance) - points);
    }

    public void addLifetimeSpend(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
        this.lifetimeSpend = (this.lifetimeSpend == null ? BigDecimal.ZERO : this.lifetimeSpend).add(amount);
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
    public Integer getLoyaltyPointsBalance() { return loyaltyPointsBalance == null ? 0 : loyaltyPointsBalance; }
    public void setLoyaltyPointsBalance(Integer loyaltyPointsBalance) { this.loyaltyPointsBalance = loyaltyPointsBalance == null ? 0 : loyaltyPointsBalance; }
    public Integer getLifetimeLoyaltyPoints() { return lifetimeLoyaltyPoints == null ? 0 : lifetimeLoyaltyPoints; }
    public void setLifetimeLoyaltyPoints(Integer lifetimeLoyaltyPoints) { this.lifetimeLoyaltyPoints = lifetimeLoyaltyPoints == null ? 0 : lifetimeLoyaltyPoints; }
    public BigDecimal getLifetimeSpend() { return lifetimeSpend == null ? BigDecimal.ZERO : lifetimeSpend; }
    public void setLifetimeSpend(BigDecimal lifetimeSpend) { this.lifetimeSpend = lifetimeSpend == null ? BigDecimal.ZERO : lifetimeSpend; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
