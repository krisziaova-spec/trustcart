package com.trustcart.controller;

import com.trustcart.model.BuyerAccount;
import com.trustcart.model.Product;
import com.trustcart.model.Seller;
import com.trustcart.repository.BuyerAccountRepository;
import com.trustcart.repository.DiscountCodeRepository;
import com.trustcart.repository.ProductRepository;
import com.trustcart.repository.SellerRepository;
import com.trustcart.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StorefrontController {
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final BuyerAccountRepository buyerRepository;
    private final CartService cartService;

    public StorefrontController(SellerRepository sellerRepository, ProductRepository productRepository,
                                DiscountCodeRepository discountCodeRepository, BuyerAccountRepository buyerRepository,
                                CartService cartService) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.buyerRepository = buyerRepository;
        this.cartService = cartService;
    }

    private BuyerAccount currentBuyer(HttpSession session) {
        Object id = session.getAttribute("buyerId");
        if (id instanceof Long buyerId) return buyerRepository.findById(buyerId).orElse(null);
        return null;
    }

    @GetMapping("/store/{id}")
    public String storefront(@PathVariable Long id, Model model, HttpSession session) {
        Seller seller = sellerRepository.findById(id).orElseThrow();
        BuyerAccount buyer = currentBuyer(session);
        model.addAttribute("buyerLoggedIn", buyer != null);
        model.addAttribute("buyer", buyer);
        model.addAttribute("cartCount", cartService.countItems(session));
        model.addAttribute("seller", seller);
        model.addAttribute("products", productRepository.findBySellerAndStatusOrderByCreatedAtDesc(seller, "APPROVED"));
        model.addAttribute("subscriptionProducts", productRepository.findBySellerAndStatusOrderByCreatedAtDesc(seller, "APPROVED").stream().filter(Product::isSubscriptionEligible).toList());
        model.addAttribute("discounts", discountCodeRepository.findBySellerOrderByCreatedAtDesc(seller).stream().filter(d -> d.isActive()).toList());
        return "storefront";
    }
}
