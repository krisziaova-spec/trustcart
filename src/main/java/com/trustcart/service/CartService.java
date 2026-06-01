package com.trustcart.service;

import com.trustcart.model.CartLine;
import com.trustcart.model.CartSummary;
import com.trustcart.model.DeliveryOption;
import com.trustcart.model.DiscountCode;
import com.trustcart.model.Product;
import com.trustcart.model.ProductStatus;
import com.trustcart.model.SellerStatus;
import com.trustcart.repository.DiscountCodeRepository;
import com.trustcart.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "TRUSTCART_CART";
    private final ProductRepository productRepository;
    private final DiscountCodeRepository discountCodeRepository;

    public CartService(ProductRepository productRepository, DiscountCodeRepository discountCodeRepository) {
        this.productRepository = productRepository;
        this.discountCodeRepository = discountCodeRepository;
    }

    @SuppressWarnings("unchecked")
    public Map<Long, Integer> getCart(HttpSession session) {
        Object existing = session.getAttribute(CART_SESSION_KEY);
        if (existing instanceof Map<?, ?>) {
            return (Map<Long, Integer>) existing;
        }
        Map<Long, Integer> cart = new LinkedHashMap<>();
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    public void add(HttpSession session, Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (product.getStatus() != ProductStatus.APPROVED || product.getSeller().getStatus() != SellerStatus.APPROVED) {
            throw new IllegalArgumentException("Product is not available for sale.");
        }
        Map<Long, Integer> cart = getCart(session);
        int safeQuantity = Math.max(1, quantity);
        cart.put(productId, cart.getOrDefault(productId, 0) + safeQuantity);
    }

    public void update(HttpSession session, Long productId, int quantity) {
        Map<Long, Integer> cart = getCart(session);
        if (quantity <= 0) {
            cart.remove(productId);
        } else {
            cart.put(productId, quantity);
        }
    }

    public void remove(HttpSession session, Long productId) {
        getCart(session).remove(productId);
    }

    public void clear(HttpSession session) {
        getCart(session).clear();
    }

    public CartSummary summarize(HttpSession session, boolean ecoPackaging) {
        return summarize(session, ecoPackaging, false, false);
    }

    public CartSummary summarize(HttpSession session, boolean ecoPackaging, boolean noExtraPlastic, boolean consolidatedDelivery) {
        return summarize(session, ecoPackaging, noExtraPlastic, consolidatedDelivery, DeliveryOption.STANDARD_DELIVERY);
    }

    public CartSummary summarize(HttpSession session, boolean ecoPackaging, boolean noExtraPlastic, boolean consolidatedDelivery, DeliveryOption deliveryOption) {
        return summarize(session, ecoPackaging, noExtraPlastic, consolidatedDelivery, deliveryOption, null, 0);
    }

    public CartSummary summarize(HttpSession session, boolean ecoPackaging, boolean noExtraPlastic, boolean consolidatedDelivery, DeliveryOption deliveryOption, String discountCode, int pointsToRedeem) {
        List<CartLine> lines = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : getCart(session).entrySet()) {
            productRepository.findById(entry.getKey()).ifPresent(product -> lines.add(new CartLine(product, entry.getValue())));
        }
        DiscountCode appliedCode = null;
        if (discountCode != null && !discountCode.isBlank()) {
            appliedCode = discountCodeRepository.findByCodeIgnoreCase(DiscountCode.normalizeCode(discountCode)).orElse(null);
        }
        return new CartSummary(lines, ecoPackaging, noExtraPlastic, consolidatedDelivery, deliveryOption, appliedCode, pointsToRedeem);
    }

    public int countItems(HttpSession session) {
        return getCart(session).values().stream().mapToInt(Integer::intValue).sum();
    }
}
