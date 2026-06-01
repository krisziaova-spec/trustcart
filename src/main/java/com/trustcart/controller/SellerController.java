package com.trustcart.controller;

import com.trustcart.model.*;
import com.trustcart.repository.CustomerOrderRepository;
import com.trustcart.repository.DiscountCodeRepository;
import com.trustcart.repository.ProductRepository;
import com.trustcart.repository.SellerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private static final String SELLER_SESSION_KEY = "TRUSTCART_SELLER_ID";

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;
    private final DiscountCodeRepository discountCodeRepository;

    public SellerController(SellerRepository sellerRepository,
                            ProductRepository productRepository,
                            CustomerOrderRepository orderRepository,
                            DiscountCodeRepository discountCodeRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.discountCodeRepository = discountCodeRepository;
    }

    @GetMapping
    public String sellerHome() {
        return "seller-home";
    }

    @GetMapping("/apply")
    public String applyForm(Model model) {
        model.addAttribute("seller", new Seller());
        return "seller-apply";
    }

    @PostMapping("/apply")
    public String apply(@RequestParam String storeName,
                        @RequestParam String email,
                        @RequestParam String phone,
                        @RequestParam String password,
                        @RequestParam String businessType,
                        @RequestParam String sustainabilityBadge,
                        @RequestParam String storeExactAddress,
                        @RequestParam String storeCity,
                        @RequestParam String storeProvince,
                        @RequestParam Double latitude,
                        @RequestParam Double longitude,
                        @RequestParam(defaultValue = "5") Integer serviceRadiusKm,
                        @RequestParam(defaultValue = "false") boolean pickupAvailable,
                        @RequestParam(required = false) String documentProofUrl,
                        @RequestParam(required = false) String locationProofUrl,
                        @RequestParam(required = false) String ecoCommitment,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (sellerRepository.findByEmailIgnoreCase(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already exists. Try logging in or use another email.");
            return "redirect:/seller/apply";
        }
        Seller seller = new Seller();
        seller.setStoreName(storeName);
        seller.setEmail(email.trim());
        seller.setPhone(phone);
        seller.setPassword(password);
        seller.setBusinessType(businessType);
        seller.setSustainabilityBadge(sustainabilityBadge);
        seller.setStoreExactAddress(storeExactAddress);
        seller.setStoreCity(storeCity);
        seller.setStoreProvince(storeProvince);
        seller.setLatitude(latitude);
        seller.setLongitude(longitude);
        seller.setServiceRadiusKm(serviceRadiusKm);
        seller.setPickupAvailable(pickupAvailable);
        seller.setDocumentProofUrl(documentProofUrl);
        seller.setLocationProofUrl(locationProofUrl);
        seller.setEcoCommitment(ecoCommitment);
        seller.setReliabilityScore(92);
        seller.setResponseRateScore(96);
        seller.setComplaintRateScore(95);
        seller.setReturnRateScore(94);
        seller.setGreenComplianceScore(88);
        seller.setStatus(SellerStatus.APPROVED);
        seller.markVerifiedDefaults();
        Seller saved = sellerRepository.save(seller);
        session.setAttribute(SELLER_SESSION_KEY, saved.getId());
        redirectAttributes.addFlashAttribute("message", "Seller account created. You can now manage your shop and publish products.");
        return "redirect:/seller/dashboard";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "seller-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        Seller seller = sellerRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
        if (seller == null || seller.getPassword() == null || !seller.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid seller email or password.");
            return "redirect:/seller/login";
        }
        if (seller.getStatus() != SellerStatus.APPROVED) {
            redirectAttributes.addFlashAttribute("error", "Seller account is currently restricted. Please contact TrustCart support.");
            return "redirect:/seller/login";
        }
        session.setAttribute(SELLER_SESSION_KEY, seller.getId());
        return "redirect:/seller/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(SELLER_SESSION_KEY);
        return "redirect:/seller";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Seller seller = currentSeller(session);
        model.addAttribute("seller", seller);
        model.addAttribute("products", productRepository.findBySellerIdOrderByCreatedAtDesc(seller.getId()));
        model.addAttribute("orders", orderRepository.findTop20ByOrderByCreatedAtDesc());
        model.addAttribute("discountCodes", discountCodeRepository.findBySellerIdOrderByCreatedAtDesc(seller.getId()));
        return "seller-dashboard";
    }

    @GetMapping("/products/new")
    public String newProduct(HttpSession session, Model model) {
        currentSeller(session);
        model.addAttribute("categories", ProductCategory.values());
        return "seller-product-form";
    }

    @PostMapping("/products")
    public String createProduct(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam ProductCategory category,
                                @RequestParam BigDecimal price,
                                @RequestParam int stock,
                                @RequestParam(defaultValue = "false") boolean ecoFriendly,
                                @RequestParam(defaultValue = "false") boolean plasticFreePackaging,
                                @RequestParam(defaultValue = "false") boolean locallySourced,
                                @RequestParam(defaultValue = "false") boolean lowWasteDelivery,
                                @RequestParam(defaultValue = "false") boolean subscriptionEligible,
                                @RequestParam(defaultValue = "5") Integer subscriptionDiscountPercent,
                                @RequestParam String sustainabilityTag,
                                @RequestParam(required = false) String productOrigin,
                                @RequestParam(required = false) String warrantyPolicy,
                                @RequestParam(required = false) String imageUrl,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Seller seller = currentSeller(session);
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        product.setEcoFriendly(ecoFriendly);
        product.setSustainabilityTag(sustainabilityTag);
        product.setPlasticFreePackaging(plasticFreePackaging);
        product.setLocallySourced(locallySourced);
        product.setLowWasteDelivery(lowWasteDelivery);
        product.setProductOrigin(productOrigin == null || productOrigin.isBlank() ? "Seller-declared source, visible for buyer review" : productOrigin);
        product.setWarrantyPolicy(warrantyPolicy == null || warrantyPolicy.isBlank() ? "7-day buyer protection with refund tracker." : warrantyPolicy);
        product.setSellerVerificationScore(25);
        product.setProductAuthenticityScore(23);
        product.setReviewQualityScore(20);
        product.setDeliveryReliabilityScore(17);
        product.setSustainabilityScore(ecoFriendly ? 10 : 6);
        product.setGreenScore(ecoFriendly ? 86 : 65);
        product.setReturnRiskScore(92);
        product.setTrustScore(Math.min(100, product.getSellerVerificationScore() + product.getProductAuthenticityScore() + product.getReviewQualityScore() + product.getDeliveryReliabilityScore() + product.getSustainabilityScore()));
        product.setReviewSummary("New listing. Only verified purchase reviews will be shown after orders are completed.");
        product.setRedFlagSummary("No red flags detected. Buyers are protected if checkout stays inside TrustCart.");
        product.setImageUrl(imageUrl == null || imageUrl.isBlank() ? "https://images.unsplash.com/photo-1516321497487-e288fb19713f?auto=format&fit=crop&w=900&q=80" : imageUrl);
        product.setSubscriptionEligible(subscriptionEligible);
        product.setSubscriptionDiscountPercent(subscriptionEligible ? subscriptionDiscountPercent : 0);
        product.setPhotoAltText(name + " product photo");
        product.setTrustCartShield(true);
        product.setAuthenticItemChecked(true);
        product.setVerifiedReviewsOnly(true);
        product.setSuspiciousReviewFlag(false);
        product.setStatus(ProductStatus.APPROVED);
        product.setSeller(seller);
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("message", "Product published successfully and is now visible in the buyer marketplace.");
        return "redirect:/seller/dashboard";
    }

    @PostMapping("/discounts")
    public String createDiscount(@RequestParam String code,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(defaultValue = "0") BigDecimal minimumSpend,
                                 @RequestParam(defaultValue = "0") Integer percentOff,
                                 @RequestParam(defaultValue = "0") BigDecimal amountOff,
                                 @RequestParam(defaultValue = "0") Integer maxRedemptions,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Seller seller = currentSeller(session);
        String normalizedCode = DiscountCode.normalizeCode(code);
        if (discountCodeRepository.findByCodeIgnoreCase(normalizedCode).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Discount code already exists: " + normalizedCode);
            return "redirect:/seller/dashboard";
        }
        DiscountCode discount = new DiscountCode(normalizedCode, description, minimumSpend, percentOff, amountOff, true);
        discount.setMaxRedemptions(maxRedemptions);
        discount.setSellerId(seller.getId());
        discount.setCreatedBySeller(seller.getStoreName());
        discountCodeRepository.save(discount);
        redirectAttributes.addFlashAttribute("message", "Seller discount code created: " + discount.getCode());
        return "redirect:/seller/dashboard";
    }

    @PostMapping("/discounts/{id}/toggle")
    public String toggleDiscount(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Seller seller = currentSeller(session);
        DiscountCode discount = discountCodeRepository.findById(id).orElseThrow();
        if (!seller.getId().equals(discount.getSellerId())) {
            redirectAttributes.addFlashAttribute("error", "You can only manage discount codes created by your shop.");
            return "redirect:/seller/dashboard";
        }
        discount.setActive(!discount.isActive());
        discountCodeRepository.save(discount);
        redirectAttributes.addFlashAttribute("message", "Discount code updated: " + discount.getCode());
        return "redirect:/seller/dashboard";
    }

    private Seller currentSeller(HttpSession session) {
        Object sellerId = session.getAttribute(SELLER_SESSION_KEY);
        if (sellerId == null) {
            throw new IllegalStateException("Seller login required.");
        }
        return sellerRepository.findById((Long) sellerId).orElseThrow();
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleNotLoggedIn(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Please login as a seller first.");
        return "redirect:/seller/login";
    }
}
