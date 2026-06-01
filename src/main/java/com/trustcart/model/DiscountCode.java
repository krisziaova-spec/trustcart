package com.trustcart.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(length = 600)
    private String description;

    private BigDecimal minimumSpend = BigDecimal.ZERO;
    private Integer percentOff = 0;
    private BigDecimal amountOff = BigDecimal.ZERO;
    private boolean active = true;
    private boolean firstOrderOnly = false;
    private boolean subscriptionBoost = false;
    private Integer maxRedemptions = 0;
    private Integer timesRedeemed = 0;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;

    private Long sellerId;

    private String createdBySeller;

    public DiscountCode() {}

    public DiscountCode(String code, String description, BigDecimal minimumSpend, Integer percentOff, BigDecimal amountOff, boolean active) {
        this.code = normalizeCode(code);
        this.description = description;
        this.minimumSpend = minimumSpend == null ? BigDecimal.ZERO : minimumSpend;
        this.percentOff = percentOff == null ? 0 : percentOff;
        this.amountOff = amountOff == null ? BigDecimal.ZERO : amountOff;
        this.active = active;
    }

    public boolean isCurrentlyUsable(BigDecimal subtotal) {
        BigDecimal safeSubtotal = subtotal == null ? BigDecimal.ZERO : subtotal;
        boolean notExpired = expiresAt == null || expiresAt.isAfter(LocalDateTime.now());
        boolean redemptionAvailable = maxRedemptions == null || maxRedemptions == 0 || timesRedeemed == null || timesRedeemed < maxRedemptions;
        return active && notExpired && redemptionAvailable && safeSubtotal.compareTo(minimumSpend == null ? BigDecimal.ZERO : minimumSpend) >= 0;
    }

    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        BigDecimal safeSubtotal = subtotal == null ? BigDecimal.ZERO : subtotal;
        if (!isCurrentlyUsable(safeSubtotal)) return BigDecimal.ZERO;
        BigDecimal percentDiscount = BigDecimal.ZERO;
        if (percentOff != null && percentOff > 0) {
            percentDiscount = safeSubtotal.multiply(BigDecimal.valueOf(percentOff)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        BigDecimal fixedDiscount = amountOff == null ? BigDecimal.ZERO : amountOff;
        BigDecimal selected = percentDiscount.max(fixedDiscount);
        return selected.min(safeSubtotal).max(BigDecimal.ZERO);
    }

    public void recordRedemption() {
        this.timesRedeemed = (this.timesRedeemed == null ? 0 : this.timesRedeemed) + 1;
    }

    public static String normalizeCode(String raw) {
        return raw == null ? "" : raw.trim().toUpperCase().replace(" ", "");
    }

    public String getDisplayLabel() {
        if (percentOff != null && percentOff > 0) return code + " - " + percentOff + "% off";
        if (amountOff != null && amountOff.compareTo(BigDecimal.ZERO) > 0) return code + " - ₱" + amountOff + " off";
        return code;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = normalizeCode(code); }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMinimumSpend() { return minimumSpend; }
    public void setMinimumSpend(BigDecimal minimumSpend) { this.minimumSpend = minimumSpend == null ? BigDecimal.ZERO : minimumSpend; }
    public Integer getPercentOff() { return percentOff; }
    public void setPercentOff(Integer percentOff) { this.percentOff = percentOff == null ? 0 : percentOff; }
    public BigDecimal getAmountOff() { return amountOff; }
    public void setAmountOff(BigDecimal amountOff) { this.amountOff = amountOff == null ? BigDecimal.ZERO : amountOff; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isFirstOrderOnly() { return firstOrderOnly; }
    public void setFirstOrderOnly(boolean firstOrderOnly) { this.firstOrderOnly = firstOrderOnly; }
    public boolean isSubscriptionBoost() { return subscriptionBoost; }
    public void setSubscriptionBoost(boolean subscriptionBoost) { this.subscriptionBoost = subscriptionBoost; }
    public Integer getMaxRedemptions() { return maxRedemptions; }
    public void setMaxRedemptions(Integer maxRedemptions) { this.maxRedemptions = maxRedemptions == null ? 0 : maxRedemptions; }
    public Integer getTimesRedeemed() { return timesRedeemed; }
    public void setTimesRedeemed(Integer timesRedeemed) { this.timesRedeemed = timesRedeemed == null ? 0 : timesRedeemed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getCreatedBySeller() { return createdBySeller; }
    public void setCreatedBySeller(String createdBySeller) { this.createdBySeller = createdBySeller; }
}
