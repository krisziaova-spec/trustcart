package com.trustcart.controller;

import com.trustcart.model.Product;
import com.trustcart.model.Seller;
import com.trustcart.repository.ProductRepository;
import com.trustcart.repository.SellerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    public AdminController(SellerRepository sellerRepository, ProductRepository productRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public String adminHome() {
        return "redirect:/admin/sellers";
    }

    @GetMapping("/sellers")
    public String sellers(Model model) {
        model.addAttribute("pendingSellers", sellerRepository.findByStatusOrderByCreatedAtDesc("PENDING"));
        model.addAttribute("approvedSellers", sellerRepository.findByStatusOrderByCreatedAtDesc("APPROVED"));
        model.addAttribute("rejectedSellers", sellerRepository.findByStatusOrderByCreatedAtDesc("REJECTED"));
        return "admin-sellers";
    }

    @PostMapping("/sellers/{id}/approve")
    public String approveSeller(@PathVariable Long id,
                                @RequestParam(required = false, defaultValue = "false") boolean canUseFbt,
                                @RequestParam(required = false) String note,
                                RedirectAttributes ra) {
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setStatus("APPROVED");
        seller.setBusinessVerified(true);
        seller.setIdentityVerified(true);
        seller.setDocumentVerified(true);
        seller.setProductComplianceChecked(true);
        seller.setStoreLocationVerified(true);
        seller.setCanUseFbt(canUseFbt);
        seller.setRequirementsStatus("COMPLETED");
        seller.setRequirementsNote(note == null || note.isBlank() ? "Requirements completed and approved by TrustCart admin." : note);
        seller.setApprovedBy("TrustCart Admin");
        seller.setApprovedAt(LocalDateTime.now());
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Seller approved. Seller can now log in." + (canUseFbt ? " FBT access enabled." : ""));
        return "redirect:/admin/sellers";
    }

    @PostMapping("/sellers/{id}/reject")
    public String rejectSeller(@PathVariable Long id,
                               @RequestParam(required = false) String note,
                               RedirectAttributes ra) {
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setStatus("REJECTED");
        seller.setCanUseFbt(false);
        seller.setRequirementsStatus("REJECTED");
        seller.setRequirementsNote(note == null || note.isBlank() ? "Application rejected by TrustCart admin." : note);
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Seller application rejected.");
        return "redirect:/admin/sellers";
    }

    @GetMapping("/fulfillment")
    public String fulfillment(Model model) {
        List<Product> products = productRepository.findByStatusOrderByCreatedAtDesc("APPROVED");
        model.addAttribute("pendingProducts", products.stream().filter(p -> "PENDING_TRUSTCART_REVIEW".equalsIgnoreCase(p.getFulfillmentStatus())).toList());
        model.addAttribute("trustCartProducts", products.stream().filter(Product::isFulfilledByTrustCart).toList());
        model.addAttribute("sellerProducts", products.stream().filter(p -> !p.isFulfilledByTrustCart()).toList());
        return "admin-fulfillment";
    }

    @PostMapping("/fulfillment/{id}/approve")
    public String approveFbt(@PathVariable Long id,
                             @RequestParam(defaultValue = "0") int receivedStock,
                             @RequestParam(required = false) String note,
                             RedirectAttributes ra) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setFulfilledBy("TRUSTCART");
        product.setFulfillmentStatus("TRUSTCART_APPROVED");
        product.setTrustCartStock(Math.max(0, receivedStock));
        product.setStock(Math.max(0, receivedStock));
        product.setEstimatedDelivery("ETA: TrustCart hub delivery");
        product.setFulfillmentNote(note == null || note.isBlank() ? "Inventory received and verified by TrustCart hub." : note);
        productRepository.save(product);
        Seller seller = product.getSeller();
        seller.setCanUseFbt(true);
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Product marked as Fulfilled by TrustCart.");
        return "redirect:/admin/fulfillment";
    }

    @PostMapping("/fulfillment/{id}/seller")
    public String setSellerFulfilled(@PathVariable Long id, RedirectAttributes ra) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setFulfilledBy("SELLER");
        product.setFulfillmentStatus("SELLER_MANAGED");
        product.setTrustCartStock(0);
        product.setFulfillmentNote("Seller stores, packs, and ships this product.");
        productRepository.save(product);
        ra.addFlashAttribute("success", "Product marked as Fulfilled by Seller.");
        return "redirect:/admin/fulfillment";
    }
}
