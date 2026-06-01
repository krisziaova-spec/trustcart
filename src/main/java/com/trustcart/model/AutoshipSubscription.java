package com.trustcart.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class AutoshipSubscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private BuyerAccount buyer;
    @ManyToOne(optional = false)
    private Product product;
    private String frequency = "MONTHLY";
    private Integer quantity = 1;
    private BigDecimal recurringPrice = BigDecimal.ZERO;
    private Integer subscriptionDiscountPercent = 5;
    private LocalDate nextShipmentDate;
    private String status = "ACTIVE";
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(length = 1000)
    private String protectionNote;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BuyerAccount getBuyer() { return buyer; }
    public void setBuyer(BuyerAccount buyer) { this.buyer = buyer; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getRecurringPrice() { return recurringPrice; }
    public void setRecurringPrice(BigDecimal recurringPrice) { this.recurringPrice = recurringPrice; }
    public Integer getSubscriptionDiscountPercent() { return subscriptionDiscountPercent; }
    public void setSubscriptionDiscountPercent(Integer subscriptionDiscountPercent) { this.subscriptionDiscountPercent = subscriptionDiscountPercent; }
    public LocalDate getNextShipmentDate() { return nextShipmentDate; }
    public void setNextShipmentDate(LocalDate nextShipmentDate) { this.nextShipmentDate = nextShipmentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getProtectionNote() { return protectionNote; }
    public void setProtectionNote(String protectionNote) { this.protectionNote = protectionNote; }
}
