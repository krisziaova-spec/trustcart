package com.trustcart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SupportTicket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String ticketCode;
    private String type = "GENERAL"; // GENERAL, PRODUCT_REPORT, SELLER_REPORT, BUYER_REPORT, REFUND
    private String sourceRole = "BUYER"; // BUYER, SELLER, ADMIN
    private String status = "OPEN"; // OPEN, INVESTIGATING, RESOLVED, CLOSED
    private String priority = "NORMAL";
    private String reporterName;
    private String reporterEmail;
    @Column(length = 1200)
    private String subject;
    @Column(length = 2500)
    private String message;
    private String attachmentUrl;
    private String attachmentName;
    @ManyToOne
    private Product product;
    @ManyToOne
    private Seller seller;
    @ManyToOne
    private BuyerAccount buyer;
    private String reportedBuyerEmail;
    @Column(length = 2500)
    private String adminNote;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSourceRole() { return sourceRole; }
    public void setSourceRole(String sourceRole) { this.sourceRole = sourceRole; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getReporterEmail() { return reporterEmail; }
    public void setReporterEmail(String reporterEmail) { this.reporterEmail = reporterEmail; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }
    public BuyerAccount getBuyer() { return buyer; }
    public void setBuyer(BuyerAccount buyer) { this.buyer = buyer; }
    public String getReportedBuyerEmail() { return reportedBuyerEmail; }
    public void setReportedBuyerEmail(String reportedBuyerEmail) { this.reportedBuyerEmail = reportedBuyerEmail; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
