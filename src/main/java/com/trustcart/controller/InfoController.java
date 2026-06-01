package com.trustcart.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
public class InfoController {

    @GetMapping("/faq")
    public String faq(Model model) { model.addAttribute("pageTitle", "FAQ"); return "faq"; }

    @GetMapping("/site-map")
    public String siteMap(Model model) { model.addAttribute("pageTitle", "Site Map"); return "site-map"; }

    @GetMapping("/about")
    public String about() { return "about"; }

    @GetMapping("/help-center")
    public String helpCenter() { return "help-center"; }

    @GetMapping("/contact-us")
    public String contactUs() { return "contact-us"; }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() { return "privacy-policy"; }

    @GetMapping("/terms-and-conditions")
    public String termsAndConditions() { return "terms-and-conditions"; }

    @GetMapping("/return-refund-policy")
    public String returnRefundPolicy() { return "return-refund-policy"; }

    @GetMapping("/shipping-delivery-policy")
    public String shippingDeliveryPolicy() { return "shipping-delivery-policy"; }

    @GetMapping("/payment-policy")
    public String paymentPolicy() { return "payment-policy"; }

    @GetMapping("/buyer-protection-policy")
    public String buyerProtectionPolicy() { return "buyer-protection-policy"; }

    @GetMapping("/seller-policy")
    public String sellerPolicy() { return "seller-policy"; }

    @GetMapping("/authenticity-policy")
    public String authenticityPolicy() { return "authenticity-policy"; }

    @GetMapping("/sustainability-policy")
    public String sustainabilityPolicy() { return "sustainability-policy"; }

    @GetMapping("/prohibited-items-policy")
    public String prohibitedItemsPolicy() { return "prohibited-items-policy"; }

    @GetMapping("/off-platform-policy")
    public String offPlatformPolicy() { return "off-platform-policy"; }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String sitemapXml(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        int port = request.getServerPort();
        if (("http".equals(request.getScheme()) && port != 80) || ("https".equals(request.getScheme()) && port != 443)) {
            baseUrl += ":" + port;
        }

        List<String> paths = List.of(
                "/", "/buyer/login", "/buyer/register", "/cart", "/checkout", "/autoship", "/track", "/refund",
                "/seller", "/seller/login", "/seller/apply", "/seller/dashboard", "/seller/products/new",
                "/faq", "/site-map", "/about", "/help-center", "/contact-us",
                "/privacy-policy", "/terms-and-conditions", "/return-refund-policy", "/shipping-delivery-policy", "/payment-policy",
                "/buyer-protection-policy", "/seller-policy", "/authenticity-policy", "/sustainability-policy", "/prohibited-items-policy", "/off-platform-policy"
        );

        String today = LocalDate.now().toString();
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (String path : paths) {
            xml.append("  <url>\n");
            xml.append("    <loc>").append(baseUrl).append(path).append("</loc>\n");
            xml.append("    <lastmod>").append(today).append("</lastmod>\n");
            xml.append("    <changefreq>weekly</changefreq>\n");
            xml.append("    <priority>").append("/".equals(path) ? "1.0" : "0.7").append("</priority>\n");
            xml.append("  </url>\n");
        }
        xml.append("</urlset>");
        return xml.toString();
    }
}
