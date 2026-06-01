package com.trustcart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class RefundRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderCode;
    private String email;

    @Column(length = 1500)
    private String reason;

    private String evidenceUrl;

    @Enumerated(EnumType.STRING)
    private RefundStatus status = RefundStatus.SUBMITTED;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private CustomerOrder order;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getEvidenceUrl() { return evidenceUrl; }
    public void setEvidenceUrl(String evidenceUrl) { this.evidenceUrl = evidenceUrl; }
    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public CustomerOrder getOrder() { return order; }
    public void setOrder(CustomerOrder order) { this.order = order; }
}
