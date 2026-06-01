package com.trustcart.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CustomerOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String orderCode;
    private String fullName;
    private String email;
    private String phone;
    @Column(length = 1000)
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus = "PLACED";
    private boolean ecoPackaging;
    private boolean noExtraPlastic;
    private boolean consolidatedDelivery;
    private String deliveryOption = "STANDARD_DELIVERY";
    private String buyerMarketLocation;
    @Column(length = 1000)
    private String platformProtectionNote;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal shippingFee = BigDecimal.ZERO;
    private BigDecimal ecoPackagingFee = BigDecimal.ZERO;
    private BigDecimal ecoDeliveryDiscount = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal promoDiscount = BigDecimal.ZERO;
    private BigDecimal loyaltyPointsDiscount = BigDecimal.ZERO;
    private String discountCode;
    private String discountCodeDescription;
    private Integer loyaltyPointsEarned = 0;
    private Integer loyaltyPointsRedeemed = 0;
    private String loyaltyTierAfterOrder;
    private BigDecimal total = BigDecimal.ZERO;
    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public boolean isEcoPackaging() { return ecoPackaging; }
    public void setEcoPackaging(boolean ecoPackaging) { this.ecoPackaging = ecoPackaging; }
    public boolean isNoExtraPlastic() { return noExtraPlastic; }
    public void setNoExtraPlastic(boolean noExtraPlastic) { this.noExtraPlastic = noExtraPlastic; }
    public boolean isConsolidatedDelivery() { return consolidatedDelivery; }
    public void setConsolidatedDelivery(boolean consolidatedDelivery) { this.consolidatedDelivery = consolidatedDelivery; }
    public String getDeliveryOption() { return deliveryOption; }
    public void setDeliveryOption(String deliveryOption) { this.deliveryOption = deliveryOption; }
    public String getBuyerMarketLocation() { return buyerMarketLocation; }
    public void setBuyerMarketLocation(String buyerMarketLocation) { this.buyerMarketLocation = buyerMarketLocation; }
    public String getPlatformProtectionNote() { return platformProtectionNote; }
    public void setPlatformProtectionNote(String platformProtectionNote) { this.platformProtectionNote = platformProtectionNote; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
    public BigDecimal getEcoPackagingFee() { return ecoPackagingFee; }
    public void setEcoPackagingFee(BigDecimal ecoPackagingFee) { this.ecoPackagingFee = ecoPackagingFee; }
    public BigDecimal getEcoDeliveryDiscount() { return ecoDeliveryDiscount; }
    public void setEcoDeliveryDiscount(BigDecimal ecoDeliveryDiscount) { this.ecoDeliveryDiscount = ecoDeliveryDiscount; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public BigDecimal getPromoDiscount() { return promoDiscount; }
    public void setPromoDiscount(BigDecimal promoDiscount) { this.promoDiscount = promoDiscount; }
    public BigDecimal getLoyaltyPointsDiscount() { return loyaltyPointsDiscount; }
    public void setLoyaltyPointsDiscount(BigDecimal loyaltyPointsDiscount) { this.loyaltyPointsDiscount = loyaltyPointsDiscount; }
    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
    public String getDiscountCodeDescription() { return discountCodeDescription; }
    public void setDiscountCodeDescription(String discountCodeDescription) { this.discountCodeDescription = discountCodeDescription; }
    public Integer getLoyaltyPointsEarned() { return loyaltyPointsEarned; }
    public void setLoyaltyPointsEarned(Integer loyaltyPointsEarned) { this.loyaltyPointsEarned = loyaltyPointsEarned; }
    public Integer getLoyaltyPointsRedeemed() { return loyaltyPointsRedeemed; }
    public void setLoyaltyPointsRedeemed(Integer loyaltyPointsRedeemed) { this.loyaltyPointsRedeemed = loyaltyPointsRedeemed; }
    public String getLoyaltyTierAfterOrder() { return loyaltyTierAfterOrder; }
    public void setLoyaltyTierAfterOrder(String loyaltyTierAfterOrder) { this.loyaltyTierAfterOrder = loyaltyTierAfterOrder; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
