package com.trustcart.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GiftRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String registryName;
    private String registryType;
    private String recipientName;
    private String recipientEmail;
    private LocalDate eventDate;
    private String deliveryCity;
    @Column(length = 1000)
    private String registryNote;
    @Column(length = 1000)
    private String protectedDeliveryNote = "Recipient exact address is hidden. Gifts must be purchased inside TrustCart to remain covered by Buyer Protection.";
    @Column(unique = true)
    private String shareCode;
    private String privacy = "PUBLIC_LINK";
    private String status = "ACTIVE";
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(optional = false)
    private BuyerAccount buyer;

    @OneToMany(mappedBy = "giftRegistry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GiftRegistryItem> items = new ArrayList<>();

    public int getTotalRequestedQuantity() {
        return items.stream().mapToInt(i -> i.getQuantity() == null ? 0 : i.getQuantity()).sum();
    }

    public int getTotalReservedQuantity() {
        return items.stream().mapToInt(i -> i.getPurchasedQuantity() == null ? 0 : i.getPurchasedQuantity()).sum();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegistryName() { return registryName; }
    public void setRegistryName(String registryName) { this.registryName = registryName; }
    public String getRegistryType() { return registryType; }
    public void setRegistryType(String registryType) { this.registryType = registryType; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }
    public String getRegistryNote() { return registryNote; }
    public void setRegistryNote(String registryNote) { this.registryNote = registryNote; }
    public String getProtectedDeliveryNote() { return protectedDeliveryNote; }
    public void setProtectedDeliveryNote(String protectedDeliveryNote) { this.protectedDeliveryNote = protectedDeliveryNote; }
    public String getShareCode() { return shareCode; }
    public void setShareCode(String shareCode) { this.shareCode = shareCode; }
    public String getPrivacy() { return privacy; }
    public void setPrivacy(String privacy) { this.privacy = privacy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public BuyerAccount getBuyer() { return buyer; }
    public void setBuyer(BuyerAccount buyer) { this.buyer = buyer; }
    public List<GiftRegistryItem> getItems() { return items; }
    public void setItems(List<GiftRegistryItem> items) { this.items = items; }
}
