package com.trustcart.controller;

import com.trustcart.model.BuyerAccount;
import com.trustcart.model.CustomerOrder;
import com.trustcart.model.Product;
import com.trustcart.model.ProductCategory;
import com.trustcart.model.IncomingStockShipment;
import com.trustcart.model.TrustCartWarehouse;
import com.trustcart.model.Seller;
import com.trustcart.model.SupportTicket;
import com.trustcart.model.ProductReview;
import com.trustcart.repository.BuyerAccountRepository;
import com.trustcart.repository.CustomerOrderRepository;
import com.trustcart.repository.ProductRepository;
import com.trustcart.repository.SellerRepository;
import com.trustcart.repository.SupportTicketRepository;
import com.trustcart.repository.IncomingStockShipmentRepository;
import com.trustcart.repository.TrustCartWarehouseRepository;
import com.trustcart.repository.ProductReviewRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/command-center")
public class AdminController {
    private static final String ADMIN_EMAIL = "admin@trustcart.ph";
    private static final String ADMIN_PASSWORD = "trustadmin2026";

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;
    private final SupportTicketRepository ticketRepository;
    private final BuyerAccountRepository buyerRepository;
    private final IncomingStockShipmentRepository incomingStockRepository;
    private final TrustCartWarehouseRepository warehouseRepository;
    private final ProductReviewRepository productReviewRepository;

    public AdminController(SellerRepository sellerRepository, ProductRepository productRepository,
                           CustomerOrderRepository orderRepository, SupportTicketRepository ticketRepository,
                           BuyerAccountRepository buyerRepository, IncomingStockShipmentRepository incomingStockRepository,
                           TrustCartWarehouseRepository warehouseRepository, ProductReviewRepository productReviewRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.buyerRepository = buyerRepository;
        this.incomingStockRepository = incomingStockRepository;
        this.warehouseRepository = warehouseRepository;
        this.productReviewRepository = productReviewRepository;
    }

    private boolean isAdmin(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
    }

    private String guard(HttpSession session) {
        return isAdmin(session) ? null : "redirect:/command-center/login";
    }

    private void adminCommon(Model model, String activeTab) {
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("openTicketCount", ticketRepository.countByStatus("OPEN"));
        model.addAttribute("pendingSellerCount", sellerRepository.findByStatusOrderByCreatedAtDesc("PENDING").size()
                + sellerRepository.findByStatusOrderByCreatedAtDesc("PENDING_REVIEW").size());
        model.addAttribute("buyerReportCount", ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(t -> "BUYER_REPORT".equalsIgnoreCase(t.getType()))
                .filter(t -> !"RESOLVED".equalsIgnoreCase(t.getStatus()) && !"CLOSED".equalsIgnoreCase(t.getStatus()))
                .count());
        model.addAttribute("incomingStockCount", incomingStockRepository.findByStatusOrderByCreatedAtDesc("PENDING_DROP_OFF").size() + incomingStockRepository.findByStatusOrderByCreatedAtDesc("IN_TRANSIT").size());
        model.addAttribute("reviewModerationCount", productReviewRepository.countByStatus("PENDING_REVIEW") + productReviewRepository.countByStatus("FLAGGED") + productReviewRepository.findByAppealOpenTrueOrderByAppealedAtDesc().size());
        model.addAttribute("riskReviewCount", buyerRepository.findAll().stream().filter(b -> "HIGH".equalsIgnoreCase(b.getRiskLevel()) || "VERIFICATION_REQUIRED".equalsIgnoreCase(b.getVerificationStatus()) || "PENDING_REVIEW".equalsIgnoreCase(b.getVerificationStatus())).count() + sellerRepository.findAll().stream().filter(s -> "HIGH".equalsIgnoreCase(s.getRiskLevel()) || "UNDER_REVIEW".equalsIgnoreCase(s.getRiskStatus()) || "VERIFICATION_REQUIRED".equalsIgnoreCase(s.getRiskStatus())).count());
        model.addAttribute("adminBase", "/command-center");
    }

    private String csvCell(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private ResponseEntity<String> csvDownload(String filename, String csv) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    private boolean withinDateRange(CustomerOrder order, String startDate, String endDate) {
        if (order.getCreatedAt() == null) return true;
        LocalDate orderDate = order.getCreatedAt().toLocalDate();
        try {
            if (startDate != null && !startDate.isBlank() && orderDate.isBefore(LocalDate.parse(startDate))) return false;
            if (endDate != null && !endDate.isBlank() && orderDate.isAfter(LocalDate.parse(endDate))) return false;
        } catch (Exception ignored) { }
        return true;
    }

    private int riskWeight(String riskLevel) {
        if (riskLevel == null) return 0;
        return switch (riskLevel.toUpperCase()) {
            case "HIGH" -> 3;
            case "MEDIUM" -> 2;
            default -> 1;
        };
    }

    private int countOpenTicketsForBuyer(BuyerAccount buyer) {
        if (buyer == null) return 0;
        return (int) ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(t -> t.getBuyer() != null && buyer.getId().equals(t.getBuyer().getId()))
                .filter(t -> !"RESOLVED".equalsIgnoreCase(t.getStatus()) && !"CLOSED".equalsIgnoreCase(t.getStatus()))
                .count();
    }

    private int countOpenTicketsForSeller(Seller seller) {
        if (seller == null) return 0;
        return (int) ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(t -> t.getSeller() != null && seller.getId().equals(t.getSeller().getId()))
                .filter(t -> !"RESOLVED".equalsIgnoreCase(t.getStatus()) && !"CLOSED".equalsIgnoreCase(t.getStatus()))
                .count();
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (isAdmin(session)) return "redirect:/command-center";
        return "admin-login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes ra) {
        if (ADMIN_EMAIL.equalsIgnoreCase(email.trim()) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/command-center";
        }
        ra.addFlashAttribute("error", "Invalid admin email or password.");
        return "redirect:/command-center/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminLoggedIn");
        return "redirect:/command-center/login";
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        adminCommon(model, "overview");
        List<Seller> sellers = sellerRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<CustomerOrder> orders = orderRepository.findAll();
        BigDecimal totalSales = orders.stream().map(CustomerOrder::getTotal).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        long fbtProducts = products.stream().filter(Product::isFulfilledByTrustCart).count();
        long fbsProducts = products.size() - fbtProducts;
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("totalStores", sellers.size());
        model.addAttribute("activeStores", sellers.stream().filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus()) || "APPROVED".equalsIgnoreCase(s.getStatus())).count());
        model.addAttribute("totalProducts", products.size());
        model.addAttribute("totalBuyers", buyerRepository.count());
        model.addAttribute("deactivatedBuyers", buyerRepository.findAll().stream().filter(b -> !b.isActive()).count());
        model.addAttribute("fbtProducts", fbtProducts);
        model.addAttribute("fbsProducts", fbsProducts);
        model.addAttribute("recentTickets", ticketRepository.findAllByOrderByCreatedAtDesc().stream().limit(5).toList());
        model.addAttribute("reviewQueueCount", productReviewRepository.countByStatus("PENDING_REVIEW") + productReviewRepository.countByStatus("FLAGGED") + productReviewRepository.findByAppealOpenTrueOrderByAppealedAtDesc().size());
        model.addAttribute("riskQueueCount", buyerRepository.findAll().stream().filter(b -> "HIGH".equalsIgnoreCase(b.getRiskLevel()) || "VERIFICATION_REQUIRED".equalsIgnoreCase(b.getVerificationStatus()) || "PENDING_REVIEW".equalsIgnoreCase(b.getVerificationStatus())).count() + sellers.stream().filter(s -> "HIGH".equalsIgnoreCase(s.getRiskLevel()) || "UNDER_REVIEW".equalsIgnoreCase(s.getRiskStatus()) || "VERIFICATION_REQUIRED".equalsIgnoreCase(s.getRiskStatus())).count());
        model.addAttribute("incomingStocks", incomingStockRepository.findAllByOrderByCreatedAtDesc().stream().limit(5).toList());
        model.addAttribute("recentOrders", orders.stream().sorted(Comparator.comparing(CustomerOrder::getCreatedAt).reversed()).limit(5).toList());
        return "admin-dashboard";
    }

    @GetMapping("/sellers")
    public String sellers(Model model, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        adminCommon(model, "sellers");
        List<Seller> all = sellerRepository.findAll().stream().sorted(Comparator.comparing(Seller::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).toList();
        model.addAttribute("allSellers", all);
        model.addAttribute("pendingSellers", all.stream().filter(s -> "PENDING".equalsIgnoreCase(s.getStatus()) || "PENDING_REVIEW".equalsIgnoreCase(s.getStatus())).toList());
        model.addAttribute("activeSellers", all.stream().filter(s -> "ACTIVE".equalsIgnoreCase(s.getStatus()) || "APPROVED".equalsIgnoreCase(s.getStatus())).toList());
        model.addAttribute("inactiveSellers", all.stream().filter(s -> "DEACTIVATED".equalsIgnoreCase(s.getStatus()) || "SUSPENDED".equalsIgnoreCase(s.getStatus()) || "REJECTED".equalsIgnoreCase(s.getStatus())).toList());
        return "admin-sellers";
    }

    @PostMapping("/sellers/{id}/approve")
    public String approveSeller(@PathVariable Long id,
                                @RequestParam(required = false, defaultValue = "false") boolean canUseFbt,
                                @RequestParam(required = false) String note,
                                RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setStatus("ACTIVE");
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
        ra.addFlashAttribute("success", "Store activated. Seller can now log in." + (canUseFbt ? " FBT access enabled." : ""));
        return "redirect:/command-center/sellers";
    }

    @PostMapping("/sellers/{id}/reject")
    public String rejectSeller(@PathVariable Long id, @RequestParam(required = false) String note,
                               RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setStatus("REJECTED");
        seller.setCanUseFbt(false);
        seller.setRequirementsStatus("REJECTED");
        seller.setRequirementsNote(note == null || note.isBlank() ? "Application rejected by TrustCart admin." : note);
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Seller application rejected.");
        return "redirect:/command-center/sellers";
    }

    @PostMapping("/sellers/{id}/activate")
    public String activateStore(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setStatus("ACTIVE");
        seller.setRequirementsStatus("COMPLETED");
        seller.setApprovedAt(seller.getApprovedAt() == null ? LocalDateTime.now() : seller.getApprovedAt());
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Store activated and visible to buyers.");
        return "redirect:/command-center/sellers";
    }

    @PostMapping("/sellers/{id}/deactivate")
    public String deactivateStore(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setStatus("DEACTIVATED");
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Store deactivated and hidden from marketplace.");
        return "redirect:/command-center/sellers";
    }

    @PostMapping("/sellers/{id}/suspend")
    public String suspendStore(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setStatus("SUSPENDED");
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Store suspended due to admin review or buyer protection concern.");
        return "redirect:/command-center/sellers";
    }



    @GetMapping("/buyers")
    public String buyers(Model model, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        adminCommon(model, "buyers");
        List<BuyerAccount> buyers = buyerRepository.findAll().stream()
                .sorted(Comparator.comparing(BuyerAccount::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        model.addAttribute("buyers", buyers);
        model.addAttribute("activeBuyers", buyers.stream().filter(BuyerAccount::isActive).count());
        model.addAttribute("restrictedBuyers", buyers.stream().filter(b -> !b.isActive()).count());
        model.addAttribute("buyerReports", ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(t -> "BUYER_REPORT".equalsIgnoreCase(t.getType())).toList());
        return "admin-buyers";
    }

    @PostMapping("/buyers/{id}/activate")
    public String activateBuyer(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        BuyerAccount buyer = buyerRepository.findById(id).orElseThrow();
        buyer.setStatus("ACTIVE");
        buyer.setAdminSafetyNote("Buyer account reactivated by TrustCart Admin.");
        buyer.setDeactivatedAt(null);
        buyer.setBlockedAt(null);
        buyerRepository.save(buyer);
        ra.addFlashAttribute("success", "Buyer account activated.");
        return "redirect:/command-center/buyers";
    }

    @PostMapping("/buyers/{id}/deactivate")
    public String deactivateBuyer(@PathVariable Long id, @RequestParam(required = false) String note, RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        BuyerAccount buyer = buyerRepository.findById(id).orElseThrow();
        buyer.setStatus("DEACTIVATED");
        buyer.setAdminSafetyNote(note == null || note.isBlank() ? "Deactivated after TrustCart safety review." : note);
        buyer.setDeactivatedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
        ra.addFlashAttribute("success", "Buyer account deactivated.");
        return "redirect:/command-center/buyers";
    }

    @PostMapping("/buyers/{id}/block")
    public String blockBuyer(@PathVariable Long id, @RequestParam(required = false) String note, RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        BuyerAccount buyer = buyerRepository.findById(id).orElseThrow();
        buyer.setStatus("BLOCKED");
        buyer.setAdminSafetyNote(note == null || note.isBlank() ? "Blocked after repeated reports or serious safety concern." : note);
        buyer.setBlockedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
        ra.addFlashAttribute("success", "Buyer account blocked.");
        return "redirect:/command-center/buyers";
    }

    @GetMapping("/fulfillment")
    public String fulfillment(Model model, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        adminCommon(model, "fulfillment");
        List<Product> products = productRepository.findByStatusOrderByCreatedAtDesc("APPROVED");
        model.addAttribute("pendingProducts", products.stream().filter(p -> "PENDING_TRUSTCART_REVIEW".equalsIgnoreCase(p.getFulfillmentStatus())).toList());
        model.addAttribute("trustCartProducts", products.stream().filter(Product::isFulfilledByTrustCart).toList());
        model.addAttribute("sellerProducts", products.stream().filter(p -> !p.isFulfilledByTrustCart()).toList());
        model.addAttribute("incomingStocks", incomingStockRepository.findAllByOrderByCreatedAtDesc().stream().limit(10).toList());
        return "admin-fulfillment";
    }

    @PostMapping("/fulfillment/{id}/approve")
    public String approveFbt(@PathVariable Long id, @RequestParam(defaultValue = "0") int receivedStock,
                             @RequestParam(required = false) String note, RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
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
        return "redirect:/command-center/fulfillment";
    }

    @PostMapping("/fulfillment/{id}/seller")
    public String setSellerFulfilled(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        Product product = productRepository.findById(id).orElseThrow();
        product.setFulfilledBy("SELLER");
        product.setFulfillmentStatus("SELLER_MANAGED");
        product.setTrustCartStock(0);
        product.setFulfillmentNote("Seller stores, packs, and ships this product.");
        productRepository.save(product);
        ra.addFlashAttribute("success", "Product marked as Fulfilled by Seller.");
        return "redirect:/command-center/fulfillment";
    }

    @GetMapping("/tickets")
    public String tickets(Model model, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        adminCommon(model, "tickets");
        List<SupportTicket> allTickets = ticketRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("tickets", allTickets);
        model.addAttribute("buyerTickets", allTickets.stream()
                .filter(t -> "BUYER".equalsIgnoreCase(t.getSourceRole()) || "PRODUCT_REPORT".equalsIgnoreCase(t.getType()) || "SELLER_REPORT".equalsIgnoreCase(t.getType()))
                .toList());
        model.addAttribute("sellerTickets", allTickets.stream()
                .filter(t -> "SELLER".equalsIgnoreCase(t.getSourceRole()) || "BUYER_REPORT".equalsIgnoreCase(t.getType()) || "SELLER_SUPPORT".equalsIgnoreCase(t.getType()))
                .toList());
        model.addAttribute("generalTickets", allTickets.stream()
                .filter(t -> !("BUYER".equalsIgnoreCase(t.getSourceRole()) || "SELLER".equalsIgnoreCase(t.getSourceRole())))
                .toList());
        return "admin-tickets";
    }

    @PostMapping("/tickets/{id}/update")
    public String updateTicket(@PathVariable Long id, @RequestParam String status,
                               @RequestParam(required = false) String adminNote,
                               RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        SupportTicket ticket = ticketRepository.findById(id).orElseThrow();
        ticket.setStatus(status);
        ticket.setAdminNote(adminNote);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        ra.addFlashAttribute("success", "Ticket updated.");
        return "redirect:/command-center/tickets";
    }


    @GetMapping("/incoming-stocks")
    public String incomingStocks(Model model, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        adminCommon(model, "incoming-stocks");
        model.addAttribute("incomingStocks", incomingStockRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("warehouses", warehouseRepository.findAll());
        return "admin-incoming-stocks";
    }

    @PostMapping("/incoming-stocks/{id}/update")
    public String updateIncomingStock(@PathVariable Long id,
                                      @RequestParam String status,
                                      @RequestParam(defaultValue = "0") int quantityReceived,
                                      @RequestParam(required = false) String adminNote,
                                      RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        IncomingStockShipment stock = incomingStockRepository.findById(id).orElseThrow();
        stock.setStatus(status);
        stock.setQuantityReceived(Math.max(0, quantityReceived));
        stock.setAdminNote(adminNote);
        stock.setUpdatedAt(LocalDateTime.now());
        incomingStockRepository.save(stock);
        if ("RECEIVED".equalsIgnoreCase(status) || "PARTIALLY_RECEIVED".equalsIgnoreCase(status)) {
            Product product = stock.getProduct();
            product.setFulfilledBy("TRUSTCART");
            product.setFulfillmentStatus("TRUSTCART_APPROVED");
            product.setTrustCartStock(Math.max(0, quantityReceived));
            product.setStock(Math.max(0, quantityReceived));
            product.setEstimatedDelivery("ETA: TrustCart warehouse delivery");
            product.setFulfillmentNote("Incoming stock " + stock.getShipmentCode() + " received at " + stock.getWarehouse().getDisplayLabel() + ".");
            productRepository.save(product);
            Seller seller = product.getSeller();
            seller.setCanUseFbt(true);
            sellerRepository.save(seller);
        }
        ra.addFlashAttribute("success", "Incoming stock updated.");
        return "redirect:/command-center/incoming-stocks";
    }

    @PostMapping("/warehouses/{id}/toggle")
    public String toggleWarehouse(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        TrustCartWarehouse warehouse = warehouseRepository.findById(id).orElseThrow();
        warehouse.setActive(!warehouse.isActive());
        warehouseRepository.save(warehouse);
        ra.addFlashAttribute("success", "Warehouse status updated.");
        return "redirect:/command-center/incoming-stocks";
    }


    @GetMapping("/reviews")
    public String reviews(Model model, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        adminCommon(model, "reviews");
        List<ProductReview> all = productReviewRepository.findAll().stream()
                .sorted(Comparator.comparing(ProductReview::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        model.addAttribute("pendingReviews", all.stream().filter(r -> "PENDING_REVIEW".equalsIgnoreCase(r.getStatus()) || "FLAGGED".equalsIgnoreCase(r.getStatus())).toList());
        model.addAttribute("appealedReviews", productReviewRepository.findByAppealOpenTrueOrderByAppealedAtDesc());
        model.addAttribute("publishedReviews", all.stream().filter(r -> "PUBLISHED".equalsIgnoreCase(r.getStatus())).toList());
        model.addAttribute("hiddenReviews", all.stream().filter(r -> "HIDDEN".equalsIgnoreCase(r.getStatus()) || "REMOVED".equalsIgnoreCase(r.getStatus()) || "REJECTED".equalsIgnoreCase(r.getStatus())).toList());
        model.addAttribute("reviewAppealTickets", ticketRepository.findAllByOrderByCreatedAtDesc().stream().filter(t -> "REVIEW_APPEAL".equalsIgnoreCase(t.getType())).toList());
        return "admin-reviews";
    }

    @PostMapping("/reviews/{id}/status")
    public String updateReviewStatus(@PathVariable Long id,
                                     @RequestParam String status,
                                     @RequestParam(required = false) String adminDecisionNote,
                                     RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        ProductReview review = productReviewRepository.findById(id).orElseThrow();
        review.setStatus(status);
        review.setAdminDecisionNote(adminDecisionNote);
        review.setModeratedBy("TrustCart Admin");
        review.setModeratedAt(LocalDateTime.now());
        if ("PUBLISHED".equalsIgnoreCase(status) || "REMOVED".equalsIgnoreCase(status) || "REJECTED".equalsIgnoreCase(status)) {
            review.setAppealOpen(false);
        }
        productReviewRepository.save(review);
        ra.addFlashAttribute("success", "Review status updated to " + status + ".");
        return "redirect:/command-center/reviews";
    }

    @PostMapping("/reviews/{id}/appeal/resolve")
    public String resolveReviewAppeal(@PathVariable Long id,
                                      @RequestParam String decision,
                                      @RequestParam(required = false) String adminDecisionNote,
                                      RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        ProductReview review = productReviewRepository.findById(id).orElseThrow();
        if ("REMOVE".equalsIgnoreCase(decision)) {
            review.setStatus("REMOVED");
        } else if ("HIDE".equalsIgnoreCase(decision)) {
            review.setStatus("HIDDEN");
        } else if ("KEEP".equalsIgnoreCase(decision)) {
            review.setStatus("PUBLISHED");
        } else if ("REQUEST_MORE_PROOF".equalsIgnoreCase(decision)) {
            review.setStatus("FLAGGED");
        }
        review.setAppealOpen("REQUEST_MORE_PROOF".equalsIgnoreCase(decision));
        review.setAdminDecisionNote(adminDecisionNote);
        review.setModeratedBy("TrustCart Admin");
        review.setModeratedAt(LocalDateTime.now());
        productReviewRepository.save(review);

        ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(t -> t.getProductReview() != null && t.getProductReview().getId().equals(review.getId()) && "REVIEW_APPEAL".equalsIgnoreCase(t.getType()))
                .forEach(t -> {
                    t.setStatus("REQUEST_MORE_PROOF".equalsIgnoreCase(decision) ? "INVESTIGATING" : "RESOLVED");
                    t.setAdminNote(adminDecisionNote);
                    t.setUpdatedAt(LocalDateTime.now());
                    ticketRepository.save(t);
                });
        ra.addFlashAttribute("success", "Review appeal decision saved.");
        return "redirect:/command-center/reviews";
    }

    @GetMapping("/reviews/export")
    public ResponseEntity<String> exportReviews(HttpSession session) {
        String redirect = guard(session); if (redirect != null) return csvDownload("reviews.csv", "Admin login required\n");
        StringBuilder csv = new StringBuilder("Review ID,Product,Seller,Buyer,Rating,Status,Appeal Open,Review Text,Admin Note,Created At\n");
        for (ProductReview r : productReviewRepository.findAll()) {
            csv.append(csvCell(r.getId())).append(',')
               .append(csvCell(r.getProduct() == null ? "" : r.getProduct().getName())).append(',')
               .append(csvCell(r.getSeller() == null ? "" : r.getSeller().getStoreName())).append(',')
               .append(csvCell(r.getBuyer() == null ? "" : r.getBuyer().getEmail())).append(',')
               .append(csvCell(r.getRating())).append(',')
               .append(csvCell(r.getStatus())).append(',')
               .append(csvCell(r.isAppealOpen())).append(',')
               .append(csvCell(r.getReviewText())).append(',')
               .append(csvCell(r.getAdminDecisionNote())).append(',')
               .append(csvCell(r.getCreatedAt())).append('\n');
        }
        return csvDownload("trustcart-review-moderation.csv", csv.toString());
    }


    @GetMapping("/risk")
    public String riskCenter(@RequestParam(required = false, defaultValue = "ALL") String riskLevel,
                             @RequestParam(required = false, defaultValue = "HIGH_FIRST") String sort,
                             Model model, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        adminCommon(model, "risk");
        List<BuyerAccount> buyers = buyerRepository.findAll().stream()
                .filter(b -> "ALL".equalsIgnoreCase(riskLevel) || riskLevel.equalsIgnoreCase(b.getRiskLevel()))
                .sorted((a, b) -> {
                    int byRisk = Integer.compare(riskWeight(b.getRiskLevel()), riskWeight(a.getRiskLevel()));
                    if (byRisk != 0) return byRisk;
                    return Integer.compare(b.getReportCount() == null ? 0 : b.getReportCount(), a.getReportCount() == null ? 0 : a.getReportCount());
                })
                .toList();
        List<Seller> sellers = sellerRepository.findAll().stream()
                .filter(seller -> "ALL".equalsIgnoreCase(riskLevel) || riskLevel.equalsIgnoreCase(seller.getRiskLevel()))
                .sorted((a, b) -> {
                    int byRisk = Integer.compare(riskWeight(b.getRiskLevel()), riskWeight(a.getRiskLevel()));
                    if (byRisk != 0) return byRisk;
                    return Integer.compare(countOpenTicketsForSeller(b), countOpenTicketsForSeller(a));
                })
                .toList();
        model.addAttribute("buyers", buyers);
        model.addAttribute("sellers", sellers);
        model.addAttribute("selectedRiskLevel", riskLevel);
        model.addAttribute("sort", sort);
        model.addAttribute("ticketRepository", ticketRepository);
        model.addAttribute("highRiskBuyers", buyerRepository.findAll().stream().filter(b -> "HIGH".equalsIgnoreCase(b.getRiskLevel())).count());
        model.addAttribute("mediumRiskBuyers", buyerRepository.findAll().stream().filter(b -> "MEDIUM".equalsIgnoreCase(b.getRiskLevel())).count());
        model.addAttribute("highRiskStores", sellerRepository.findAll().stream().filter(st -> "HIGH".equalsIgnoreCase(st.getRiskLevel())).count());
        model.addAttribute("verificationRequired", buyerRepository.findAll().stream().filter(b -> "VERIFICATION_REQUIRED".equalsIgnoreCase(b.getVerificationStatus()) || "PENDING_REVIEW".equalsIgnoreCase(b.getVerificationStatus())).count());
        return "admin-risk";
    }

    @PostMapping("/risk/buyers/{id}/update")
    public String updateBuyerRisk(@PathVariable Long id,
                                  @RequestParam String riskLevel,
                                  @RequestParam(defaultValue = "95") Integer credibilityScore,
                                  @RequestParam String verificationStatus,
                                  @RequestParam String status,
                                  @RequestParam(required = false) String riskNote,
                                  RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        BuyerAccount buyer = buyerRepository.findById(id).orElseThrow();
        buyer.setRiskLevel(riskLevel);
        buyer.setCredibilityScore(Math.max(0, Math.min(100, credibilityScore == null ? 95 : credibilityScore)));
        buyer.setVerificationStatus(verificationStatus);
        buyer.setStatus(status);
        buyer.setRiskNote(riskNote);
        buyer.setAdminSafetyNote(riskNote);
        buyer.setRiskReviewedAt(LocalDateTime.now());
        if ("BLOCKED".equalsIgnoreCase(status)) buyer.setBlockedAt(LocalDateTime.now());
        if ("DEACTIVATED".equalsIgnoreCase(status) || "RESTRICTED".equalsIgnoreCase(status)) buyer.setDeactivatedAt(LocalDateTime.now());
        buyerRepository.save(buyer);
        ra.addFlashAttribute("success", "Buyer risk profile updated.");
        return "redirect:/command-center/risk";
    }

    @PostMapping("/risk/buyers/{id}/request-verification")
    public String requestBuyerVerification(@PathVariable Long id,
                                           @RequestParam(required = false) String riskNote,
                                           RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        BuyerAccount buyer = buyerRepository.findById(id).orElseThrow();
        buyer.setRiskLevel("HIGH");
        buyer.setVerificationStatus("VERIFICATION_REQUIRED");
        buyer.setStatus("UNDER_REVIEW");
        buyer.setCredibilityScore(Math.min(buyer.getCredibilityScore(), 59));
        buyer.setVerificationRequestedAt(LocalDateTime.now());
        buyer.setRiskNote(riskNote == null || riskNote.isBlank() ? "Valid ID and face verification required due to marketplace risk review." : riskNote);
        buyer.setAdminSafetyNote(buyer.getRiskNote());
        buyerRepository.save(buyer);
        ra.addFlashAttribute("success", "Buyer verification request recorded. Admin/support can notify the buyer.");
        return "redirect:/command-center/risk";
    }

    @PostMapping("/risk/sellers/{id}/update")
    public String updateSellerRisk(@PathVariable Long id,
                                   @RequestParam String riskLevel,
                                   @RequestParam(defaultValue = "95") Integer credibilityScore,
                                   @RequestParam String riskStatus,
                                   @RequestParam String status,
                                   @RequestParam(required = false) String riskNote,
                                   RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setRiskLevel(riskLevel);
        seller.setCredibilityScore(Math.max(0, Math.min(100, credibilityScore == null ? 95 : credibilityScore)));
        seller.setRiskStatus(riskStatus);
        seller.setStatus(status);
        seller.setRiskNote(riskNote);
        seller.setRiskReviewedAt(LocalDateTime.now());
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Seller/store risk profile updated.");
        return "redirect:/command-center/risk";
    }

    @PostMapping("/risk/sellers/{id}/alert")
    public String alertSellerRisk(@PathVariable Long id,
                                  @RequestParam(required = false) String riskNote,
                                  RedirectAttributes ra, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        Seller seller = sellerRepository.findById(id).orElseThrow();
        seller.setRiskLevel("MEDIUM");
        seller.setRiskStatus("UNDER_REVIEW");
        seller.setRiskNote(riskNote == null || riskNote.isBlank() ? "Store is under TrustCart credibility review due to recent reports or unresolved concerns." : riskNote);
        seller.setWarningSentAt(LocalDateTime.now());
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Seller/store risk alert recorded.");
        return "redirect:/command-center/risk";
    }

    @GetMapping("/risk/export")
    public ResponseEntity<String> exportRisk(HttpSession session) {
        String redirect = guard(session); if (redirect != null) return csvDownload("risk.csv", "Admin login required\n");
        StringBuilder csv = new StringBuilder("Type,Name,Email,Risk Level,Credibility Score,Verification/Risk Status,Account Status,Reports/Notes,Reviewed At\n");
        for (BuyerAccount b : buyerRepository.findAll()) {
            csv.append(csvCell("Buyer")).append(',')
               .append(csvCell(b.getFullName())).append(',')
               .append(csvCell(b.getEmail())).append(',')
               .append(csvCell(b.getRiskLevel())).append(',')
               .append(csvCell(b.getCredibilityScore())).append(',')
               .append(csvCell(b.getVerificationStatus())).append(',')
               .append(csvCell(b.getStatus())).append(',')
               .append(csvCell(b.getRiskNote())).append(',')
               .append(csvCell(b.getRiskReviewedAt())).append('\n');
        }
        for (Seller st : sellerRepository.findAll()) {
            csv.append(csvCell("Seller/Store")).append(',')
               .append(csvCell(st.getStoreName())).append(',')
               .append(csvCell(st.getEmail())).append(',')
               .append(csvCell(st.getRiskLevel())).append(',')
               .append(csvCell(st.getCredibilityScore())).append(',')
               .append(csvCell(st.getRiskStatus())).append(',')
               .append(csvCell(st.getStatus())).append(',')
               .append(csvCell(st.getRiskNote())).append(',')
               .append(csvCell(st.getRiskReviewedAt())).append('\n');
        }
        return csvDownload("trustcart-credibility-risk.csv", csv.toString());
    }

    @GetMapping("/analytics")
    public String analytics(@RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(required = false) String orderStatus,
                            @RequestParam(required = false) ProductCategory category,
                            @RequestParam(required = false) String fulfillment,
                            @RequestParam(required = false, defaultValue = "revenue") String sort,
                            Model model, HttpSession session) {
        String redirect = guard(session); if (redirect != null) return redirect;
        adminCommon(model, "analytics");
        List<CustomerOrder> orders = orderRepository.findAllWithItems();
        List<Product> products = productRepository.findAll();
        List<Seller> sellers = sellerRepository.findAll();
        orders = orders.stream()
                .filter(o -> withinDateRange(o, startDate, endDate))
                .filter(o -> orderStatus == null || orderStatus.isBlank() || orderStatus.equalsIgnoreCase(o.getOrderStatus()))
                .filter(o -> category == null || o.getItems().stream().anyMatch(i -> i.getProduct() != null && category.equals(i.getProduct().getCategory())))
                .filter(o -> fulfillment == null || fulfillment.isBlank() || o.getItems().stream().anyMatch(i -> i.getProduct() != null && fulfillment.equalsIgnoreCase(i.getProduct().getFulfilledBy())))
                .toList();
        Map<String, BigDecimal> salesByStore = orders.stream().flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(i -> i.getSellerName() == null ? "Unknown Store" : i.getSellerName(),
                        Collectors.mapping(i -> i.getLineTotal() == null ? BigDecimal.ZERO : i.getLineTotal(), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        Map<String, BigDecimal> salesByProduct = orders.stream().flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(i -> i.getProductName() == null ? "Unknown Product" : i.getProductName(),
                        Collectors.mapping(i -> i.getLineTotal() == null ? BigDecimal.ZERO : i.getLineTotal(), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        long fbtOrders = orders.stream().filter(o -> o.getItems().stream().anyMatch(i -> i.getProduct() != null && i.getProduct().isFulfilledByTrustCart())).count();
        BigDecimal totalSales = orders.stream().map(CustomerOrder::getTotal).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageOrderValue = orders.isEmpty() ? BigDecimal.ZERO : totalSales.divide(BigDecimal.valueOf(orders.size()), 2, java.math.RoundingMode.HALF_UP);
        int unitsSold = orders.stream().flatMap(o -> o.getItems().stream()).mapToInt(i -> i.getQuantity()).sum();

        model.addAttribute("orders", orders);
        model.addAttribute("products", products);
        model.addAttribute("sellers", sellers);
        model.addAttribute("categories", ProductCategory.storefrontCategories());
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("averageOrderValue", averageOrderValue);
        model.addAttribute("unitsSold", unitsSold);
        model.addAttribute("fbtOrders", fbtOrders);
        model.addAttribute("fbsOrders", orders.size() - fbtOrders);
        model.addAttribute("salesByStore", salesByStore.entrySet().stream().sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed()).limit(10).toList());
        model.addAttribute("salesByProduct", salesByProduct.entrySet().stream().sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed()).limit(10).toList());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("fulfillment", fulfillment);
        model.addAttribute("sort", sort);
        return "admin-analytics";
    }



    @GetMapping("/analytics/export")
    public ResponseEntity<String> exportAnalytics(HttpSession session) {
        String redirect = guard(session); if (redirect != null) return csvDownload("admin-analytics.csv", "Admin login required\n");
        List<CustomerOrder> orders = orderRepository.findAllWithItems();
        StringBuilder csv = new StringBuilder("Order Code,Buyer,Email,Order Status,Payment Status,Escrow Status,Total,Created At\n");
        for (CustomerOrder order : orders) {
            csv.append(csvCell(order.getOrderCode())).append(',')
               .append(csvCell(order.getFullName())).append(',')
               .append(csvCell(order.getEmail())).append(',')
               .append(csvCell(order.getOrderStatus())).append(',')
               .append(csvCell(order.getPaymentStatus())).append(',')
               .append(csvCell(order.getEscrowStatus())).append(',')
               .append(csvCell(order.getTotal())).append(',')
               .append(csvCell(order.getCreatedAt())).append('\n');
        }
        return csvDownload("trustcart-admin-sales-analytics.csv", csv.toString());
    }

    @GetMapping("/tickets/export")
    public ResponseEntity<String> exportTickets(HttpSession session) {
        String redirect = guard(session); if (redirect != null) return csvDownload("tickets.csv", "Admin login required\n");
        StringBuilder csv = new StringBuilder("Ticket Code,Source,Type,Reporter,Email,Subject,Status,Attachment,Created At\n");
        for (SupportTicket t : ticketRepository.findAllByOrderByCreatedAtDesc()) {
            csv.append(csvCell(t.getTicketCode())).append(',')
               .append(csvCell(t.getSourceRole())).append(',')
               .append(csvCell(t.getType())).append(',')
               .append(csvCell(t.getReporterName())).append(',')
               .append(csvCell(t.getReporterEmail())).append(',')
               .append(csvCell(t.getSubject())).append(',')
               .append(csvCell(t.getStatus())).append(',')
               .append(csvCell(t.getAttachmentUrl())).append(',')
               .append(csvCell(t.getCreatedAt())).append('\n');
        }
        return csvDownload("trustcart-tickets.csv", csv.toString());
    }

    @GetMapping("/incoming-stocks/export")
    public ResponseEntity<String> exportIncomingStocks(HttpSession session) {
        String redirect = guard(session); if (redirect != null) return csvDownload("incoming-stocks.csv", "Admin login required\n");
        StringBuilder csv = new StringBuilder("Shipment Code,Seller,Product,Warehouse,Planned,Received,Status,Drop-off Date,Admin Note\n");
        for (IncomingStockShipment stock : incomingStockRepository.findAllByOrderByCreatedAtDesc()) {
            csv.append(csvCell(stock.getShipmentCode())).append(',')
               .append(csvCell(stock.getSeller() == null ? "" : stock.getSeller().getStoreName())).append(',')
               .append(csvCell(stock.getProduct() == null ? "" : stock.getProduct().getName())).append(',')
               .append(csvCell(stock.getWarehouse() == null ? "" : stock.getWarehouse().getDisplayLabel())).append(',')
               .append(csvCell(stock.getQuantityPlanned())).append(',')
               .append(csvCell(stock.getQuantityReceived())).append(',')
               .append(csvCell(stock.getStatus())).append(',')
               .append(csvCell(stock.getPreferredDropoffDate())).append(',')
               .append(csvCell(stock.getAdminNote())).append('\n');
        }
        return csvDownload("trustcart-admin-incoming-stocks.csv", csv.toString());
    }

}
