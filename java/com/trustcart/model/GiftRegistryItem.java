package com.trustcart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class GiftRegistryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private GiftRegistry giftRegistry;

    @ManyToOne(optional = false)
    private Product product;

    private Integer quantity = 1;
    private Integer purchasedQuantity = 0;
    private String priority = "Nice to have";
    @Column(length = 800)
    private String giftNote;
    private String status = "NEEDED";
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isCompleted() {
        int q = quantity == null ? 0 : quantity;
        int p = purchasedQuantity == null ? 0 : purchasedQuantity;
        return q > 0 && p >= q;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GiftRegistry getGiftRegistry() { return giftRegistry; }
    public void setGiftRegistry(GiftRegistry giftRegistry) { this.giftRegistry = giftRegistry; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getPurchasedQuantity() { return purchasedQuantity; }
    public void setPurchasedQuantity(Integer purchasedQuantity) { this.purchasedQuantity = purchasedQuantity; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getGiftNote() { return giftNote; }
    public void setGiftNote(String giftNote) { this.giftNote = giftNote; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
