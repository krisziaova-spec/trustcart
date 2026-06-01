package com.trustcart.controller;

import com.trustcart.model.*;
import com.trustcart.repository.*;
import com.trustcart.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BuyerController {
    private final BuyerAccountRepository buyerRepository;
    private final ProductRepository productRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final CustomerOrderRepository orderRepository;
    private final RefundRequestRepository refundRepository;
    private final AutoshipSubscriptionRepository autoshipRepository;
    private final CartService cartService;

    public BuyerController(BuyerAccountRepository buyerRepository, ProductRepository productRepository,
                           DiscountCodeRepository discountCodeRepository, CustomerOrderRepository orderRepository,
                           RefundRequestRepository refundRepository, AutoshipSubscriptionRepository autoshipRepository,
                           CartService cartService) {
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
        this.autoshipRepository = autoshipRepository;
        this.cartService = cartService;
    }

    private void addCommon(Model model, HttpSession session) {
        BuyerAccount buyer = currentBuyer(session);
        model.addAttribute("buyerLoggedIn", buyer != null);
        model.addAttribute("buyer", buyer);
        model.addAttribute("cartCount", cartService.countItems(session));
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("marketCity", session.getAttribute("marketCity") != null ? session.getAttribute("marketCity") : "San Pablo City");
        model.addAttribute("marketLatitude", session.getAttribute("marketLatitude") != null ? session.getAttribute("marketLatitude") : 14.0683);
        model.addAttribute("marketLongitude", session.getAttribute("marketLongitude") != null ? session.getAttribute("marketLongitude") : 121.3256);
        model.addAttribute("marketRadius", session.getAttribute("marketRadius") != null ? session.getAttribute("marketRadius") : 5);
        model.addAttribute("nearbyOnly", session.getAttribute("nearbyOnly") != null ? session.getAttribute("nearbyOnly") : false);
        model.addAttribute("pickupOnly", session.getAttribute("pickupOnly") != null ? session.getAttribute("pickupOnly") : false);
    }

    private BuyerAccount currentBuyer(HttpSession session) {
        Object id = session.getAttribute("buyerId");
        if (id instanceof Long buyerId) return buyerRepository.findById(buyerId).orElse(null);
        return null;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       @RequestParam(required = false) ProductCategory category,
                       @RequestParam(required = false) Boolean nearbyOnly,
                       @RequestParam(required = false) Boolean pickupOnly,
                       Model model, HttpSession session) {
        addCommon(model, session);
        if (nearbyOnly != null) session.setAttribute("nearbyOnly", nearbyOnly);
        if (pickupOnly != null) session.setAttribute("pickupOnly", pickupOnly);
        List<Product> products;
        if (category != null) {
            products = productRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, "APPROVED");
        } else if (q != null && !q.isBlank()) {
            products = productRepository.findByNameContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(q.trim(), "APPROVED");
        } else {
            products = productRepository.findByStatusOrderByCreatedAtDesc("APPROVED");
        }
        // Keep homepage clean but make all results accessible.
        model.addAttribute("products", products.stream().limit(24).collect(Collectors.toList()));
        model.addAttribute("query", q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("discounts", discountCodeRepository.findByActiveTrueOrderByCreatedAtDesc().stream().limit(4).collect(Collectors.toList()));
        return "home";
    }

    @PostMapping("/buyer/location")
    public String setLocation(@RequestParam String marketCity,
                              @RequestParam Double latitude,
                              @RequestParam Double longitude,
                              @RequestParam Integer radiusKm,
                              @RequestParam(required = false, defaultValue = "false") boolean nearbyOnly,
                              @RequestParam(required = false, defaultValue = "false") boolean pickupOnly,
                              HttpSession session) {
        session.setAttribute("marketCity", marketCity);
        session.setAttribute("marketLatitude", latitude);
        session.setAttribute("marketLongitude", longitude);
        session.setAttribute("marketRadius", radiusKm);
        session.setAttribute("nearbyOnly", nearbyOnly);
        session.setAttribute("pickupOnly", pickupOnly);
        return "redirect:/";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productRepository.findById(id).orElseThrow();
        addCommon(model, session);
        model.addAttribute("product", product);
        model.addAttribute("related", productRepository.findByCategoryAndStatusOrderByCreatedAtDesc(product.getCategory(), "APPROVED").stream().filter(p -> !Objects.equals(p.getId(), id)).limit(4).toList());
        return "product-detail";
    }

    @GetMapping("/buyer/login")
    public String buyerLogin(Model model, HttpSession session) {
        addCommon(model, session);
        return "buyer-login";
    }

    @PostMapping("/buyer/login")
    public String doBuyerLogin(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes ra) {
        Optional<BuyerAccount> buyer = buyerRepository.findByEmailIgnoreCase(email);
        if (buyer.isPresent() && Objects.equals(buyer.get().getPassword(), password)) {
            session.setAttribute("buyerId", buyer.get().getId());
            return "redirect:/";
        }
        ra.addFlashAttribute("error", "Invalid buyer email or password.");
        return "redirect:/buyer/login";
    }

    @GetMapping("/buyer/register")
    public String register(Model model, HttpSession session) {
        addCommon(model, session);
        return "buyer-register";
    }

    @PostMapping("/buyer/register")
    public String doRegister(@RequestParam String fullName, @RequestParam String email, @RequestParam String password,
                             @RequestParam(required = false) String phone, @RequestParam(required = false) String defaultAddress,
                             HttpSession session, RedirectAttributes ra) {
        if (buyerRepository.existsByEmailIgnoreCase(email)) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/buyer/register";
        }
        BuyerAccount buyer = new BuyerAccount();
        buyer.setFullName(fullName);
        buyer.setEmail(email);
        buyer.setPassword(password);
        buyer.setPhone(phone);
        buyer.setDefaultAddress(defaultAddress);
        buyer.setPreferredCity("San Pablo City");
        buyer.setPreferredLatitude(14.0683);
        buyer.setPreferredLongitude(121.3256);
        buyerRepository.save(buyer);
        session.setAttribute("buyerId", buyer.getId());
        return "redirect:/";
    }

    @PostMapping("/buyer/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("buyerId");
        return "redirect:/";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id, @RequestParam(defaultValue = "1") int quantity, HttpSession session, RedirectAttributes ra) {
        if (currentBuyer(session) == null) {
            ra.addFlashAttribute("error", "Please login as buyer before adding to cart.");
            return "redirect:/buyer/login";
        }
        Map<Long, Integer> cart = cartService.getCart(session);
        cart.put(id, cart.getOrDefault(id, 0) + Math.max(1, quantity));
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        addCommon(model, session);
        Map<Long, Integer> cart = cartService.getCart(session);
        List<Map<String, Object>> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> e : cart.entrySet()) {
            Product p = productRepository.findById(e.getKey()).orElse(null);
            if (p != null) {
                BigDecimal line = p.getPrice().multiply(BigDecimal.valueOf(e.getValue()));
                subtotal = subtotal.add(line);
                Map<String,Object> row = new HashMap<>();
                row.put("product", p);
                row.put("qty", e.getValue());
                row.put("line", line);
                items.add(row);
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("subtotal", subtotal);
        return "cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeItem(@PathVariable Long id, HttpSession session) {
        cartService.getCart(session).remove(id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        cart(model, session);
        model.addAttribute("discounts", discountCodeRepository.findByActiveTrueOrderByCreatedAtDesc());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String fullName, @RequestParam String email, @RequestParam String phone,
                             @RequestParam String shippingAddress, @RequestParam String paymentMethod,
                             @RequestParam(required = false) String discountCode,
                             @RequestParam(required = false, defaultValue = "false") boolean ecoPackaging,
                             @RequestParam(required = false, defaultValue = "false") boolean noExtraPlastic,
                             @RequestParam(required = false, defaultValue = "false") boolean consolidatedDelivery,
                             @RequestParam(required = false, defaultValue = "STANDARD_DELIVERY") String deliveryOption,
                             @RequestParam(required = false, defaultValue = "0") int pointsToRedeem,
                             HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        Map<Long, Integer> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/cart";
        }
        CustomerOrder order = new CustomerOrder();
        order.setOrderCode("TC-" + System.currentTimeMillis());
        order.setFullName(fullName);
        order.setEmail(email);
        order.setPhone(phone);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("Payment option selected");
        order.setEcoPackaging(ecoPackaging);
        order.setNoExtraPlastic(noExtraPlastic);
        order.setConsolidatedDelivery(consolidatedDelivery);
        order.setDeliveryOption(deliveryOption);
        order.setBuyerMarketLocation(String.valueOf(session.getAttribute("marketCity") != null ? session.getAttribute("marketCity") : "San Pablo City"));
        order.setPlatformProtectionNote("For buyer protection, checkout, payment, tracking, and refund requests must remain inside TrustCart.");

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> e : cart.entrySet()) {
            Product p = productRepository.findById(e.getKey()).orElse(null);
            if (p != null) {
                int qty = Math.max(1, e.getValue());
                BigDecimal line = p.getPrice().multiply(BigDecimal.valueOf(qty));
                subtotal = subtotal.add(line);
                OrderItem item = new OrderItem();
                item.setProduct(p);
                item.setProductName(p.getName());
                item.setSellerName(p.getSeller().getStoreName());
                item.setQuantity(qty);
                item.setUnitPrice(p.getPrice());
                item.setLineTotal(line);
                order.addItem(item);
                p.setStock(Math.max(0, p.getStock() - qty));
                productRepository.save(p);
            }
        }
        BigDecimal shipping = deliveryOption.equals("PICKUP_HUB") ? BigDecimal.ZERO : BigDecimal.valueOf(80);
        BigDecimal ecoFee = ecoPackaging ? BigDecimal.valueOf(10) : BigDecimal.ZERO;
        BigDecimal ecoDiscount = consolidatedDelivery ? BigDecimal.valueOf(20) : BigDecimal.ZERO;
        BigDecimal promoDiscount = calculateDiscount(discountCode, subtotal, email);
        if (promoDiscount.compareTo(BigDecimal.ZERO) > 0) {
            discountCodeRepository.findByCodeIgnoreCase(discountCode).ifPresent(dc -> {
                dc.setTimesRedeemed(dc.getTimesRedeemed() + 1);
                discountCodeRepository.save(dc);
                order.setDiscountCode(dc.getCode());
                order.setDiscountCodeDescription(dc.getDescription());
            });
        }
        int safeRedeem = Math.min(pointsToRedeem, buyer.getLoyaltyPointsBalance());
        BigDecimal pointsDiscount = BigDecimal.valueOf(safeRedeem).divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(shipping).add(ecoFee).subtract(ecoDiscount).subtract(promoDiscount).subtract(pointsDiscount);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
        int earned = subtotal.divide(BigDecimal.valueOf(20), 0, RoundingMode.DOWN).intValue();
        buyer.setLoyaltyPointsBalance(buyer.getLoyaltyPointsBalance() - safeRedeem + earned);
        buyer.setLifetimeLoyaltyPoints(buyer.getLifetimeLoyaltyPoints() + earned);
        buyer.setLifetimeSpend(buyer.getLifetimeSpend().add(total));
        if (buyer.getLifetimeSpend().compareTo(BigDecimal.valueOf(5000)) > 0) buyer.setLoyaltyTier("TrustCart Gold Green Member");
        buyerRepository.save(buyer);

        order.setSubtotal(subtotal);
        order.setShippingFee(shipping);
        order.setEcoPackagingFee(ecoFee);
        order.setEcoDeliveryDiscount(ecoDiscount);
        order.setDiscount(promoDiscount.add(pointsDiscount));
        order.setPromoDiscount(promoDiscount);
        order.setLoyaltyPointsDiscount(pointsDiscount);
        order.setLoyaltyPointsRedeemed(safeRedeem);
        order.setLoyaltyPointsEarned(earned);
        order.setLoyaltyTierAfterOrder(buyer.getLoyaltyTier());
        order.setTotal(total);
        orderRepository.save(order);
        cartService.clear(session);
        return "redirect:/order-success?code=" + order.getOrderCode();
    }

    private BigDecimal calculateDiscount(String code, BigDecimal subtotal, String email) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;
        Optional<DiscountCode> optional = discountCodeRepository.findByCodeIgnoreCase(code.trim());
        if (optional.isEmpty()) return BigDecimal.ZERO;
        DiscountCode dc = optional.get();
        if (!dc.isActive()) return BigDecimal.ZERO;
        if (dc.getMinimumSpend() != null && subtotal.compareTo(dc.getMinimumSpend()) < 0) return BigDecimal.ZERO;
        if (dc.isFirstOrderOnly() && orderRepository.countByEmailIgnoreCase(email) > 0) return BigDecimal.ZERO;
        BigDecimal percent = subtotal.multiply(BigDecimal.valueOf(dc.getPercentOff())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal amount = dc.getAmountOff() == null ? BigDecimal.ZERO : dc.getAmountOff();
        return percent.add(amount).min(subtotal);
    }

    @GetMapping("/order-success")
    public String success(@RequestParam String code, Model model, HttpSession session) {
        addCommon(model, session);
        orderRepository.findByOrderCodeIgnoreCase(code).ifPresent(o -> model.addAttribute("order", o));
        return "order-success";
    }

    @GetMapping("/track")
    public String track(@RequestParam(required = false) String code, Model model, HttpSession session) {
        addCommon(model, session);
        if (code != null && !code.isBlank()) {
            orderRepository.findByOrderCodeIgnoreCase(code).ifPresent(o -> model.addAttribute("order", o));
        }
        return "track";
    }

    @GetMapping("/refund")
    public String refund(Model model, HttpSession session) {
        addCommon(model, session);
        return "refund";
    }

    @PostMapping("/refund")
    public String doRefund(@RequestParam String orderCode, @RequestParam String email,
                           @RequestParam String reason, @RequestParam(required = false) String evidenceUrl,
                           RedirectAttributes ra) {
        RefundRequest r = new RefundRequest();
        r.setOrderCode(orderCode);
        r.setEmail(email);
        r.setReason(reason);
        r.setEvidenceUrl(evidenceUrl);
        orderRepository.findByOrderCodeIgnoreCase(orderCode).ifPresent(r::setOrder);
        refundRepository.save(r);
        ra.addFlashAttribute("success", "Refund request submitted. Status: SUBMITTED");
        return "redirect:/refund";
    }

    @GetMapping("/autoship")
    public String autoship(Model model, HttpSession session) {
        addCommon(model, session);
        model.addAttribute("eligibleProducts", productRepository.findByStatusOrderByCreatedAtDesc("APPROVED").stream().filter(Product::isSubscriptionEligible).limit(20).toList());
        BuyerAccount buyer = currentBuyer(session);
        model.addAttribute("subscriptions", buyer == null ? List.of() : autoshipRepository.findByBuyerOrderByCreatedAtDesc(buyer));
        return "autoship";
    }

    @PostMapping("/autoship/create")
    public String createAutoship(@RequestParam Long productId, @RequestParam String frequency, @RequestParam(defaultValue = "1") int quantity,
                                 HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        Product product = productRepository.findById(productId).orElseThrow();
        AutoshipSubscription sub = new AutoshipSubscription();
        sub.setBuyer(buyer);
        sub.setProduct(product);
        sub.setFrequency(frequency);
        sub.setQuantity(quantity);
        sub.setRecurringPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        sub.setSubscriptionDiscountPercent(product.getSubscriptionDiscountPercent());
        sub.setNextShipmentDate(LocalDate.now().plusMonths(1));
        sub.setProtectionNote("Autoship remains protected inside TrustCart. Seller exact address is not shown to buyers.");
        autoshipRepository.save(sub);
        ra.addFlashAttribute("success", "Autoship subscription created.");
        return "redirect:/autoship";
    }

    @GetMapping("/try-on")
    public String tryOn(Model model, HttpSession session) {
        addCommon(model, session);
        List<Product> items = productRepository.findByTryOnEligibleTrueAndStatusOrderByNameAsc("APPROVED");
        model.addAttribute("tryOnItems", items);
        model.addAttribute("menItems", items.stream().filter(p -> "MEN".equalsIgnoreCase(p.getTryOnGender())).toList());
        model.addAttribute("womenItems", items.stream().filter(p -> "WOMEN".equalsIgnoreCase(p.getTryOnGender())).toList());
        return "try-on";
    }
}
