package com.trustcart.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class DiscountCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String code;
    @Column(length = 600)
    private String description;
    private BigDecimal minimumSpend = BigDecimal.ZERO;
    private int percentOff = 0;
    private BigDecimal amountOff = BigDecimal.ZERO;
    private boolean active = true;
    private boolean firstOrderOnly = false;
    private boolean subscriptionBoost = false;
    private int maxRedemptions = 0;
    private int timesRedeemed = 0;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
    @ManyToOne
    private Seller seller;
    private String createdBySeller;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMinimumSpend() { return minimumSpend; }
    public void setMinimumSpend(BigDecimal minimumSpend) { this.minimumSpend = minimumSpend; }
    public int getPercentOff() { return percentOff; }
    public void setPercentOff(int percentOff) { this.percentOff = percentOff; }
    public BigDecimal getAmountOff() { return amountOff; }
    public void setAmountOff(BigDecimal amountOff) { this.amountOff = amountOff; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isFirstOrderOnly() { return firstOrderOnly; }
    public void setFirstOrderOnly(boolean firstOrderOnly) { this.firstOrderOnly = firstOrderOnly; }
    public boolean isSubscriptionBoost() { return subscriptionBoost; }
    public void setSubscriptionBoost(boolean subscriptionBoost) { this.subscriptionBoost = subscriptionBoost; }
    public int getMaxRedemptions() { return maxRedemptions; }
    public void setMaxRedemptions(int maxRedemptions) { this.maxRedemptions = maxRedemptions; }
    public int getTimesRedeemed() { return timesRedeemed; }
    public void setTimesRedeemed(int timesRedeemed) { this.timesRedeemed = timesRedeemed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }
    public String getCreatedBySeller() { return createdBySeller; }
    public void setCreatedBySeller(String createdBySeller) { this.createdBySeller = createdBySeller; }
}
