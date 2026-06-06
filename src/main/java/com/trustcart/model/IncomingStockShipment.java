package com.trustcart.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class IncomingStockShipment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String shipmentCode;
    @ManyToOne(optional = false)
    private Seller seller;
    @ManyToOne(optional = false)
    private Product product;
    @ManyToOne(optional = false)
    private TrustCartWarehouse warehouse;
    private int quantityPlanned = 0;
    private int quantityReceived = 0;
    private String status = "PENDING_DROP_OFF"; // PENDING_DROP_OFF, IN_TRANSIT, RECEIVED, PARTIALLY_RECEIVED, REJECTED
    private LocalDate preferredDropoffDate;
    @Column(length = 1600)
    private String sellerNote;
    @Column(length = 1600)
    private String adminNote;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getShipmentCode() { return shipmentCode; }
    public void setShipmentCode(String shipmentCode) { this.shipmentCode = shipmentCode; }
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public TrustCartWarehouse getWarehouse() { return warehouse; }
    public void setWarehouse(TrustCartWarehouse warehouse) { this.warehouse = warehouse; }
    public int getQuantityPlanned() { return quantityPlanned; }
    public void setQuantityPlanned(int quantityPlanned) { this.quantityPlanned = quantityPlanned; }
    public int getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(int quantityReceived) { this.quantityReceived = quantityReceived; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getPreferredDropoffDate() { return preferredDropoffDate; }
    public void setPreferredDropoffDate(LocalDate preferredDropoffDate) { this.preferredDropoffDate = preferredDropoffDate; }
    public String getSellerNote() { return sellerNote; }
    public void setSellerNote(String sellerNote) { this.sellerNote = sellerNote; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
