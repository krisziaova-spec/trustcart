package com.trustcart.controller;

import com.trustcart.model.*;
import com.trustcart.repository.AutoshipSubscriptionRepository;
import com.trustcart.repository.BuyerAccountRepository;
import com.trustcart.repository.CustomerOrderRepository;
import com.trustcart.repository.DiscountCodeRepository;
import com.trustcart.repository.ProductRepository;
import com.trustcart.repository.RefundRequestRepository;
import com.trustcart.service.CartService;
import com.trustcart.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Controller
public class BuyerController {

    public static final String BUYER_SESSION_KEY = "TRUSTCART_BUYER_ID";
    private static final String MARKET_CITY = "TRUSTCART_MARKET_CITY";
    private static final String MARKET_LAT = "TRUSTCART_MARKET_LAT";
    private static final String MARKET_LNG = "TRUSTCART_MARKET_LNG";
    private static final String MARKET_RADIUS = "TRUSTCART_MARKET_RADIUS";

    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;
    private final RefundRequestRepository refundRepository;
    private final BuyerAccountRepository buyerRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final AutoshipSubscriptionRepository autoshipRepository;
    private final CartService cartService;
    private final OrderService orderService;

    public BuyerController(ProductRepository productRepository,
                           CustomerOrderRepository orderRepository,
                           RefundRequestRepository refundRepository,
                           BuyerAccountRepository buyerRepository,
                           DiscountCodeRepository discountCodeRepository,
                           AutoshipSubscriptionRepository autoshipRepository,
                           CartService cartService,
                           OrderService orderService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
        this.buyerRepository = buyerRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.autoshipRepository = autoshipRepository;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) ProductCategory category,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false) String marketCity,
                       @RequestParam(required = false) Double latitude,
                       @RequestParam(required = false) Double longitude,
                       @RequestParam(required = false) Integer radiusKm,
                       @RequestParam(defaultValue = "false") boolean nearbyOnly,
                       @RequestParam(defaultValue = "false") boolean pickupOnly,
                       Model model,
                       HttpSession session) {
        saveMarketIfProvided(session, marketCity, latitude, longitude, radiusKm);

        List<Product> products;
        if (q != null && !q.isBlank()) {
            products = productRepository.searchLiveProducts(ProductStatus.APPROVED, SellerStatus.APPROVED, q.trim());
        } else if (category != null) {
            products = productRepository.findLiveProductsByCategory(ProductStatus.APPROVED, SellerStatus.APPROVED, category);
        } else {
            products = productRepository.findLiveProducts(ProductStatus.APPROVED, SellerStatus.APPROVED);
        }

        Double targetLat = getDoubleSession(session, MARKET_LAT);
        Double targetLng = getDoubleSession(session, MARKET_LNG);
        Integer targetRadius = getIntegerSession(session, MARKET_RADIUS, 5);
        if ((nearbyOnly || pickupOnly) && targetLat != null && targetLng != null) {
            products = products.stream()
                    .filter(product -> product.getSeller().hasCoordinates())
                    .filter(product -> distanceKm(targetLat, targetLng, product.getSeller().getLatitude(), product.getSeller().getLongitude()) <= targetRadius)
                    .filter(product -> !pickupOnly || product.getSeller().isPickupAvailable())
                    .sorted(Comparator.comparingDouble(product -> distanceKm(targetLat, targetLng, product.getSeller().getLatitude(), product.getSeller().getLongitude())))
                    .toList();
        }

        addCommonModel(model, session);
        model.addAttribute("products", products);
        model.addAttribute("categories", ProductCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("query", q);
        model.addAttribute("nearbyOnly", nearbyOnly);
        model.addAttribute("pickupOnly", pickupOnly);
        return "home";
    }

    @PostMapping("/buyer/location")
    public String updateMarketLocation(@RequestParam String marketCity,
                                       @RequestParam Double latitude,
                                       @RequestParam Double longitude,
                                       @RequestParam(defaultValue = "5") Integer radiusKm,
                                       @RequestParam(defaultValue = "false") boolean nearbyOnly,
                                       @RequestParam(defaultValue = "false") boolean pickupOnly,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        saveMarketIfProvided(session, marketCity, latitude, longitude, radiusKm);
        currentBuyer(session).ifPresent(buyer -> {
            buyer.setPreferredCity(marketCity);
            buyer.setPreferredLatitude(latitude);
            buyer.setPreferredLongitude(longitude);
            buyer.setPreferredRadiusKm(radiusKm);
            buyer.setNearbySellerFirst(nearbyOnly);
            buyer.setPickupInterested(pickupOnly);
            buyerRepository.save(buyer);
        });
        redirectAttributes.addFlashAttribute("message", "Target market location updated. Nearby sellers will be prioritized.");
        return "redirect:/?nearbyOnly=" + nearbyOnly + "&pickupOnly=" + pickupOnly;
    }

    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productRepository.findById(id).orElseThrow();
        addCommonModel(model, session);
        model.addAttribute("product", product);
        model.addAttribute("autoshipFrequencies", AutoshipFrequency.values());
        Double targetLat = getDoubleSession(session, MARKET_LAT);
        Double targetLng = getDoubleSession(session, MARKET_LNG);
        if (targetLat != null && targetLng != null && product.getSeller().hasCoordinates()) {
            model.addAttribute("distanceKm", Math.round(distanceKm(targetLat, targetLng, product.getSeller().getLatitude(), product.getSeller().getLongitude()) * 10.0) / 10.0);
        }
        return "product-detail";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!isBuyerLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Please login as a buyer before adding items to cart.");
            return "redirect:/buyer/login";
        }
        cartService.add(session, id, quantity);
        redirectAttributes.addFlashAttribute("message", "Product added to cart.");
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isBuyerLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Buyer login is required to view the cart.");
            return "redirect:/buyer/login";
        }
        addCommonModel(model, session);
        model.addAttribute("summary", cartService.summarize(session, false));
        return "cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCart(@PathVariable Long id, @RequestParam int quantity, HttpSession session) {
        cartService.update(session, id, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeCart(@PathVariable Long id, HttpSession session) {
        cartService.remove(session, id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam(defaultValue = "false") boolean ecoPackaging,
                           @RequestParam(defaultValue = "false") boolean noExtraPlastic,
                           @RequestParam(defaultValue = "false") boolean consolidatedDelivery,
                           @RequestParam(defaultValue = "STANDARD_DELIVERY") DeliveryOption deliveryOption,
                           @RequestParam(required = false) String discountCode,
                           @RequestParam(defaultValue = "0") int pointsToRedeem,
                           Model model,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (!isBuyerLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Buyer login is required before checkout.");
            return "redirect:/buyer/login";
        }
        BuyerAccount buyer = currentBuyer(session).orElse(null);
        int safePoints = buyer == null ? 0 : Math.min(Math.max(0, pointsToRedeem), buyer.getLoyaltyPointsBalance());
        CartSummary summary = cartService.summarize(session, ecoPackaging, noExtraPlastic, consolidatedDelivery, deliveryOption, discountCode, safePoints);
        if (summary.getLines().isEmpty()) {
            return "redirect:/cart";
        }
        addCommonModel(model, session);
        model.addAttribute("summary", summary);
        model.addAttribute("ecoPackaging", ecoPackaging);
        model.addAttribute("noExtraPlastic", noExtraPlastic);
        model.addAttribute("consolidatedDelivery", consolidatedDelivery);
        model.addAttribute("deliveryOption", deliveryOption);
        model.addAttribute("deliveryOptions", DeliveryOption.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("discountCode", discountCode == null ? "" : discountCode);
        model.addAttribute("pointsToRedeem", safePoints);
        model.addAttribute("activeDiscountCodes", discountCodeRepository.findByActiveTrueOrderByCreatedAtDesc());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam String shippingAddress,
                             @RequestParam PaymentMethod paymentMethod,
                             @RequestParam(defaultValue = "STANDARD_DELIVERY") DeliveryOption deliveryOption,
                             @RequestParam(defaultValue = "false") boolean ecoPackaging,
                             @RequestParam(defaultValue = "false") boolean noExtraPlastic,
                             @RequestParam(defaultValue = "false") boolean consolidatedDelivery,
                             @RequestParam(required = false) String discountCode,
                             @RequestParam(defaultValue = "0") int pointsToRedeem,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isBuyerLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Buyer login is required before checkout.");
            return "redirect:/buyer/login";
        }
        String marketLabel = getStringSession(session, MARKET_CITY, "No target market selected");
        CustomerOrder order = orderService.createOrder(session, fullName, email, phone, shippingAddress, paymentMethod, ecoPackaging, noExtraPlastic, consolidatedDelivery, deliveryOption, marketLabel, discountCode, pointsToRedeem);
        redirectAttributes.addFlashAttribute("message", "Order placed successfully.");
        return "redirect:/orders/" + order.getOrderCode() + "/success";
    }

    @GetMapping("/orders/{orderCode}/success")
    public String orderSuccess(@PathVariable String orderCode, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isBuyerLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Buyer login is required to view order details.");
            return "redirect:/buyer/login";
        }
        CustomerOrder order = orderRepository.findByOrderCodeIgnoreCase(orderCode).orElseThrow();
        addCommonModel(model, session);
        model.addAttribute("order", order);
        return "order-success";
    }

    @GetMapping("/track")
    public String track(@RequestParam(required = false) String orderCode,
                        @RequestParam(required = false) String email,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (!isBuyerLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Buyer login is required to track orders.");
            return "redirect:/buyer/login";
        }
        if (orderCode != null && !orderCode.isBlank() && email != null && !email.isBlank()) {
            orderRepository.findByOrderCodeIgnoreCaseAndEmailIgnoreCase(orderCode.trim(), email.trim())
                    .ifPresentOrElse(
                            order -> model.addAttribute("order", order),
                            () -> model.addAttribute("error", "No matching order found. Check the order code and email.")
                    );
        }
        addCommonModel(model, session);
        return "track";
    }

    @GetMapping("/refund")
    public String refundForm(@RequestParam(required = false) String orderCode, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isBuyerLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Buyer login is required to use the Protection Center.");
            return "redirect:/buyer/login";
        }
        addCommonModel(model, session);
        model.addAttribute("orderCode", orderCode);
        return "refund";
    }

    @PostMapping("/refund")
    public String submitRefund(@RequestParam String orderCode,
                               @RequestParam String email,
                               @RequestParam String reason,
                               @RequestParam(required = false) String evidenceUrl,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isBuyerLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Buyer login is required to submit a refund request.");
            return "redirect:/buyer/login";
        }
        RefundRequest refund = new RefundRequest();
        refund.setOrderCode(orderCode.trim());
        refund.setEmail(email.trim());
        refund.setReason(reason.trim());
        refund.setEvidenceUrl(evidenceUrl);
        refund.setStatus(RefundStatus.SUBMITTED);
        orderRepository.findByOrderCodeIgnoreCaseAndEmailIgnoreCase(orderCode.trim(), email.trim())
                .ifPresent(refund::setOrder);
        refundRepository.save(refund);
        redirectAttributes.addFlashAttribute("message", "Refund request submitted. Admin will review it inside the platform.");
        return "redirect:/refund";
    }


    @GetMapping("/autoship")
    public String autoshipList(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        BuyerAccount buyer = currentBuyer(session).orElse(null);
        if (buyer == null) {
            redirectAttributes.addFlashAttribute("error", "Buyer login is required to view subscriptions.");
            return "redirect:/buyer/login";
        }
        addCommonModel(model, session);
        model.addAttribute("subscriptions", autoshipRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId()));
        return "autoship";
    }

    @PostMapping("/autoship/start/{productId}")
    public String startAutoship(@PathVariable Long productId,
                                @RequestParam(defaultValue = "MONTHLY") AutoshipFrequency frequency,
                                @RequestParam(defaultValue = "1") int quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        BuyerAccount buyer = currentBuyer(session).orElse(null);
        if (buyer == null) {
            redirectAttributes.addFlashAttribute("error", "Buyer login is required to create a subscription.");
            return "redirect:/buyer/login";
        }
        Product product = productRepository.findById(productId).orElseThrow();
        if (!product.isSubscriptionEligible()) {
            redirectAttributes.addFlashAttribute("error", "This item is not available for autoshipment.");
            return "redirect:/product/" + productId;
        }
        AutoshipSubscription subscription = new AutoshipSubscription(buyer, product, frequency, quantity);
        autoshipRepository.save(subscription);
        redirectAttributes.addFlashAttribute("message", "Autoshipment created for " + product.getName() + ".");
        return "redirect:/autoship";
    }

    @ModelAttribute("allCategories")
    public List<ProductCategory> allCategories() {
        return Arrays.asList(ProductCategory.values());
    }

    private void addCommonModel(Model model, HttpSession session) {
        model.addAttribute("cartCount", cartService.countItems(session));
        model.addAttribute("buyer", currentBuyer(session).orElse(null));
        model.addAttribute("buyerLoggedIn", isBuyerLoggedIn(session));
        model.addAttribute("marketCity", getStringSession(session, MARKET_CITY, "San Pablo City"));
        model.addAttribute("marketLatitude", getDoubleSession(session, MARKET_LAT) == null ? 14.0683 : getDoubleSession(session, MARKET_LAT));
        model.addAttribute("marketLongitude", getDoubleSession(session, MARKET_LNG) == null ? 121.3256 : getDoubleSession(session, MARKET_LNG));
        model.addAttribute("marketRadius", getIntegerSession(session, MARKET_RADIUS, 5));
        currentBuyer(session).ifPresent(b -> model.addAttribute("buyerSubscriptions", autoshipRepository.findByBuyerIdOrderByCreatedAtDesc(b.getId())));
    }

    private java.util.Optional<BuyerAccount> currentBuyer(HttpSession session) {
        Object buyerId = session.getAttribute(BUYER_SESSION_KEY);
        if (buyerId instanceof Long id) {
            return buyerRepository.findById(id);
        }
        return java.util.Optional.empty();
    }

    private boolean isBuyerLoggedIn(HttpSession session) {
        return currentBuyer(session).isPresent();
    }

    private void saveMarketIfProvided(HttpSession session, String city, Double lat, Double lng, Integer radius) {
        if (city != null && !city.isBlank() && lat != null && lng != null) {
            session.setAttribute(MARKET_CITY, city.trim());
            session.setAttribute(MARKET_LAT, lat);
            session.setAttribute(MARKET_LNG, lng);
            session.setAttribute(MARKET_RADIUS, radius == null ? 5 : Math.max(1, Math.min(radius, 50)));
        }
    }

    private String getStringSession(HttpSession session, String key, String fallback) {
        Object value = session.getAttribute(key);
        return value == null ? fallback : value.toString();
    }

    private Double getDoubleSession(HttpSession session, String key) {
        Object value = session.getAttribute(key);
        if (value instanceof Double d) return d;
        if (value instanceof Number n) return n.doubleValue();
        return null;
    }

    private Integer getIntegerSession(HttpSession session, String key, Integer fallback) {
        Object value = session.getAttribute(key);
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        return fallback;
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double radius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radius * c;
    }
}
