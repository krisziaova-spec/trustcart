package com.trustcart.controller;

import com.trustcart.model.*;
import com.trustcart.repository.*;
import com.trustcart.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class GiftRegistryController {
    private final BuyerAccountRepository buyerRepository;
    private final ProductRepository productRepository;
    private final GiftRegistryRepository registryRepository;
    private final GiftRegistryItemRepository registryItemRepository;
    private final CartService cartService;

    public GiftRegistryController(BuyerAccountRepository buyerRepository,
                                  ProductRepository productRepository,
                                  GiftRegistryRepository registryRepository,
                                  GiftRegistryItemRepository registryItemRepository,
                                  CartService cartService) {
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
        this.registryRepository = registryRepository;
        this.registryItemRepository = registryItemRepository;
        this.cartService = cartService;
    }

    private BuyerAccount currentBuyer(HttpSession session) {
        Object id = session.getAttribute("buyerId");
        if (id instanceof Long buyerId) return buyerRepository.findById(buyerId).orElse(null);
        return null;
    }

    private void addCommon(Model model, HttpSession session) {
        BuyerAccount buyer = currentBuyer(session);
        model.addAttribute("buyerLoggedIn", buyer != null);
        model.addAttribute("buyer", buyer);
        model.addAttribute("cartCount", cartService.countItems(session));
        model.addAttribute("categories", ProductCategory.publicValues());
    }

    @GetMapping("/registry")
    public String registryHome(Model model, HttpSession session) {
        addCommon(model, session);
        BuyerAccount buyer = currentBuyer(session);
        model.addAttribute("myRegistries", buyer == null ? List.of() : registryRepository.findByBuyerOrderByCreatedAtDesc(buyer));
        model.addAttribute("featuredRegistries", registryRepository.findByStatusOrderByCreatedAtDesc("ACTIVE").stream().limit(6).collect(Collectors.toList()));
        return "gift-registry";
    }

    @GetMapping("/registry/create")
    public String createRegistryForm(Model model, HttpSession session) {
        if (currentBuyer(session) == null) return "redirect:/buyer/login";
        addCommon(model, session);
        return "gift-registry-form";
    }

    @PostMapping("/registry/create")
    public String createRegistry(@RequestParam String registryName,
                                 @RequestParam String registryType,
                                 @RequestParam String recipientName,
                                 @RequestParam(required = false) String recipientEmail,
                                 @RequestParam(required = false) String eventDate,
                                 @RequestParam(required = false) String deliveryCity,
                                 @RequestParam(required = false) String registryNote,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        GiftRegistry registry = new GiftRegistry();
        registry.setBuyer(buyer);
        registry.setRegistryName(registryName);
        registry.setRegistryType(registryType);
        registry.setRecipientName(recipientName);
        registry.setRecipientEmail(recipientEmail);
        registry.setDeliveryCity(deliveryCity == null || deliveryCity.isBlank() ? buyer.getPreferredCity() : deliveryCity);
        registry.setRegistryNote(registryNote);
        if (eventDate != null && !eventDate.isBlank()) {
            registry.setEventDate(LocalDate.parse(eventDate));
        }
        registry.setShareCode(generateShareCode(registryName));
        registryRepository.save(registry);
        ra.addFlashAttribute("success", "Gift registry created. Share the protected link with family and friends.");
        return "redirect:/registry/" + registry.getShareCode();
    }

    @GetMapping("/registry/{shareCode}")
    public String viewRegistry(@PathVariable String shareCode, Model model, HttpSession session) {
        GiftRegistry registry = registryRepository.findByShareCodeIgnoreCase(shareCode).orElseThrow();
        addCommon(model, session);
        BuyerAccount buyer = currentBuyer(session);
        boolean owner = buyer != null && registry.getBuyer() != null && Objects.equals(buyer.getId(), registry.getBuyer().getId());
        model.addAttribute("registry", registry);
        model.addAttribute("items", registryItemRepository.findByGiftRegistryOrderByCreatedAtDesc(registry));
        model.addAttribute("owner", owner);
        model.addAttribute("allProducts", productRepository.findByStatusOrderByCreatedAtDesc("APPROVED").stream().limit(80).toList());
        return "gift-registry-detail";
    }

    @PostMapping("/registry/{shareCode}/items")
    public String addRegistryItem(@PathVariable String shareCode,
                                  @RequestParam Long productId,
                                  @RequestParam(defaultValue = "1") int quantity,
                                  @RequestParam(required = false, defaultValue = "Nice to have") String priority,
                                  @RequestParam(required = false) String giftNote,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        GiftRegistry registry = registryRepository.findByShareCodeIgnoreCase(shareCode).orElseThrow();
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null || registry.getBuyer() == null || !Objects.equals(buyer.getId(), registry.getBuyer().getId())) {
            ra.addFlashAttribute("error", "Only the registry owner can add preferred gift items.");
            return "redirect:/registry/" + shareCode;
        }
        Product product = productRepository.findById(productId).orElseThrow();
        GiftRegistryItem item = new GiftRegistryItem();
        item.setGiftRegistry(registry);
        item.setProduct(product);
        item.setQuantity(Math.max(1, quantity));
        item.setPriority(priority);
        item.setGiftNote(giftNote);
        registryItemRepository.save(item);
        ra.addFlashAttribute("success", "Gift item added to registry.");
        return "redirect:/registry/" + shareCode;
    }

    @PostMapping("/registry/{shareCode}/reserve/{itemId}")
    public String reserveRegistryItem(@PathVariable String shareCode,
                                      @PathVariable Long itemId,
                                      RedirectAttributes ra) {
        GiftRegistryItem item = registryItemRepository.findById(itemId).orElseThrow();
        int requested = item.getQuantity() == null ? 1 : item.getQuantity();
        int purchased = item.getPurchasedQuantity() == null ? 0 : item.getPurchasedQuantity();
        if (purchased < requested) {
            item.setPurchasedQuantity(purchased + 1);
            item.setStatus((purchased + 1) >= requested ? "FULFILLED" : "RESERVED");
            registryItemRepository.save(item);
            ra.addFlashAttribute("success", "Gift reserved. Complete checkout inside TrustCart to keep Buyer Protection active.");
        }
        return "redirect:/registry/" + shareCode;
    }

    @PostMapping("/registry/{shareCode}/remove/{itemId}")
    public String removeRegistryItem(@PathVariable String shareCode,
                                     @PathVariable Long itemId,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        GiftRegistry registry = registryRepository.findByShareCodeIgnoreCase(shareCode).orElseThrow();
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null || registry.getBuyer() == null || !Objects.equals(buyer.getId(), registry.getBuyer().getId())) {
            ra.addFlashAttribute("error", "Only the registry owner can remove items.");
            return "redirect:/registry/" + shareCode;
        }
        registryItemRepository.deleteById(itemId);
        ra.addFlashAttribute("success", "Gift item removed.");
        return "redirect:/registry/" + shareCode;
    }

    @PostMapping("/registry/{shareCode}/cart/{productId}")
    public String addRegistryProductToCart(@PathVariable String shareCode,
                                           @PathVariable Long productId,
                                           @RequestParam(defaultValue = "1") int quantity,
                                           HttpSession session,
                                           RedirectAttributes ra) {
        if (currentBuyer(session) == null) {
            ra.addFlashAttribute("error", "Please login or create a buyer account before checkout.");
            return "redirect:/buyer/login";
        }
        cartService.getCart(session).merge(productId, Math.max(1, quantity), Integer::sum);
        ra.addFlashAttribute("success", "Gift item added to cart. Checkout inside TrustCart for protection.");
        return "redirect:/cart";
    }

    private String generateShareCode(String registryName) {
        String base = registryName == null ? "trustcart-registry" : registryName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (base.isBlank()) base = "trustcart-registry";
        String candidate = base;
        int attempts = 0;
        while (registryRepository.existsByShareCodeIgnoreCase(candidate)) {
            attempts++;
            candidate = base + "-" + UUID.randomUUID().toString().substring(0, 6);
            if (attempts > 5) candidate = "registry-" + UUID.randomUUID().toString().substring(0, 10);
        }
        return candidate;
    }
}
