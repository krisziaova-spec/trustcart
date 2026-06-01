package com.trustcart.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class PageController {
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }

    @GetMapping("/sitemap.xml")
    public void sitemapXml(HttpServletResponse response) throws IOException {
        response.setContentType("application/xml");
        String base = "https://trustcart.onrender.com";
        String[] paths = {"/","/seller","/seller/apply","/buyer/login","/buyer/register","/try-on","/autoship","/track","/refund","/faq","/site-map","/privacy-policy","/terms-and-conditions","/return-refund-policy","/shipping-delivery-policy","/payment-policy","/buyer-protection-policy","/seller-policy","/authenticity-policy","/sustainability-policy","/prohibited-items-policy","/off-platform-policy"};
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (String p : paths) xml.append("<url><loc>").append(base).append(p).append("</loc><lastmod>").append(LocalDate.now()).append("</lastmod></url>\n");
        xml.append("</urlset>");
        response.getWriter().write(xml.toString());
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("title", "Frequently Asked Questions");
        model.addAttribute("content", "TrustCart answers buyer and seller questions about verified sellers, WELCOME10, TrustPoints, autoshipment, live selling, Virtual Try-On Preview, refunds, and protected checkout.");
        return "simple-page";
    }

    @GetMapping("/site-map")
    public String siteMap(Model model) {
        Map<String, String> links = new LinkedHashMap<>();
        links.put("Shop", "/");
        links.put("Buyer Login", "/buyer/login");
        links.put("Buyer Register", "/buyer/register");
        links.put("Cart", "/cart");
        links.put("Checkout", "/checkout");
        links.put("Virtual Try-On Preview", "/try-on");
        links.put("Autoship", "/autoship");
        links.put("Track Order", "/track");
        links.put("Refund Center", "/refund");
        links.put("Seller Centre", "/seller");
        links.put("Start Selling", "/seller/apply");
        links.put("Seller Login", "/seller/login");
        links.put("FAQ", "/faq");
        links.put("Help Center", "/help-center");
        links.put("Contact Us", "/contact-us");
        links.put("Policies", "/privacy-policy");
        model.addAttribute("links", links);
        return "site-map";
    }

    @GetMapping({"/help-center","/contact-us","/about","/privacy-policy","/terms-and-conditions","/return-refund-policy","/shipping-delivery-policy","/payment-policy","/buyer-protection-policy","/seller-policy","/authenticity-policy","/sustainability-policy","/prohibited-items-policy","/off-platform-policy","/protection-center"})
    public String standardPage(jakarta.servlet.http.HttpServletRequest request, Model model) {
        String path = request.getRequestURI();
        String title = switch(path) {
            case "/help-center" -> "Help Center";
            case "/contact-us" -> "Contact Us";
            case "/about" -> "About TrustCart";
            case "/terms-and-conditions" -> "Terms and Conditions";
            case "/return-refund-policy" -> "Return and Refund Policy";
            case "/shipping-delivery-policy" -> "Shipping and Delivery Policy";
            case "/payment-policy" -> "Payment Policy";
            case "/buyer-protection-policy", "/protection-center" -> "Buyer Protection Policy";
            case "/seller-policy" -> "Seller Policy";
            case "/authenticity-policy" -> "Authenticity Policy";
            case "/sustainability-policy" -> "Sustainability Policy";
            case "/prohibited-items-policy" -> "Prohibited Items Policy";
            case "/off-platform-policy" -> "Off-Platform Transaction Policy";
            default -> "Privacy Policy";
        };
        String content = "TrustCart requires protected transactions inside the platform. Seller exact addresses are hidden from buyers, discount codes are managed by sellers, and buyer protection applies only to orders completed through TrustCart.";
        if (title.contains("Sustainability")) content = "TrustCart highlights Green Score, eco-packaging, local sellers, autoshipment, and lower-waste checkout choices to support sustainable shopping.";
        if (title.contains("Authenticity")) content = "TrustCart uses seller verification, product trust scoring, verified reviews, and red-flag summaries to reduce fake listings and misleading claims.";
        if (title.contains("Off-Platform")) content = "Buyers and sellers are reminded not to move transactions outside TrustCart. Off-platform purchases are not covered by Buyer Protection, refund tracking, or order support.";
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        return "simple-page";
    }
}
