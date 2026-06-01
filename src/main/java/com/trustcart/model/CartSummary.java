package com.trustcart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CartSummary {
    private final List<CartLine> lines;
    private final BigDecimal subtotal;
    private final BigDecimal shippingFee;
    private final BigDecimal ecoPackagingFee;
    private final BigDecimal ecoDeliveryDiscount;
    private final BigDecimal discount;
    private final BigDecimal promoDiscount;
    private final BigDecimal loyaltyPointsDiscount;
    private final BigDecimal total;
    private final boolean ecoPackaging;
    private final boolean noExtraPlastic;
    private final boolean consolidatedDelivery;
    private final DeliveryOption deliveryOption;
    private final String appliedDiscountCode;
    private final String discountMessage;
    private final int loyaltyPointsRedeemed;

    public CartSummary(List<CartLine> lines, boolean ecoPackaging) {
        this(lines, ecoPackaging, false, false, DeliveryOption.STANDARD_DELIVERY, null, 0);
    }

    public CartSummary(List<CartLine> lines, boolean ecoPackaging, boolean noExtraPlastic, boolean consolidatedDelivery) {
        this(lines, ecoPackaging, noExtraPlastic, consolidatedDelivery, consolidatedDelivery ? DeliveryOption.ECO_CONSOLIDATED_DELIVERY : DeliveryOption.STANDARD_DELIVERY, null, 0);
    }

    public CartSummary(List<CartLine> lines, boolean ecoPackaging, boolean noExtraPlastic, boolean consolidatedDelivery, DeliveryOption deliveryOption) {
        this(lines, ecoPackaging, noExtraPlastic, consolidatedDelivery, deliveryOption, null, 0);
    }

    public CartSummary(List<CartLine> lines, boolean ecoPackaging, boolean noExtraPlastic, boolean consolidatedDelivery, DeliveryOption deliveryOption, DiscountCode discountCode, int loyaltyPointsToRedeem) {
        this.lines = lines;
        this.ecoPackaging = ecoPackaging;
        this.noExtraPlastic = noExtraPlastic;
        this.consolidatedDelivery = consolidatedDelivery;
        this.deliveryOption = deliveryOption == null ? DeliveryOption.STANDARD_DELIVERY : deliveryOption;
        this.subtotal = lines.stream()
                .map(CartLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal baseShipping = lines.isEmpty() ? BigDecimal.ZERO : BigDecimal.valueOf(75);
        if (this.deliveryOption == DeliveryOption.TRUSTCART_PICKUP_HUB) {
            baseShipping = BigDecimal.valueOf(25);
        }
        this.ecoDeliveryDiscount = (!lines.isEmpty() && (consolidatedDelivery || this.deliveryOption == DeliveryOption.ECO_CONSOLIDATED_DELIVERY)) ? BigDecimal.valueOf(20) : BigDecimal.ZERO;
        this.shippingFee = baseShipping.subtract(ecoDeliveryDiscount).max(BigDecimal.ZERO);
        this.ecoPackagingFee = (!lines.isEmpty() && ecoPackaging) ? BigDecimal.valueOf(15) : BigDecimal.ZERO;
        this.discount = subtotal.compareTo(BigDecimal.valueOf(1500)) >= 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;

        if (discountCode != null && discountCode.isCurrentlyUsable(subtotal)) {
            this.promoDiscount = discountCode.calculateDiscount(subtotal);
            this.appliedDiscountCode = discountCode.getCode();
            this.discountMessage = discountCode.getDisplayLabel() + " applied.";
        } else if (discountCode != null) {
            this.promoDiscount = BigDecimal.ZERO;
            this.appliedDiscountCode = discountCode.getCode();
            this.discountMessage = "Code is not eligible for this order.";
        } else {
            this.promoDiscount = BigDecimal.ZERO;
            this.appliedDiscountCode = "";
            this.discountMessage = "";
        }

        int safePoints = Math.max(0, loyaltyPointsToRedeem);
        BigDecimal pointsPesoValue = BigDecimal.valueOf(safePoints).divide(BigDecimal.valueOf(10), 2, RoundingMode.DOWN);
        BigDecimal maxPointsDiscount = subtotal.multiply(BigDecimal.valueOf(0.20)).setScale(2, RoundingMode.DOWN);
        this.loyaltyPointsDiscount = pointsPesoValue.min(maxPointsDiscount).max(BigDecimal.ZERO);
        this.loyaltyPointsRedeemed = this.loyaltyPointsDiscount.multiply(BigDecimal.valueOf(10)).intValue();

        this.total = subtotal.add(shippingFee).add(ecoPackagingFee).subtract(discount).subtract(promoDiscount).subtract(loyaltyPointsDiscount).max(BigDecimal.ZERO);
    }

    public List<CartLine> getLines() { return lines; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public BigDecimal getEcoPackagingFee() { return ecoPackagingFee; }
    public BigDecimal getEcoDeliveryDiscount() { return ecoDeliveryDiscount; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getPromoDiscount() { return promoDiscount; }
    public BigDecimal getLoyaltyPointsDiscount() { return loyaltyPointsDiscount; }
    public BigDecimal getTotal() { return total; }
    public boolean isEcoPackaging() { return ecoPackaging; }
    public boolean isNoExtraPlastic() { return noExtraPlastic; }
    public boolean isConsolidatedDelivery() { return consolidatedDelivery; }
    public DeliveryOption getDeliveryOption() { return deliveryOption; }
    public String getAppliedDiscountCode() { return appliedDiscountCode; }
    public String getDiscountMessage() { return discountMessage; }
    public int getLoyaltyPointsRedeemed() { return loyaltyPointsRedeemed; }
    public int getEstimatedPointsEarned() { return total.divide(BigDecimal.valueOf(20), 0, RoundingMode.DOWN).intValue(); }
    public int getTotalItems() { return lines.stream().mapToInt(CartLine::getQuantity).sum(); }
}
