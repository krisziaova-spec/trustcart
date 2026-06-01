package com.trustcart.controller;

import com.trustcart.model.*;
import com.trustcart.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/seller")
public class SellerController {
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final DiscountCodeRepository discountCodeRepository;

    public SellerController(SellerRepository sellerRepository, ProductRepository productRepository, DiscountCodeRepository discountCodeRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.discountCodeRepository = discountCodeRepository;
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
            session.setAttribute("sellerId", seller.get().getId());
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
        seller.setStatus("APPROVED");
        seller.setStoreLocationVerified(true);
        seller.setApprovedAt(LocalDateTime.now());
        seller.setApprovedBy("TrustCart Verification");
        sellerRepository.save(seller);
        session.setAttribute("sellerId", seller.getId());
        return "redirect:/seller/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        model.addAttribute("products", productRepository.findBySellerOrderByCreatedAtDesc(seller));
        model.addAttribute("discounts", discountCodeRepository.findBySellerOrderByCreatedAtDesc(seller));
        return "seller-dashboard";
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
                                @RequestParam Integer stock, @RequestParam String imageUrl,
                                @RequestParam(required = false, defaultValue = "false") boolean ecoFriendly,
                                @RequestParam(required = false, defaultValue = "false") boolean locallySourced,
                                @RequestParam(required = false, defaultValue = "false") boolean subscriptionEligible,
                                @RequestParam(required = false, defaultValue = "false") boolean tryOnEligible,
                                @RequestParam(required = false) String tryOnGender,
                                @RequestParam(required = false) String tryOnAssetUrl,
                                HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setPrice(price);
        p.setStock(stock);
        p.setImageUrl(imageUrl);
        p.setPhotoAltText(name + " product photo");
        p.setEcoFriendly(ecoFriendly);
        p.setLocallySourced(locallySourced);
        p.setSubscriptionEligible(subscriptionEligible);
        p.setTryOnEligible(tryOnEligible);
        p.setTryOnGender(tryOnGender);
        p.setTryOnAssetUrl(tryOnAssetUrl);
        p.setTrustScore(92);
        p.setGreenScore(ecoFriendly ? 95 : 75);
        p.setReviewSummary("Seller-uploaded product. Buyer protection applies after checkout.");
        p.setRedFlagSummary("New seller product. Transactions must remain inside TrustCart.");
        p.setSeller(seller);
        p.setStatus("APPROVED");
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
        dc.setSeller(seller);
        dc.setCreatedBySeller(seller.getStoreName());
        discountCodeRepository.save(dc);
        return "redirect:/seller/dashboard";
    }
}
