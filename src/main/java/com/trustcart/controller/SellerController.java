package com.trustcart.controller;

import com.trustcart.model.*;
import com.trustcart.repository.CustomerOrderRepository;
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

    public SellerController(SellerRepository sellerRepository, ProductRepository productRepository, CustomerOrderRepository orderRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
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
                        RedirectAttributes redirectAttributes) {
        if (sellerRepository.findByEmailIgnoreCase(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already exists. Try logging in or use another email.");
            return "redirect:/seller/apply";
        }
        Seller seller = new Seller();
        seller.setStoreName(storeName);
        seller.setEmail(email);
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
        seller.setReliabilityScore(0);
        seller.setResponseRateScore(0);
        seller.setComplaintRateScore(0);
        seller.setReturnRateScore(0);
        seller.setGreenComplianceScore(0);
        seller.setStatus(SellerStatus.PENDING);
        sellerRepository.save(seller);
        redirectAttributes.addFlashAttribute("message", "Seller application submitted. Please wait for admin approval.");
        return "redirect:/seller/login";
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
            redirectAttributes.addFlashAttribute("error", "Seller account is not yet approved. Current status: " + seller.getStatus());
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
        product.setProductOrigin(productOrigin == null || productOrigin.isBlank() ? "Pending origin verification" : productOrigin);
        product.setWarrantyPolicy(warrantyPolicy == null || warrantyPolicy.isBlank() ? "7-day buyer protection with refund tracker." : warrantyPolicy);
        product.setSellerVerificationScore(23);
        product.setProductAuthenticityScore(23);
        product.setReviewQualityScore(20);
        product.setDeliveryReliabilityScore(17);
        product.setSustainabilityScore(ecoFriendly ? 10 : 6);
        product.setGreenScore(ecoFriendly ? 86 : 65);
        product.setReturnRiskScore(92);
        product.setTrustScore(Math.min(100, product.getSellerVerificationScore() + product.getProductAuthenticityScore() + product.getReviewQualityScore() + product.getDeliveryReliabilityScore() + product.getSustainabilityScore()));
        product.setReviewSummary("New listing. TrustCart will show only verified buyer reviews and flag suspicious review patterns.");
        product.setRedFlagSummary("Pending admin product review. No public red flags after approval.");
        product.setImageUrl(imageUrl == null || imageUrl.isBlank() ? "https://images.unsplash.com/photo-1516321497487-e288fb19713f?auto=format&fit=crop&w=900&q=80" : imageUrl);
        product.setSubscriptionEligible(subscriptionEligible);
        product.setSubscriptionDiscountPercent(subscriptionEligible ? subscriptionDiscountPercent : 0);
        product.setPhotoAltText(name + " product photo");
        product.setStatus(ProductStatus.PENDING);
        product.setSeller(seller);
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("message", "Product submitted for admin approval.");
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
        redirectAttributes.addFlashAttribute("error", "Please login as an approved seller first.");
        return "redirect:/seller/login";
    }
}
