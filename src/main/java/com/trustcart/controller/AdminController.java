package com.trustcart.controller;

import com.trustcart.model.*;
import com.trustcart.repository.AutoshipSubscriptionRepository;
import com.trustcart.repository.CustomerOrderRepository;
import com.trustcart.repository.DiscountCodeRepository;
import com.trustcart.repository.ProductRepository;
import com.trustcart.repository.RefundRequestRepository;
import com.trustcart.repository.SellerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;
    private final RefundRequestRepository refundRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final AutoshipSubscriptionRepository autoshipRepository;

    public AdminController(SellerRepository sellerRepository,
                           ProductRepository productRepository,
                           CustomerOrderRepository orderRepository,
                           RefundRequestRepository refundRepository,
                           DiscountCodeRepository discountCodeRepository,
                           AutoshipSubscriptionRepository autoshipRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.autoshipRepository = autoshipRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("pendingSellers", sellerRepository.findByStatus(SellerStatus.PENDING));
        model.addAttribute("approvedSellers", sellerRepository.findByStatus(SellerStatus.APPROVED));
        model.addAttribute("pendingProducts", productRepository.findByStatus(ProductStatus.PENDING));
        model.addAttribute("orders", orderRepository.findTop20ByOrderByCreatedAtDesc());
        model.addAttribute("refunds", refundRepository.findTop20ByOrderByCreatedAtDesc());
        model.addAttribute("discountCodes", discountCodeRepository.findTop20ByOrderByCreatedAtDesc());
        model.addAttribute("subscriptions", autoshipRepository.findTop20ByOrderByCreatedAtDesc());
        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("refundStatuses", RefundStatus.values());
        return "admin-dashboard";
    }


    @PostMapping("/discounts")
    public String createDiscount(@RequestParam String code,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(defaultValue = "0") BigDecimal minimumSpend,
                                 @RequestParam(defaultValue = "0") Integer percentOff,
                                 @RequestParam(defaultValue = "0") BigDecimal amountOff,
                                 @RequestParam(defaultValue = "0") Integer maxRedemptions,
                                 RedirectAttributes redirectAttributes) {
        if (discountCodeRepository.findByCodeIgnoreCase(DiscountCode.normalizeCode(code)).isPresent()) {
            redirectAttributes.addFlashAttribute("message", "Discount code already exists: " + DiscountCode.normalizeCode(code));
            return "redirect:/admin";
        }
        DiscountCode discount = new DiscountCode(DiscountCode.normalizeCode(code), description, minimumSpend, percentOff, amountOff, true);
        discount.setMaxRedemptions(maxRedemptions);
        discountCodeRepository.save(discount);
        redirectAttributes.addFlashAttribute("message", "Discount code created: " + discount.getCode());
        return "redirect:/admin";
    }

    @PostMapping("/discounts/{id}/toggle")
    public String toggleDiscount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        DiscountCode discount = discountCodeRepository.findById(id).orElseThrow();
        discount.setActive(!discount.isActive());
        discountCodeRepository.save(discount);
        redirectAttributes.addFlashAttribute("message", "Discount code updated: " + discount.getCode());
        return "redirect:/admin";
    }

    @PostMapping("/sellers/{id}/approve")
    public String approveSeller(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setStatus(SellerStatus.APPROVED);
        seller.markVerifiedDefaults();
        sellerRepository.save(seller);
        redirectAttributes.addFlashAttribute("message", "Seller approved: " + seller.getStoreName());
        return "redirect:/admin";
    }

    @PostMapping("/sellers/{id}/reject")
    public String rejectSeller(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setStatus(SellerStatus.REJECTED);
        sellerRepository.save(seller);
        redirectAttributes.addFlashAttribute("message", "Seller rejected: " + seller.getStoreName());
        return "redirect:/admin";
    }

    @PostMapping("/products/{id}/approve")
    public String approveProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setStatus(ProductStatus.APPROVED);
        product.setTrustCartShield(true);
        product.setAuthenticItemChecked(true);
        product.setVerifiedReviewsOnly(true);
        product.setSuspiciousReviewFlag(false);
        product.applyDefaultTrustBreakdown();
        product.setTrustScore(Math.max(product.getTrustScore(), 90));
        product.setGreenScore(Math.max(product.getGreenScore(), product.isEcoFriendly() ? 88 : 65));
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("message", "Product approved: " + product.getName());
        return "redirect:/admin";
    }

    @PostMapping("/products/{id}/reject")
    public String rejectProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setStatus(ProductStatus.REJECTED);
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("message", "Product rejected: " + product.getName());
        return "redirect:/admin";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam OrderStatus status,
                                    RedirectAttributes redirectAttributes) {
        CustomerOrder order = orderRepository.findById(id).orElseThrow();
        order.setOrderStatus(status);
        orderRepository.save(order);
        redirectAttributes.addFlashAttribute("message", "Order status updated: " + order.getOrderCode());
        return "redirect:/admin";
    }

    @PostMapping("/refunds/{id}/status")
    public String updateRefundStatus(@PathVariable Long id,
                                     @RequestParam RefundStatus status,
                                     RedirectAttributes redirectAttributes) {
        RefundRequest refund = refundRepository.findById(id).orElseThrow();
        refund.setStatus(status);
        refundRepository.save(refund);
        redirectAttributes.addFlashAttribute("message", "Refund status updated: " + refund.getOrderCode());
        return "redirect:/admin";
    }
}
