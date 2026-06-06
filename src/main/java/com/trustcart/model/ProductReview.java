package com.trustcart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ProductReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Product product;
    @ManyToOne
    private CustomerOrder customerOrder;
    @ManyToOne
    private BuyerAccount buyer;
    @ManyToOne
    private Seller seller;

    private int rating = 5;
    @Column(length = 2500)
    private String reviewText;
    private boolean verifiedPurchase = true;
    private String attachmentUrl;
    private String attachmentName;
    private String status = "PUBLISHED"; // PUBLISHED, PENDING_REVIEW, FLAGGED, HIDDEN, REMOVED
    @Column(length = 1200)
    private String moderationNote;
    @Column(length = 1500)
    private String adminDecisionNote;
    private String moderatedBy;
    private LocalDateTime moderatedAt;
    private boolean appealOpen = false;
    @Column(length = 1200)
    private String appealReason;
    private String appealProofUrl;
    private String appealProofName;
    private LocalDateTime appealedAt;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public CustomerOrder getCustomerOrder() { return customerOrder; }
    public void setCustomerOrder(CustomerOrder customerOrder) { this.customerOrder = customerOrder; }
    public BuyerAccount getBuyer() { return buyer; }
    public void setBuyer(BuyerAccount buyer) { this.buyer = buyer; }
    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public boolean isVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getModerationNote() { return moderationNote; }
    public void setModerationNote(String moderationNote) { this.moderationNote = moderationNote; }
    public String getAdminDecisionNote() { return adminDecisionNote; }
    public void setAdminDecisionNote(String adminDecisionNote) { this.adminDecisionNote = adminDecisionNote; }
    public String getModeratedBy() { return moderatedBy; }
    public void setModeratedBy(String moderatedBy) { this.moderatedBy = moderatedBy; }
    public LocalDateTime getModeratedAt() { return moderatedAt; }
    public void setModeratedAt(LocalDateTime moderatedAt) { this.moderatedAt = moderatedAt; }
    public boolean isAppealOpen() { return appealOpen; }
    public void setAppealOpen(boolean appealOpen) { this.appealOpen = appealOpen; }
    public String getAppealReason() { return appealReason; }
    public void setAppealReason(String appealReason) { this.appealReason = appealReason; }
    public String getAppealProofUrl() { return appealProofUrl; }
    public void setAppealProofUrl(String appealProofUrl) { this.appealProofUrl = appealProofUrl; }
    public String getAppealProofName() { return appealProofName; }
    public void setAppealProofName(String appealProofName) { this.appealProofName = appealProofName; }
    public LocalDateTime getAppealedAt() { return appealedAt; }
    public void setAppealedAt(LocalDateTime appealedAt) { this.appealedAt = appealedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
