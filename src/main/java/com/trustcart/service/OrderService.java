package com.trustcart.service;

import com.trustcart.model.*;
import com.trustcart.repository.BuyerAccountRepository;
import com.trustcart.repository.CustomerOrderRepository;
import com.trustcart.repository.DiscountCodeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class OrderService {

    private static final String BUYER_SESSION_KEY = "TRUSTCART_BUYER_ID";

    private final CustomerOrderRepository orderRepository;
    private final CartService cartService;
    private final BuyerAccountRepository buyerRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final Random random = new Random();

    public OrderService(CustomerOrderRepository orderRepository,
                        CartService cartService,
                        BuyerAccountRepository buyerRepository,
                        DiscountCodeRepository discountCodeRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.buyerRepository = buyerRepository;
        this.discountCodeRepository = discountCodeRepository;
    }

    @Transactional
    public CustomerOrder createOrder(HttpSession session, String fullName, String email, String phone,
                                     String shippingAddress, PaymentMethod paymentMethod, boolean ecoPackaging) {
        return createOrder(session, fullName, email, phone, shippingAddress, paymentMethod, ecoPackaging, false, false, DeliveryOption.STANDARD_DELIVERY, "No target market selected", null, 0);
    }

    @Transactional
    public CustomerOrder createOrder(HttpSession session, String fullName, String email, String phone,
                                     String shippingAddress, PaymentMethod paymentMethod, boolean ecoPackaging,
                                     boolean noExtraPlastic, boolean consolidatedDelivery) {
        return createOrder(session, fullName, email, phone, shippingAddress, paymentMethod, ecoPackaging, noExtraPlastic, consolidatedDelivery, DeliveryOption.STANDARD_DELIVERY, "No target market selected", null, 0);
    }

    @Transactional
    public CustomerOrder createOrder(HttpSession session, String fullName, String email, String phone,
                                     String shippingAddress, PaymentMethod paymentMethod, boolean ecoPackaging,
                                     boolean noExtraPlastic, boolean consolidatedDelivery,
                                     DeliveryOption deliveryOption, String buyerMarketLocation) {
        return createOrder(session, fullName, email, phone, shippingAddress, paymentMethod, ecoPackaging, noExtraPlastic, consolidatedDelivery, deliveryOption, buyerMarketLocation, null, 0);
    }

    @Transactional
    public CustomerOrder createOrder(HttpSession session, String fullName, String email, String phone,
                                     String shippingAddress, PaymentMethod paymentMethod, boolean ecoPackaging,
                                     boolean noExtraPlastic, boolean consolidatedDelivery,
                                     DeliveryOption deliveryOption, String buyerMarketLocation,
                                     String discountCode, int pointsToRedeem) {
        BuyerAccount buyer = currentBuyer(session);
        int redeemablePoints = buyer == null ? 0 : Math.min(Math.max(0, pointsToRedeem), buyer.getLoyaltyPointsBalance());
        String effectiveDiscountCode = eligibleDiscountForBuyer(discountCode, buyer);
        CartSummary summary = cartService.summarize(session, ecoPackaging, noExtraPlastic, consolidatedDelivery, deliveryOption, effectiveDiscountCode, redeemablePoints);
        if (summary.getLines().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        CustomerOrder order = new CustomerOrder();
        order.setOrderCode(generateOrderCode());
        order.setFullName(fullName);
        order.setEmail(email);
        order.setPhone(phone);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(paymentMethod == PaymentMethod.CASH_ON_DELIVERY ? "Payment pending through Cash on Delivery" : "Paid successfully through TrustCart Pay");
        order.setOrderStatus(OrderStatus.PLACED);
        order.setEcoPackaging(ecoPackaging);
        order.setNoExtraPlastic(noExtraPlastic);
        order.setConsolidatedDelivery(consolidatedDelivery || deliveryOption == DeliveryOption.ECO_CONSOLIDATED_DELIVERY);
        order.setDeliveryOption(deliveryOption);
        order.setBuyerMarketLocation(buyerMarketLocation);
        order.setSubtotal(summary.getSubtotal());
        order.setShippingFee(summary.getShippingFee());
        order.setEcoPackagingFee(summary.getEcoPackagingFee());
        order.setEcoDeliveryDiscount(summary.getEcoDeliveryDiscount());
        order.setDiscount(summary.getDiscount());
        order.setPromoDiscount(summary.getPromoDiscount());
        order.setLoyaltyPointsDiscount(summary.getLoyaltyPointsDiscount());
        order.setLoyaltyPointsRedeemed(summary.getLoyaltyPointsRedeemed());
        order.setDiscountCode(summary.getAppliedDiscountCode());
        order.setDiscountCodeDescription(summary.getDiscountMessage());
        order.setTotal(summary.getTotal());

        for (CartLine line : summary.getLines()) {
            order.addItem(new OrderItem(line.getProduct(), line.getQuantity()));
        }

        if (buyer != null) {
            if (summary.getLoyaltyPointsRedeemed() > 0) {
                buyer.redeemLoyaltyPoints(summary.getLoyaltyPointsRedeemed());
            }
            int earned = summary.getTotal().divide(java.math.BigDecimal.valueOf(20), 0, RoundingMode.DOWN).intValue();
            buyer.addLoyaltyPoints(earned);
            buyer.addLifetimeSpend(summary.getTotal());
            buyerRepository.save(buyer);
            order.setLoyaltyPointsEarned(earned);
            order.setLoyaltyTierAfterOrder(buyer.getLoyaltyTier());
        }

        if (summary.getAppliedDiscountCode() != null && !summary.getAppliedDiscountCode().isBlank() && summary.getPromoDiscount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            discountCodeRepository.findByCodeIgnoreCase(summary.getAppliedDiscountCode()).ifPresent(code -> {
                code.recordRedemption();
                discountCodeRepository.save(code);
            });
        }

        CustomerOrder saved = orderRepository.save(order);
        cartService.clear(session);
        return saved;
    }


    private String eligibleDiscountForBuyer(String discountCode, BuyerAccount buyer) {
        if (discountCode == null || discountCode.isBlank()) {
            return discountCode;
        }
        return discountCodeRepository.findByCodeIgnoreCase(DiscountCode.normalizeCode(discountCode))
                .map(code -> {
                    if (code.isFirstOrderOnly() && buyer != null && buyer.getLifetimeSpend().compareTo(BigDecimal.ZERO) > 0) {
                        return "";
                    }
                    return discountCode;
                })
                .orElse(discountCode);
    }

    private BuyerAccount currentBuyer(HttpSession session) {
        Object buyerId = session.getAttribute(BUYER_SESSION_KEY);
        if (buyerId instanceof Long id) {
            return buyerRepository.findById(id).orElse(null);
        }
        return null;
    }

    private String generateOrderCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "TC-" + date + "-" + (1000 + random.nextInt(9000));
    }
}
