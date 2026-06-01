package com.trustcart.controller;

import com.trustcart.model.BuyerAccount;
import com.trustcart.repository.BuyerAccountRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/buyer")
public class AuthController {

    private final BuyerAccountRepository buyerRepository;

    public AuthController(BuyerAccountRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "buyer-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        BuyerAccount buyer = buyerRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
        if (buyer == null || buyer.getPassword() == null || !buyer.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid buyer email or password. You may register a new buyer account.");
            return "redirect:/buyer/login";
        }
        session.setAttribute(BuyerController.BUYER_SESSION_KEY, buyer.getId());
        if (buyer.getPreferredCity() != null) {
            session.setAttribute("TRUSTCART_MARKET_CITY", buyer.getPreferredCity());
            session.setAttribute("TRUSTCART_MARKET_LAT", buyer.getPreferredLatitude());
            session.setAttribute("TRUSTCART_MARKET_LNG", buyer.getPreferredLongitude());
            session.setAttribute("TRUSTCART_MARKET_RADIUS", buyer.getPreferredRadiusKm());
        }
        redirectAttributes.addFlashAttribute("message", "Welcome back, " + buyer.getFullName() + ".");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "buyer-register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam(required = false) String defaultAddress,
                           @RequestParam(defaultValue = "San Pablo City") String preferredCity,
                           @RequestParam(defaultValue = "14.0683") Double preferredLatitude,
                           @RequestParam(defaultValue = "121.3256") Double preferredLongitude,
                           @RequestParam(defaultValue = "5") Integer preferredRadiusKm,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (buyerRepository.findByEmailIgnoreCase(email.trim()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Buyer email already exists. Please login instead.");
            return "redirect:/buyer/login";
        }
        BuyerAccount buyer = new BuyerAccount(fullName.trim(), email.trim(), phone.trim(), password, preferredCity,
                preferredLatitude, preferredLongitude, preferredRadiusKm);
        buyer.setDefaultAddress(defaultAddress);
        BuyerAccount saved = buyerRepository.save(buyer);
        session.setAttribute(BuyerController.BUYER_SESSION_KEY, saved.getId());
        session.setAttribute("TRUSTCART_MARKET_CITY", saved.getPreferredCity());
        session.setAttribute("TRUSTCART_MARKET_LAT", saved.getPreferredLatitude());
        session.setAttribute("TRUSTCART_MARKET_LNG", saved.getPreferredLongitude());
        session.setAttribute("TRUSTCART_MARKET_RADIUS", saved.getPreferredRadiusKm());
        redirectAttributes.addFlashAttribute("message", "Buyer account created. You can now shop with buyer protection.");
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(BuyerController.BUYER_SESSION_KEY);
        redirectAttributes.addFlashAttribute("message", "Buyer logged out.");
        return "redirect:/";
    }
}
