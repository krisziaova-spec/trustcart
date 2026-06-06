package com.trustcart.controller;

import com.trustcart.model.BuyerAccount;
import com.trustcart.model.Seller;
import com.trustcart.repository.BuyerAccountRepository;
import com.trustcart.repository.SellerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthAccessController {
    private final BuyerAccountRepository buyerRepository;
    private final SellerRepository sellerRepository;

    public AuthAccessController(BuyerAccountRepository buyerRepository, SellerRepository sellerRepository) {
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(@RequestParam(required = false, defaultValue = "buyer") String type, Model model) {
        model.addAttribute("type", normalizeType(type));
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String resetPassword(@RequestParam String type,
                                @RequestParam String email,
                                @RequestParam String newPassword,
                                RedirectAttributes ra) {
        String accountType = normalizeType(type);
        if (newPassword == null || newPassword.length() < 6) {
            ra.addFlashAttribute("error", "Password must have at least 6 characters.");
            return "redirect:/forgot-password?type=" + accountType;
        }
        if ("seller".equals(accountType)) {
            Optional<Seller> seller = sellerRepository.findByEmailIgnoreCase(email);
            if (seller.isPresent()) {
                Seller s = seller.get();
                s.setPassword(newPassword);
                sellerRepository.save(s);
                ra.addFlashAttribute("success", "Seller password updated. You may now log in.");
                return "redirect:/seller/login";
            }
        } else if ("buyer".equals(accountType)) {
            Optional<BuyerAccount> buyer = buyerRepository.findByEmailIgnoreCase(email);
            if (buyer.isPresent()) {
                BuyerAccount b = buyer.get();
                b.setPassword(newPassword);
                buyerRepository.save(b);
                ra.addFlashAttribute("success", "Buyer password updated. You may now log in.");
                return "redirect:/buyer/login";
            }
        }
        ra.addFlashAttribute("error", "We could not find that account email.");
        return "redirect:/forgot-password?type=" + accountType;
    }

    @GetMapping("/admin")
    public String adminShortcut() {
        return "redirect:/command-center";
    }

    @GetMapping("/portal")
    public String portalShortcut(HttpSession session) {
        if (session.getAttribute("sellerId") != null) return "redirect:/seller/dashboard";
        if (session.getAttribute("buyerId") != null) return "redirect:/";
        return "redirect:/buyer/login";
    }

    private String normalizeType(String type) {
        if (type == null) return "buyer";
        String t = type.trim().toLowerCase();
        return t.equals("seller") ? "seller" : "buyer";
    }
}
