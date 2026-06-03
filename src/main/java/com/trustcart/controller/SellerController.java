package com.trustcart.controller;

import com.trustcart.model.CustomerOrder;
import com.trustcart.model.DiscountCode;
import com.trustcart.model.OrderItem;
import com.trustcart.model.Product;
import com.trustcart.model.ProductCategory;
import com.trustcart.model.Seller;
import com.trustcart.model.SupportTicket;
import com.trustcart.model.BuyerAccount;
import com.trustcart.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/seller")
public class SellerController {
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final CustomerOrderRepository orderRepository;
    private final SupportTicketRepository ticketRepository;
    private final BuyerAccountRepository buyerRepository;

    public SellerController(SellerRepository sellerRepository, ProductRepository productRepository,
                            DiscountCodeRepository discountCodeRepository, CustomerOrderRepository orderRepository,
                            SupportTicketRepository ticketRepository, BuyerAccountRepository buyerRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.buyerRepository = buyerRepository;
    }


    private String saveUpload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String originalName = file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename();
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0 && dot < originalName.length() - 1) {
            extension = originalName.substring(dot).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9.]", "");
        }
        if (extension.isBlank()) extension = ".jpg";
        Path uploadDir = Path.of("uploads");
        Files.createDirectories(uploadDir);
        String fileName = UUID.randomUUID() + extension;
        Path destination = uploadDir.resolve(fileName).normalize();
        file.transferTo(destination.toFile());
        return "/uploads/" + fileName;
    }

    private String firstNonBlank(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }
    private Seller currentSeller(HttpSession session) {
        Object id = session.getAttribute("sellerId");
        if (id instanceof Long sellerId) return sellerRepository.findById(sellerId).orElse(null);
        return null;
    }

    private void sellerCommon(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        model.addAttribute("sellerLoggedIn", seller != null);
        model.addAttribute("seller", seller);
        model.addAttribute("categories", ProductCategory.values());
    }

    @GetMapping
    public String sellerCentre(Model model, HttpSession session) {
        sellerCommon(model, session);
        return "seller-centre";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        sellerCommon(model, session);
        return "seller-login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes ra) {
        Optional<Seller> seller = sellerRepository.findByEmailIgnoreCase(email);
        if (seller.isPresent() && Objects.equals(seller.get().getPassword(), password)) {
            Seller account = seller.get();
            if (!("APPROVED".equalsIgnoreCase(account.getStatus()) || "ACTIVE".equalsIgnoreCase(account.getStatus()))) {
                ra.addFlashAttribute("error", "Your seller account is currently " + account.getStatus() + ". Please wait for admin approval or contact TrustCart support.");
                return "redirect:/seller/login";
            }
            session.setAttribute("sellerId", account.getId());
            return "redirect:/seller/dashboard";
        }
        ra.addFlashAttribute("error", "Invalid seller email or password.");
        return "redirect:/seller/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("sellerId");
        return "redirect:/seller";
    }

    @GetMapping("/apply")
    public String apply(Model model, HttpSession session) {
        sellerCommon(model, session);
        return "seller-apply";
    }

    @PostMapping("/apply")
    public String doApply(@RequestParam String storeName, @RequestParam String email, @RequestParam String password,
                          @RequestParam String businessType, @RequestParam String storeExactAddress,
                          @RequestParam String storeCity, @RequestParam String storeProvince,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) Double latitude,
                          @RequestParam(required = false) Double longitude,
                          @RequestParam(required = false, defaultValue = "SELLER") String fulfillmentPreference,
                          HttpSession session, RedirectAttributes ra) {
        if (sellerRepository.existsByEmailIgnoreCase(email)) {
            ra.addFlashAttribute("error", "Seller email already exists.");
            return "redirect:/seller/apply";
        }
        Seller seller = new Seller();
        seller.setStoreName(storeName);
        seller.setEmail(email);
        seller.setPassword(password);
        seller.setBusinessType(businessType);
        seller.setPhone(phone);
        seller.setStoreExactAddress(storeExactAddress);
        seller.setStoreCity(storeCity);
        seller.setStoreProvince(storeProvince);
        seller.setLatitude(latitude == null ? 14.0683 : latitude);
        seller.setLongitude(longitude == null ? 121.3256 : longitude);
        seller.setStatus("PENDING");
        seller.setStoreLocationVerified(false);
        seller.setApprovedAt(null);
        seller.setApprovedBy(null);
        seller.setBusinessVerified(false);
        seller.setIdentityVerified(false);
        seller.setDocumentVerified(false);
        seller.setProductComplianceChecked(false);
        seller.setRequirementsStatus("REQUIREMENTS_SENT");
        seller.setRequirementsNote("Email sent: please submit valid government ID, proof of address, business permit if applicable, and selfie with ID. Admin approval is required before login.");
        seller.setFulfillmentPreference(fulfillmentPreference);
        seller.setCanUseFbt(false);
        seller.setStoreDescription("Verified TrustCart seller with protected checkout and seller-area visibility only.");
        seller.setStoreProfileImageUrl("https://images.unsplash.com/photo-1556745753-b2904692b3cd?auto=format&fit=crop&w=700&q=80");
        seller.setStoreBannerImageUrl("https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1600&q=80");
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Seller application submitted. TrustCart has sent the requirements email. You can log in only after admin approval.");
        return "redirect:/seller/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        List<Product> sellerProducts = productRepository.findBySellerOrderByCreatedAtDesc(seller);
        List<DiscountCode> sellerDiscounts = discountCodeRepository.findBySellerOrderByCreatedAtDesc(seller);
        List<CustomerOrder> sellerOrders = orderRepository.findOrdersForSeller(seller);
        BigDecimal salesTotal = orderRepository.sumSalesForSeller(seller);
        Long unitsSold = orderRepository.sumUnitsForSeller(seller);
        model.addAttribute("products", sellerProducts);
        model.addAttribute("discounts", sellerDiscounts);
        model.addAttribute("orders", sellerOrders.stream().limit(20).toList());
        model.addAttribute("salesTotal", salesTotal == null ? BigDecimal.ZERO : salesTotal);
        model.addAttribute("unitsSold", unitsSold == null ? 0 : unitsSold);
        model.addAttribute("orderCount", sellerOrders.size());
        return "seller-dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (currentSeller(session) == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        return "seller-profile-form";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String storeName,
                                @RequestParam(required = false) String businessType,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String storeDescription,
                                @RequestParam(required = false) String storeProfileImageUrl,
                                @RequestParam(required = false) String storeBannerImageUrl,
                                @RequestParam(required = false) MultipartFile storeProfileImage,
                                @RequestParam(required = false) MultipartFile storeBannerImage,
                                @RequestParam(required = false) String sustainabilityBadge,
                                @RequestParam(required = false) String ecoCommitment,
                                @RequestParam(required = false) String storeCity,
                                @RequestParam(required = false) String storeProvince,
                                @RequestParam(required = false) Double latitude,
                                @RequestParam(required = false) Double longitude,
                                @RequestParam(required = false, defaultValue = "false") boolean pickupAvailable,
                                @RequestParam(required = false, defaultValue = "5") Integer serviceRadiusKm,
                                HttpSession session, RedirectAttributes ra) throws IOException {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        seller.setStoreName(storeName);
        seller.setBusinessType(businessType);
        seller.setPhone(phone);
        seller.setStoreDescription(storeDescription);
        seller.setStoreProfileImageUrl(firstNonBlank(saveUpload(storeProfileImage), storeProfileImageUrl));
        seller.setStoreBannerImageUrl(firstNonBlank(saveUpload(storeBannerImage), storeBannerImageUrl));
        seller.setSustainabilityBadge(sustainabilityBadge);
        seller.setEcoCommitment(ecoCommitment);
        seller.setStoreCity(storeCity);
        seller.setStoreProvince(storeProvince);
        if (latitude != null) seller.setLatitude(latitude);
        if (longitude != null) seller.setLongitude(longitude);
        seller.setPickupAvailable(pickupAvailable);
        seller.setServiceRadiusKm(serviceRadiusKm == null ? 5 : serviceRadiusKm);
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Storefront profile updated.");
        return "redirect:/seller/dashboard";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model, HttpSession session) {
        if (currentSeller(session) == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        return "seller-product-form";
    }

    @PostMapping("/products")
    public String createProduct(@RequestParam String name, @RequestParam String description,
                                @RequestParam ProductCategory category, @RequestParam BigDecimal price,
                                @RequestParam Integer stock,
                                @RequestParam(required = false) String imageUrl,
                                @RequestParam(required = false) MultipartFile productImage,
                                @RequestParam(required = false) String productOrigin,
                                @RequestParam(required = false) String sustainabilityTag,
                                @RequestParam(required = false, defaultValue = "false") boolean ecoFriendly,
                                @RequestParam(required = false, defaultValue = "false") boolean locallySourced,
                                @RequestParam(required = false, defaultValue = "false") boolean subscriptionEligible,
                                @RequestParam(required = false, defaultValue = "5") int subscriptionDiscountPercent,
                                @RequestParam(required = false, defaultValue = "false") boolean tryOnEligible,
                                @RequestParam(required = false) String tryOnGender,
                                @RequestParam(required = false) String tryOnAssetUrl,
                                @RequestParam(required = false, defaultValue = "false") boolean requestFbt,
                                HttpSession session) throws IOException {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setPrice(price);
        p.setStock(stock);
        p.setImageUrl(firstNonBlank(saveUpload(productImage), firstNonBlank(imageUrl, "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=80")));
        p.setPhotoAltText(name + " product photo");
        p.setProductOrigin(productOrigin == null || productOrigin.isBlank() ? seller.getPublicLocationLabel() : productOrigin);
        p.setSustainabilityTag(sustainabilityTag == null || sustainabilityTag.isBlank() ? "Seller-managed verified listing" : sustainabilityTag);
        p.setEcoFriendly(ecoFriendly);
        p.setLocallySourced(locallySourced);
        p.setSubscriptionEligible(subscriptionEligible);
        p.setSubscriptionDiscountPercent(Math.max(0, subscriptionDiscountPercent));
        p.setTryOnEligible(tryOnEligible);
        p.setTryOnGender(tryOnGender);
        p.setTryOnAssetUrl(tryOnAssetUrl);
        p.setTrustScore(92);
        p.setGreenScore(ecoFriendly ? 95 : 75);
        p.setReviewSummary("Seller-uploaded product. Buyer protection applies after checkout.");
        p.setRedFlagSummary("New seller product. Transactions must remain inside TrustCart.");
        p.setSeller(seller);
        p.setStatus("APPROVED");
        if (requestFbt && seller.isCanUseFbt()) {
            p.setFulfilledBy("TRUSTCART");
            p.setFulfillmentStatus("TRUSTCART_APPROVED");
            p.setTrustCartStock(stock);
            p.setFulfillmentNote("Inventory is recorded as received and managed by TrustCart for prototype fulfillment.");
            p.setEstimatedDelivery("ETA: TrustCart hub delivery");
        } else if (requestFbt) {
            p.setFulfilledBy("SELLER");
            p.setFulfillmentStatus("PENDING_TRUSTCART_REVIEW");
            p.setFulfillmentNote("Seller requested Fulfilled by TrustCart. Admin must approve after inventory verification.");
        } else {
            p.setFulfilledBy("SELLER");
            p.setFulfillmentStatus("SELLER_MANAGED");
            p.setFulfillmentNote("Seller stores, packs, and ships this product.");
        }
        productRepository.save(p);
        return "redirect:/seller/dashboard";
    }

    @GetMapping("/discounts/new")
    public String newDiscount(Model model, HttpSession session) {
        if (currentSeller(session) == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        return "seller-discount-form";
    }

    @PostMapping("/discounts")
    public String createDiscount(@RequestParam String code, @RequestParam String description,
                                 @RequestParam(defaultValue = "0") BigDecimal minimumSpend,
                                 @RequestParam(defaultValue = "0") int percentOff,
                                 @RequestParam(defaultValue = "0") BigDecimal amountOff,
                                 @RequestParam(required = false, defaultValue = "false") boolean firstOrderOnly,
                                 @RequestParam(required = false, defaultValue = "false") boolean subscriptionBoost,
                                 HttpSession session, RedirectAttributes ra) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        if (discountCodeRepository.findByCodeIgnoreCase(code).isPresent()) {
            ra.addFlashAttribute("error", "Discount code already exists.");
            return "redirect:/seller/discounts/new";
        }
        DiscountCode dc = new DiscountCode();
        dc.setCode(code.toUpperCase().trim());
        dc.setDescription(description);
        dc.setMinimumSpend(minimumSpend);
        dc.setPercentOff(percentOff);
        dc.setAmountOff(amountOff);
        dc.setFirstOrderOnly(firstOrderOnly);
        dc.setSubscriptionBoost(subscriptionBoost);
        dc.setSeller(seller);
        dc.setCreatedBySeller(seller.getStoreName());
        discountCodeRepository.save(dc);
        return "redirect:/seller/dashboard";
    }


    @GetMapping("/report-buyer")
    public String reportBuyerForm(Model model, HttpSession session, RedirectAttributes ra) {
        Seller seller = currentSeller(session);
        if (seller == null) {
            ra.addFlashAttribute("error", "Please log in as seller first.");
            return "redirect:/seller/login";
        }
        sellerCommon(model, session);
        return "seller-report-buyer";
    }

    @PostMapping("/report-buyer")
    public String reportBuyer(@RequestParam String buyerEmail,
                              @RequestParam String reason,
                              @RequestParam(required = false) String orderCode,
                              HttpSession session, RedirectAttributes ra) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketCode("TC-TICKET-" + System.currentTimeMillis());
        ticket.setType("BUYER_REPORT");
        ticket.setStatus("OPEN");
        ticket.setPriority("HIGH");
        ticket.setReporterName(seller.getStoreName());
        ticket.setReporterEmail(seller.getEmail());
        ticket.setSeller(seller);
        ticket.setReportedBuyerEmail(buyerEmail);
        ticket.setSubject("Seller reported buyer account" + (orderCode == null || orderCode.isBlank() ? "" : " - Order " + orderCode));
        ticket.setMessage(reason);
        buyerRepository.findByEmailIgnoreCase(buyerEmail).ifPresent(buyer -> {
            ticket.setBuyer(buyer);
            buyer.setReportCount((buyer.getReportCount() == null ? 0 : buyer.getReportCount()) + 1);
            buyerRepository.save(buyer);
        });
        ticketRepository.save(ticket);
        ra.addFlashAttribute("success", "Buyer report submitted to TrustCart Admin for review.");
        return "redirect:/seller/dashboard";
    }

}
