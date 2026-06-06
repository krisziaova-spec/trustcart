package com.trustcart.controller;

import com.trustcart.model.AutoshipSubscription;
import com.trustcart.model.BuyerAccount;
import com.trustcart.model.ChatMessage;
import com.trustcart.model.ChatThread;
import com.trustcart.model.CustomerOrder;
import com.trustcart.model.DiscountCode;
import com.trustcart.model.OrderItem;
import com.trustcart.model.Product;
import com.trustcart.model.ProductReview;
import com.trustcart.model.ProductCategory;
import com.trustcart.model.RefundRequest;
import com.trustcart.model.Seller;
import com.trustcart.model.SupportTicket;
import com.trustcart.repository.*;
import com.trustcart.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class BuyerController {
    private final BuyerAccountRepository buyerRepository;
    private final ProductRepository productRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final CustomerOrderRepository orderRepository;
    private final RefundRequestRepository refundRepository;
    private final AutoshipSubscriptionRepository autoshipRepository;
    private final SellerRepository sellerRepository;
    private final SupportTicketRepository ticketRepository;
    private final ChatThreadRepository chatThreadRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductReviewRepository productReviewRepository;
    private final CartService cartService;

    public BuyerController(BuyerAccountRepository buyerRepository, ProductRepository productRepository,
                           DiscountCodeRepository discountCodeRepository, CustomerOrderRepository orderRepository,
                           RefundRequestRepository refundRepository, AutoshipSubscriptionRepository autoshipRepository,
                           SellerRepository sellerRepository, SupportTicketRepository ticketRepository,
                           ChatThreadRepository chatThreadRepository, ChatMessageRepository chatMessageRepository,
                           ProductReviewRepository productReviewRepository,
                           CartService cartService) {
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
        this.autoshipRepository = autoshipRepository;
        this.sellerRepository = sellerRepository;
        this.ticketRepository = ticketRepository;
        this.chatThreadRepository = chatThreadRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.productReviewRepository = productReviewRepository;
        this.cartService = cartService;
    }

    private void addCommon(Model model, HttpSession session) {
        BuyerAccount buyer = currentBuyer(session);
        model.addAttribute("buyerLoggedIn", buyer != null);
        model.addAttribute("buyer", buyer);
        model.addAttribute("cartCount", cartService.countItems(session));
        model.addAttribute("categories", ProductCategory.storefrontCategories());
        model.addAttribute("marketCity", session.getAttribute("marketCity") != null ? session.getAttribute("marketCity") : "San Pablo City");
        model.addAttribute("marketLatitude", session.getAttribute("marketLatitude") != null ? session.getAttribute("marketLatitude") : 14.0683);
        model.addAttribute("marketLongitude", session.getAttribute("marketLongitude") != null ? session.getAttribute("marketLongitude") : 121.3256);
        model.addAttribute("marketRadius", session.getAttribute("marketRadius") != null ? session.getAttribute("marketRadius") : 5);
        model.addAttribute("nearbyOnly", session.getAttribute("nearbyOnly") != null ? session.getAttribute("nearbyOnly") : false);
        model.addAttribute("pickupOnly", session.getAttribute("pickupOnly") != null ? session.getAttribute("pickupOnly") : false);
    }

    private BuyerAccount currentBuyer(HttpSession session) {
        Object id = session.getAttribute("buyerId");
        if (id instanceof Long buyerId) return buyerRepository.findById(buyerId).orElse(null);
        return null;
    }

    private String saveUpload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String originalName = file.getOriginalFilename() == null ? "proof" : file.getOriginalFilename();
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0 && dot < originalName.length() - 1) {
            extension = originalName.substring(dot).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9.]", "");
        }
        if (extension.isBlank()) extension = ".jpg";
        Path uploadDir = Path.of("uploads");
        Files.createDirectories(uploadDir);
        String fileName = UUID.randomUUID() + extension;
        Path destination = uploadDir.resolve(fileName).normalize();
        file.transferTo(destination.toFile());
        return "/uploads/" + fileName;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       @RequestParam(required = false) ProductCategory category,
                       @RequestParam(required = false) Boolean nearbyOnly,
                       @RequestParam(required = false) Boolean pickupOnly,
                       @RequestParam(required = false) String searchMode,
                       @RequestParam(required = false) String imageQuery,
                       Model model, HttpSession session) {
        if (nearbyOnly != null) session.setAttribute("nearbyOnly", nearbyOnly);
        if (pickupOnly != null) session.setAttribute("pickupOnly", pickupOnly);
        addCommon(model, session);

        String searchTerm = normalizeSearchTerm(q);
        if (searchTerm == null && imageQuery != null && !imageQuery.isBlank()) {
            searchTerm = normalizeSearchTerm(imageQuery);
        }

        List<Product> products;
        final String finalSearchTerm = searchTerm;
        if (category != null && finalSearchTerm != null) {
            products = productRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, "APPROVED")
                    .stream()
                    .filter(product -> matchesSearchTerm(product, finalSearchTerm))
                    .collect(Collectors.toList());
        } else if (category != null) {
            products = productRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, "APPROVED");
        } else if (finalSearchTerm != null) {
            products = productRepository.searchApprovedProducts(finalSearchTerm, "APPROVED");
        } else {
            products = productRepository.findByStatusOrderByCreatedAtDesc("APPROVED");
        }
        products = applyMarketFilters(products, session);
        boolean browsingHome = category == null && finalSearchTerm == null;
        if (browsingHome) {
            products = products.stream()
                    .sorted(Comparator.comparingInt(this::homepagePriority)
                            .thenComparing(Product::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(24)
                    .collect(Collectors.toList());
        } else {
            products = products.stream().limit(48).collect(Collectors.toList());
        }
        model.addAttribute("products", products);
        model.addAttribute("query", searchTerm);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchMode", searchMode);
        model.addAttribute("imageQuery", imageQuery);
        model.addAttribute("discounts", discountCodeRepository.findByActiveTrueOrderByCreatedAtDesc().stream().limit(4).collect(Collectors.toList()));
        return "home";
    }


    private boolean isSellerPubliclyActive(Seller seller) {
        if (seller == null) return false;
        String status = seller.getStatus();
        return "ACTIVE".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status);
    }

    private List<Product> applyMarketFilters(List<Product> products, HttpSession session) {
        products = products.stream().filter(p -> isSellerPubliclyActive(p.getSeller())).collect(Collectors.toList());
        boolean nearbyOnly = Boolean.TRUE.equals(session.getAttribute("nearbyOnly"));
        boolean pickupOnly = Boolean.TRUE.equals(session.getAttribute("pickupOnly"));

        double targetLatitude = sessionNumber(session, "marketLatitude", 14.0683);
        double targetLongitude = sessionNumber(session, "marketLongitude", 121.3256);
        double radiusKm = sessionNumber(session, "marketRadius", 5.0);

        return products.stream()
                .filter(product -> {
                    Seller seller = product.getSeller();
                    if (seller == null) return false;

                    if (pickupOnly && !seller.isPickupAvailable()) {
                        return false;
                    }

                    boolean preparedFood = product.getCategory() == ProductCategory.PREPARED_FOODS;
                    boolean shouldCheckDistance = nearbyOnly || preparedFood;
                    double allowedRadiusKm = preparedFood ? Math.min(radiusKm, 5.0) : radiusKm;

                    if (shouldCheckDistance) {
                        if (seller.getLatitude() == null || seller.getLongitude() == null) {
                            return false;
                        }
                        double distanceKm = distanceInKilometers(
                                targetLatitude,
                                targetLongitude,
                                seller.getLatitude(),
                                seller.getLongitude()
                        );
                        return distanceKm <= allowedRadiusKm;
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    private int homepagePriority(Product product) {
        if (product == null || product.getCategory() == null) return 99;
        return switch (product.getCategory()) {
            case PREPARED_FOODS -> 0;
            case GROCERIES, FMCG, CONVENIENCE_GOODS, CONSUMER_STAPLES, EVERYDAY_ESSENTIALS, DAILY_NECESSITIES, PACKAGED_GOODS -> 1;
            case SUSTAINABLE_PRODUCTS -> 2;
            case LOCAL_FILIPINO_PRODUCTS -> 3;
            case HEALTH_WELLNESS, BEAUTY_PERSONAL_CARE, HOME_LIVING -> 4;
            case FASHION -> 5;
            default -> 6;
        };
    }

    private double sessionNumber(HttpSession session, String key, double defaultValue) {
        Object value = session.getAttribute(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private double distanceInKilometers(double lat1, double lon1, double lat2, double lon2) {
        final double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private String normalizeSearchTerm(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String cleaned = raw.trim().replaceAll("\\s+", " ");
        return cleaned.length() > 80 ? cleaned.substring(0, 80) : cleaned;
    }

    private boolean matchesSearchTerm(Product product, String term) {
        if (product == null || term == null || term.isBlank()) return true;
        String needle = term.toLowerCase(Locale.ROOT);
        return containsIgnoreCase(product.getName(), needle)
                || containsIgnoreCase(product.getDescription(), needle)
                || containsIgnoreCase(product.getSustainabilityTag(), needle)
                || containsIgnoreCase(product.getPhotoAltText(), needle)
                || containsIgnoreCase(product.getProductOrigin(), needle)
                || (product.getCategory() != null && containsIgnoreCase(product.getCategory().getDisplayName(), needle))
                || (product.getSeller() != null && (containsIgnoreCase(product.getSeller().getStoreName(), needle)
                || containsIgnoreCase(product.getSeller().getStoreCity(), needle)
                || containsIgnoreCase(product.getSeller().getStoreProvince(), needle)));
    }

    private boolean containsIgnoreCase(String haystack, String lowerCaseNeedle) {
        return haystack != null && haystack.toLowerCase(Locale.ROOT).contains(lowerCaseNeedle);
    }

    private boolean orderContainsProduct(CustomerOrder order, Product product) {
        if (order == null || product == null || order.getItems() == null) return false;
        return order.getItems().stream().anyMatch(item -> item.getProduct() != null && Objects.equals(item.getProduct().getId(), product.getId()));
    }

    private boolean isReviewEligible(CustomerOrder order) {
        if (order == null || order.getOrderStatus() == null) return false;
        String status = order.getOrderStatus().trim().toUpperCase(Locale.ROOT);
        return status.equals("COMPLETED")
                || status.equals("BUYER_CONFIRMED_RECEIVED")
                || status.equals("ORDER_RECEIVED")
                || status.equals("RECEIVED");
    }

    private boolean isActiveOrder(CustomerOrder order) {
        if (order == null || order.getOrderStatus() == null) return false;
        String status = order.getOrderStatus().trim().toUpperCase(Locale.ROOT);
        return !(status.equals("COMPLETED") || status.equals("BUYER_CONFIRMED_RECEIVED") || status.equals("ORDER_RECEIVED")
                || status.equals("RECEIVED") || status.equals("CANCELLED") || status.equals("CANCELED") || status.equals("REFUNDED"));
    }

    @PostMapping("/buyer/location")
    public String setLocation(@RequestParam String marketCity,
                              @RequestParam Double latitude,
                              @RequestParam Double longitude,
                              @RequestParam Integer radiusKm,
                              @RequestParam(required = false, defaultValue = "false") boolean nearbyOnly,
                              @RequestParam(required = false, defaultValue = "false") boolean pickupOnly,
                              HttpSession session) {
        session.setAttribute("marketCity", marketCity);
        session.setAttribute("marketLatitude", latitude);
        session.setAttribute("marketLongitude", longitude);
        session.setAttribute("marketRadius", radiusKm);
        session.setAttribute("nearbyOnly", nearbyOnly);
        session.setAttribute("pickupOnly", pickupOnly);
        return "redirect:/";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id,
                          @RequestParam(required = false) String reviewOrderCode,
                          Model model, HttpSession session, RedirectAttributes ra) {
        Product product = productRepository.findById(id).orElseThrow();
        if (!isSellerPubliclyActive(product.getSeller())) {
            ra.addFlashAttribute("error", "This product is currently unavailable because the store is not active.");
            return "redirect:/";
        }
        addCommon(model, session);
        model.addAttribute("product", product);
        model.addAttribute("reviews", productReviewRepository.findByProductAndStatusOrderByCreatedAtDesc(product, "PUBLISHED"));
        BuyerAccount buyer = currentBuyer(session);
        List<CustomerOrder> eligibleReviewOrders = buyer == null ? List.of() : orderRepository.findByEmailWithItems(buyer.getEmail()).stream()
                .filter(order -> isReviewEligible(order))
                .filter(order -> orderContainsProduct(order, product))
                .filter(order -> !productReviewRepository.existsByBuyerAndProductAndCustomerOrder(buyer, product, order))
                .toList();
        boolean selectedReviewOrderValid = reviewOrderCode != null && eligibleReviewOrders.stream().anyMatch(o -> reviewOrderCode.equalsIgnoreCase(o.getOrderCode()));
        model.addAttribute("eligibleReviewOrders", eligibleReviewOrders);
        model.addAttribute("selectedReviewOrderCode", selectedReviewOrderValid ? reviewOrderCode : null);
        model.addAttribute("reviewRequiresCompletedOrder", true);
        model.addAttribute("buyerOrders", buyer == null ? List.of() : orderRepository.findByEmailWithItems(buyer.getEmail()).stream().limit(10).toList());
        model.addAttribute("related", productRepository.findByCategoryAndStatusOrderByCreatedAtDesc(product.getCategory(), "APPROVED").stream().filter(p -> !Objects.equals(p.getId(), id)).filter(p -> isSellerPubliclyActive(p.getSeller())).limit(4).toList());
        return "product-detail";
    }


    @GetMapping("/report/product/{id}")
    public String reportProductForm(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productRepository.findById(id).orElseThrow();
        addCommon(model, session);
        model.addAttribute("reportType", "PRODUCT_REPORT");
        model.addAttribute("product", product);
        model.addAttribute("seller", product.getSeller());
        model.addAttribute("targetLabel", product.getName());
        model.addAttribute("postUrl", "/report/product/" + id);
        return "buyer-report";
    }

    @PostMapping("/report/product/{id}")
    public String submitProductReport(@PathVariable Long id,
                                      @RequestParam String subject,
                                      @RequestParam String message,
                                      @RequestParam(required = false) String reporterName,
                                      @RequestParam(required = false) String reporterEmail,
                                      @RequestParam(required = false) MultipartFile proofFile,
                                      HttpSession session, RedirectAttributes ra) throws IOException {
        Product product = productRepository.findById(id).orElseThrow();
        BuyerAccount buyer = currentBuyer(session);
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketCode("TC-TICKET-" + System.currentTimeMillis());
        ticket.setType("PRODUCT_REPORT");
        ticket.setSourceRole("BUYER");
        ticket.setStatus("OPEN");
        ticket.setPriority("HIGH");
        ticket.setProduct(product);
        ticket.setSeller(product.getSeller());
        ticket.setBuyer(buyer);
        ticket.setReporterName(buyer != null ? buyer.getFullName() : reporterName);
        ticket.setReporterEmail(buyer != null ? buyer.getEmail() : reporterEmail);
        ticket.setSubject(subject == null || subject.isBlank() ? "Product report: " + product.getName() : subject);
        ticket.setMessage(message);
        String proofUrl = saveUpload(proofFile);
        ticket.setAttachmentUrl(proofUrl);
        ticket.setAttachmentName(proofFile == null ? null : proofFile.getOriginalFilename());
        ticketRepository.save(ticket);
        ra.addFlashAttribute("success", "Product report submitted to TrustCart Support.");
        return "redirect:/product/" + id;
    }

    @GetMapping("/report/seller/{id}")
    public String reportSellerForm(@PathVariable Long id, Model model, HttpSession session) {
        Seller seller = sellerRepository.findById(id).orElseThrow();
        addCommon(model, session);
        model.addAttribute("reportType", "SELLER_REPORT");
        model.addAttribute("seller", seller);
        model.addAttribute("targetLabel", seller.getStoreName());
        model.addAttribute("postUrl", "/report/seller/" + id);
        return "buyer-report";
    }

    @PostMapping("/report/seller/{id}")
    public String submitSellerReport(@PathVariable Long id,
                                     @RequestParam String subject,
                                     @RequestParam String message,
                                     @RequestParam(required = false) String reporterName,
                                     @RequestParam(required = false) String reporterEmail,
                                     @RequestParam(required = false) MultipartFile proofFile,
                                     HttpSession session, RedirectAttributes ra) throws IOException {
        Seller seller = sellerRepository.findById(id).orElseThrow();
        BuyerAccount buyer = currentBuyer(session);
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketCode("TC-TICKET-" + System.currentTimeMillis());
        ticket.setType("SELLER_REPORT");
        ticket.setSourceRole("BUYER");
        ticket.setStatus("OPEN");
        ticket.setPriority("HIGH");
        ticket.setSeller(seller);
        ticket.setBuyer(buyer);
        ticket.setReporterName(buyer != null ? buyer.getFullName() : reporterName);
        ticket.setReporterEmail(buyer != null ? buyer.getEmail() : reporterEmail);
        ticket.setSubject(subject == null || subject.isBlank() ? "Seller report: " + seller.getStoreName() : subject);
        ticket.setMessage(message);
        String proofUrl = saveUpload(proofFile);
        ticket.setAttachmentUrl(proofUrl);
        ticket.setAttachmentName(proofFile == null ? null : proofFile.getOriginalFilename());
        ticketRepository.save(ticket);
        ra.addFlashAttribute("success", "Seller report submitted to TrustCart Support.");
        return "redirect:/store/" + id;
    }


    @GetMapping("/buyer/messages")
    public String buyerMessages(Model model, HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) {
            ra.addFlashAttribute("error", "Please log in to view seller messages.");
            return "redirect:/buyer/login";
        }
        addCommon(model, session);
        model.addAttribute("threads", chatThreadRepository.findByBuyerOrderByUpdatedAtDesc(buyer));
        return "buyer-messages";
    }

    @GetMapping("/buyer/messages/{threadId}")
    public String buyerMessageThread(@PathVariable Long threadId, Model model, HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        ChatThread thread = chatThreadRepository.findById(threadId).orElseThrow();
        if (thread.getBuyer() == null || !Objects.equals(thread.getBuyer().getId(), buyer.getId())) {
            ra.addFlashAttribute("error", "You can only view your own messages.");
            return "redirect:/buyer/messages";
        }
        addCommon(model, session);
        model.addAttribute("thread", thread);
        model.addAttribute("messages", chatMessageRepository.findByThreadOrderByCreatedAtAsc(thread));
        return "buyer-chat-thread";
    }

    @GetMapping("/chat/seller/{sellerId}")
    public String startBuyerSellerChat(@PathVariable Long sellerId,
                                       @RequestParam(required = false) Long productId,
                                       Model model, HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) {
            ra.addFlashAttribute("error", "Please log in to contact the seller.");
            return "redirect:/buyer/login";
        }
        Seller seller = sellerRepository.findById(sellerId).orElseThrow();
        Product product = productId == null ? null : productRepository.findById(productId).orElse(null);
        ChatThread thread = chatThreadRepository.findByBuyerOrderByUpdatedAtDesc(buyer).stream()
                .filter(t -> t.getSeller() != null && Objects.equals(t.getSeller().getId(), seller.getId()))
                .filter(t -> product == null ? t.getProduct() == null : (t.getProduct() != null && Objects.equals(t.getProduct().getId(), product.getId())))
                .findFirst().orElseGet(() -> {
                    ChatThread created = new ChatThread();
                    created.setBuyer(buyer);
                    created.setSeller(seller);
                    created.setProduct(product);
                    created.setSubject(product == null ? "Store inquiry: " + seller.getStoreName() : "Product inquiry: " + product.getName());
                    created.setLastMessagePreview("New conversation opened.");
                    created.setLastSenderRole("BUYER");
                    return chatThreadRepository.save(created);
                });
        return "redirect:/buyer/messages/" + thread.getId();
    }

    @PostMapping("/buyer/messages/{threadId}/reply")
    public String buyerReply(@PathVariable Long threadId, @RequestParam String message,
                             @RequestParam(required = false) MultipartFile attachment,
                             HttpSession session, RedirectAttributes ra) throws IOException {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        ChatThread thread = chatThreadRepository.findById(threadId).orElseThrow();
        if (thread.getBuyer() == null || !Objects.equals(thread.getBuyer().getId(), buyer.getId())) {
            ra.addFlashAttribute("error", "You can only reply to your own messages.");
            return "redirect:/buyer/messages";
        }
        saveChatMessage(thread, "BUYER", buyer.getFullName(), buyer.getEmail(), message, attachment);
        return "redirect:/buyer/messages/" + threadId;
    }

    @GetMapping("/support/contact")
    public String supportContact(Model model, HttpSession session) {
        addCommon(model, session);
        model.addAttribute("sourceRole", "BUYER");
        return "support-contact";
    }

    @PostMapping("/support/contact")
    public String submitSupportContact(@RequestParam String subject,
                                       @RequestParam String message,
                                       @RequestParam(required = false) String reporterName,
                                       @RequestParam(required = false) String reporterEmail,
                                       @RequestParam(required = false) MultipartFile proofFile,
                                       HttpSession session, RedirectAttributes ra) throws IOException {
        BuyerAccount buyer = currentBuyer(session);
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketCode("TC-TICKET-" + System.currentTimeMillis());
        ticket.setType("GENERAL_SUPPORT");
        ticket.setSourceRole("BUYER");
        ticket.setStatus("OPEN");
        ticket.setPriority("NORMAL");
        ticket.setBuyer(buyer);
        ticket.setReporterName(buyer != null ? buyer.getFullName() : reporterName);
        ticket.setReporterEmail(buyer != null ? buyer.getEmail() : reporterEmail);
        ticket.setSubject(subject);
        ticket.setMessage(message);
        String proofUrl = saveUpload(proofFile);
        ticket.setAttachmentUrl(proofUrl);
        ticket.setAttachmentName(proofFile == null ? null : proofFile.getOriginalFilename());
        ticketRepository.save(ticket);
        ra.addFlashAttribute("success", "Support ticket opened. TrustCart Admin will review it in the Buyer Concerns tab.");
        return "redirect:/support/contact";
    }

    private void saveChatMessage(ChatThread thread, String role, String name, String email, String message, MultipartFile attachment) throws IOException {
        ChatMessage chat = new ChatMessage();
        chat.setThread(thread);
        chat.setSenderRole(role);
        chat.setSenderName(name);
        chat.setSenderEmail(email);
        chat.setMessage(message);
        String attachmentUrl = saveUpload(attachment);
        chat.setAttachmentUrl(attachmentUrl);
        chat.setAttachmentName(attachment == null ? null : attachment.getOriginalFilename());
        chatMessageRepository.save(chat);
        String preview = message == null ? "" : message.trim();
        if (preview.isBlank() && attachmentUrl != null) preview = "Sent an attachment";
        thread.setLastMessagePreview(preview.length() > 120 ? preview.substring(0, 120) : preview);
        thread.setLastSenderRole(role);
        thread.setUpdatedAt(java.time.LocalDateTime.now());
        chatThreadRepository.save(thread);
    }


    @GetMapping("/buyer/verification")
    public String buyerVerification(Model model, HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) {
            ra.addFlashAttribute("error", "Please log in to submit verification documents.");
            return "redirect:/buyer/login";
        }
        addCommon(model, session);
        model.addAttribute("buyer", buyer);
        return "buyer-verification";
    }

    @PostMapping("/buyer/verification")
    public String submitBuyerVerification(@RequestParam(required = false) MultipartFile idProof,
                                          @RequestParam(required = false) MultipartFile facePhoto,
                                          @RequestParam(required = false) String note,
                                          HttpSession session, RedirectAttributes ra) throws IOException {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        String idUrl = saveUpload(idProof);
        String faceUrl = saveUpload(facePhoto);
        if (idUrl != null) buyer.setIdProofUrl(idUrl);
        if (faceUrl != null) buyer.setFaceVerificationUrl(faceUrl);
        buyer.setVerificationStatus("PENDING_REVIEW");
        buyer.setRiskLevel("HIGH".equalsIgnoreCase(buyer.getRiskLevel()) ? "HIGH" : "MEDIUM");
        buyer.setRiskNote(note == null || note.isBlank() ? "Buyer submitted ID and face verification for TrustCart Admin review." : note);
        buyer.setRiskReviewedAt(java.time.LocalDateTime.now());
        buyerRepository.save(buyer);
        ra.addFlashAttribute("success", "Verification documents submitted. TrustCart Admin will review your account standing.");
        return "redirect:/buyer/verification";
    }

    @GetMapping("/buyer/login")
    public String buyerLogin(Model model, HttpSession session) {
        addCommon(model, session);
        return "buyer-login";
    }

    @PostMapping("/buyer/login")
    public String doBuyerLogin(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes ra) {
        Optional<BuyerAccount> buyer = buyerRepository.findByEmailIgnoreCase(email);
        if (buyer.isPresent() && Objects.equals(buyer.get().getPassword(), password)) {
            BuyerAccount account = buyer.get();
            if (!account.isActive()) {
                ra.addFlashAttribute("error", "Your buyer account is currently " + account.getStatus() + ". Please contact TrustCart support if you believe this is a mistake.");
                return "redirect:/buyer/login";
            }
            session.setAttribute("buyerId", account.getId());
            return "redirect:/";
        }
        ra.addFlashAttribute("error", "Invalid buyer email or password.");
        return "redirect:/buyer/login";
    }

    @GetMapping("/buyer/register")
    public String register(Model model, HttpSession session) {
        addCommon(model, session);
        return "buyer-register";
    }

    @PostMapping("/buyer/register")
    public String doRegister(@RequestParam String fullName, @RequestParam String email, @RequestParam String password,
                             @RequestParam(required = false) String phone, @RequestParam(required = false) String defaultAddress,
                             HttpSession session, RedirectAttributes ra) {
        if (buyerRepository.existsByEmailIgnoreCase(email)) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/buyer/register";
        }
        BuyerAccount buyer = new BuyerAccount();
        buyer.setFullName(fullName);
        buyer.setEmail(email);
        buyer.setPassword(password);
        buyer.setPhone(phone);
        buyer.setDefaultAddress(defaultAddress);
        buyer.setPreferredCity("San Pablo City");
        buyer.setPreferredLatitude(14.0683);
        buyer.setPreferredLongitude(121.3256);
        buyerRepository.save(buyer);
        session.setAttribute("buyerId", buyer.getId());
        return "redirect:/";
    }

    @PostMapping("/buyer/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("buyerId");
        return "redirect:/";
    }

    @GetMapping("/buyer/logout")
    public String logoutLink(HttpSession session) {
        session.removeAttribute("buyerId");
        return "redirect:/";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id, @RequestParam(defaultValue = "1") int quantity, HttpSession session, RedirectAttributes ra) {
        if (currentBuyer(session) == null) {
            ra.addFlashAttribute("error", "Please login as buyer before adding to cart.");
            return "redirect:/buyer/login";
        }
        Map<Long, Integer> cart = cartService.getCart(session);
        cart.put(id, cart.getOrDefault(id, 0) + Math.max(1, quantity));
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        addCommon(model, session);
        Map<Long, Integer> cart = cartService.getCart(session);
        List<Map<String, Object>> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> e : cart.entrySet()) {
            Product p = productRepository.findById(e.getKey()).orElse(null);
            if (p != null) {
                BigDecimal line = p.getPrice().multiply(BigDecimal.valueOf(e.getValue()));
                subtotal = subtotal.add(line);
                Map<String,Object> row = new HashMap<>();
                row.put("product", p);
                row.put("qty", e.getValue());
                row.put("line", line);
                items.add(row);
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("subtotal", subtotal);
        return "cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateQuantity(@PathVariable Long id, @RequestParam(defaultValue = "1") int quantity, HttpSession session) {
        Map<Long, Integer> cart = cartService.getCart(session);
        if (quantity <= 0) {
            cart.remove(id);
        } else {
            cart.put(id, quantity);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeItem(@PathVariable Long id, HttpSession session) {
        cartService.getCart(session).remove(id);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        cart(model, session);
        model.addAttribute("discounts", discountCodeRepository.findByActiveTrueOrderByCreatedAtDesc());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String fullName, @RequestParam String email, @RequestParam String phone,
                             @RequestParam String shippingAddress, @RequestParam String paymentMethod,
                             @RequestParam(required = false) String discountCode,
                             @RequestParam(required = false, defaultValue = "false") boolean ecoPackaging,
                             @RequestParam(required = false, defaultValue = "false") boolean noExtraPlastic,
                             @RequestParam(required = false, defaultValue = "false") boolean consolidatedDelivery,
                             @RequestParam(required = false, defaultValue = "STANDARD_DELIVERY") String deliveryOption,
                             @RequestParam(required = false, defaultValue = "0") int pointsToRedeem,
                             HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        Map<Long, Integer> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            ra.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/cart";
        }
        CustomerOrder order = new CustomerOrder();
        order.setOrderCode("TC-" + System.currentTimeMillis());
        order.setFullName(fullName);
        order.setEmail(email);
        order.setPhone(phone);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("Cash on Delivery".equalsIgnoreCase(paymentMethod) ? "COD selected - payment due upon delivery" : "Simulated paid inside TrustCart");
        order.setPaymentReference("TC-PAY-" + System.currentTimeMillis());
        order.setEscrowStatus("Funds protected by TrustCart until delivery is completed");
        order.setOrderStatus("ORDER_CONFIRMED");
        order.setTrackingStage("Order confirmed");
        order.setTrackingNote("Payment is simulated and protected inside TrustCart for demo purposes.");
        order.setEstimatedDeliveryAt(java.time.LocalDateTime.now().plusDays("PICKUP_HUB".equalsIgnoreCase(deliveryOption) ? 1 : 2));
        order.setEcoPackaging(ecoPackaging);
        order.setNoExtraPlastic(noExtraPlastic);
        order.setConsolidatedDelivery(consolidatedDelivery);
        order.setDeliveryOption(deliveryOption);
        order.setBuyerMarketLocation(String.valueOf(session.getAttribute("marketCity") != null ? session.getAttribute("marketCity") : "San Pablo City"));
        order.setPlatformProtectionNote("For buyer protection, checkout, payment, tracking, and refund requests must remain inside TrustCart.");

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> e : cart.entrySet()) {
            Product p = productRepository.findById(e.getKey()).orElse(null);
            if (p != null) {
                int qty = Math.max(1, e.getValue());
                BigDecimal line = p.getPrice().multiply(BigDecimal.valueOf(qty));
                subtotal = subtotal.add(line);
                OrderItem item = new OrderItem();
                item.setProduct(p);
                item.setProductName(p.getName());
                item.setSellerName(p.getSeller().getStoreName());
                item.setQuantity(qty);
                item.setUnitPrice(p.getPrice());
                item.setLineTotal(line);
                order.addItem(item);
                p.setStock(Math.max(0, p.getStock() - qty));
                productRepository.save(p);
            }
        }
        BigDecimal shipping = deliveryOption.equals("PICKUP_HUB") ? BigDecimal.ZERO : BigDecimal.valueOf(80);
        BigDecimal ecoFee = ecoPackaging ? BigDecimal.valueOf(10) : BigDecimal.ZERO;
        BigDecimal ecoDiscount = consolidatedDelivery ? BigDecimal.valueOf(20) : BigDecimal.ZERO;
        BigDecimal promoDiscount = calculateDiscount(discountCode, subtotal, email);
        if (promoDiscount.compareTo(BigDecimal.ZERO) > 0) {
            discountCodeRepository.findByCodeIgnoreCase(discountCode).ifPresent(dc -> {
                dc.setTimesRedeemed(dc.getTimesRedeemed() + 1);
                discountCodeRepository.save(dc);
                order.setDiscountCode(dc.getCode());
                order.setDiscountCodeDescription(dc.getDescription());
            });
        }
        int currentPoints = buyer.getLoyaltyPointsBalance() == null ? 0 : buyer.getLoyaltyPointsBalance();
        int safeRedeem = Math.min(Math.max(0, pointsToRedeem), currentPoints);
        // TrustPoints rule: 1 point = ₱1 discount.
        BigDecimal pointsDiscount = BigDecimal.valueOf(safeRedeem);
        BigDecimal total = subtotal.add(shipping).add(ecoFee).subtract(ecoDiscount).subtract(promoDiscount).subtract(pointsDiscount);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
        // TrustPoints earning rule: 1 point per ₱100 purchase. Points are credited after buyer confirms received.
        int earned = subtotal.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).intValue();
        buyer.setLoyaltyPointsBalance(currentPoints - safeRedeem);
        buyerRepository.save(buyer);

        order.setSubtotal(subtotal);
        order.setShippingFee(shipping);
        order.setEcoPackagingFee(ecoFee);
        order.setEcoDeliveryDiscount(ecoDiscount);
        order.setDiscount(promoDiscount.add(pointsDiscount));
        order.setPromoDiscount(promoDiscount);
        order.setLoyaltyPointsDiscount(pointsDiscount);
        order.setLoyaltyPointsRedeemed(safeRedeem);
        order.setLoyaltyPointsEarned(earned);
        order.setLoyaltyTierAfterOrder(buyer.getLoyaltyTier());
        order.setTotal(total);
        orderRepository.save(order);
        cartService.clear(session);
        return "redirect:/order-success?code=" + order.getOrderCode();
    }

    private BigDecimal calculateDiscount(String code, BigDecimal subtotal, String email) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;
        Optional<DiscountCode> optional = discountCodeRepository.findByCodeIgnoreCase(code.trim());
        if (optional.isEmpty()) return BigDecimal.ZERO;
        DiscountCode dc = optional.get();
        if (!dc.isActive()) return BigDecimal.ZERO;
        if (dc.getMinimumSpend() != null && subtotal.compareTo(dc.getMinimumSpend()) < 0) return BigDecimal.ZERO;
        if (dc.isFirstOrderOnly() && orderRepository.countByEmailIgnoreCase(email) > 0) return BigDecimal.ZERO;
        BigDecimal percent = subtotal.multiply(BigDecimal.valueOf(dc.getPercentOff())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal amount = dc.getAmountOff() == null ? BigDecimal.ZERO : dc.getAmountOff();
        return percent.add(amount).min(subtotal);
    }

    @GetMapping("/order-success")
    public String success(@RequestParam String code, Model model, HttpSession session) {
        addCommon(model, session);
        orderRepository.findByOrderCodeIgnoreCase(code).ifPresent(o -> model.addAttribute("order", o));
        return "order-success";
    }

    @GetMapping("/track")
    public String track(@RequestParam(required = false) String code, Model model, HttpSession session) {
        addCommon(model, session);
        if (code != null && !code.isBlank()) {
            orderRepository.findByOrderCodeIgnoreCase(code).ifPresent(o -> model.addAttribute("order", o));
        }
        return "track";
    }

    @GetMapping("/refund")
    public String refund(Model model, HttpSession session) {
        addCommon(model, session);
        return "refund";
    }

    @PostMapping("/refund")
    public String doRefund(@RequestParam String orderCode, @RequestParam String email,
                           @RequestParam String reason, @RequestParam(required = false) String evidenceUrl,
                           RedirectAttributes ra) {
        RefundRequest r = new RefundRequest();
        r.setOrderCode(orderCode);
        r.setEmail(email);
        r.setReason(reason);
        r.setEvidenceUrl(evidenceUrl);
        orderRepository.findByOrderCodeIgnoreCase(orderCode).ifPresent(r::setOrder);
        refundRepository.save(r);
        ra.addFlashAttribute("success", "Refund request submitted. Status: SUBMITTED");
        return "redirect:/refund";
    }


    @GetMapping("/buyer/profile")
    public String buyerProfile(Model model, HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) {
            ra.addFlashAttribute("error", "Please log in to view your profile.");
            return "redirect:/buyer/login";
        }
        addCommon(model, session);
        List<CustomerOrder> orders = orderRepository.findByEmailWithItems(buyer.getEmail());
        int completedOrders = (int) orders.stream().filter(this::isReviewEligible).count();
        int pendingPoints = orders.stream()
                .filter(o -> !isReviewEligible(o))
                .filter(o -> o.getLoyaltyPointsEarned() != null)
                .filter(o -> o.getOrderStatus() == null || !(o.getOrderStatus().equalsIgnoreCase("CANCELLED") || o.getOrderStatus().equalsIgnoreCase("CANCELED") || o.getOrderStatus().equalsIgnoreCase("REFUNDED")))
                .mapToInt(CustomerOrder::getLoyaltyPointsEarned)
                .sum();
        model.addAttribute("orders", orders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("pendingPoints", pendingPoints);
        model.addAttribute("redeemedPoints", orders.stream().filter(o -> o.getLoyaltyPointsRedeemed() != null).mapToInt(CustomerOrder::getLoyaltyPointsRedeemed).sum());
        return "buyer-profile";
    }

    @GetMapping("/buyer/orders")
    public String buyerOrders(Model model, HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) {
            ra.addFlashAttribute("error", "Please log in to view your orders.");
            return "redirect:/buyer/login";
        }
        addCommon(model, session);
        List<CustomerOrder> orders = orderRepository.findByEmailWithItems(buyer.getEmail());
        model.addAttribute("orders", orders);
        model.addAttribute("toPayOrders", orders.stream().filter(o -> o.getPaymentStatus() != null && o.getPaymentStatus().toLowerCase(Locale.ROOT).contains("due")).toList());
        model.addAttribute("toShipOrders", orders.stream().filter(o -> o.getOrderStatus() != null && (o.getOrderStatus().equalsIgnoreCase("ORDER_CONFIRMED") || o.getOrderStatus().equalsIgnoreCase("PREPARING") || o.getOrderStatus().equalsIgnoreCase("TO_SHIP"))).toList());
        model.addAttribute("toReceiveOrders", orders.stream().filter(o -> o.getOrderStatus() != null && (o.getOrderStatus().equalsIgnoreCase("OUT_FOR_DELIVERY") || o.getOrderStatus().equalsIgnoreCase("TO_RECEIVE") || o.getOrderStatus().equalsIgnoreCase("DELIVERED"))).toList());
        model.addAttribute("completedOrders", orders.stream().filter(this::isReviewEligible).toList());
        model.addAttribute("cancelledOrders", orders.stream().filter(o -> o.getOrderStatus() != null && (o.getOrderStatus().equalsIgnoreCase("CANCELLED") || o.getOrderStatus().equalsIgnoreCase("CANCELED") || o.getOrderStatus().equalsIgnoreCase("REFUNDED"))).toList());
        return "buyer-orders";
    }

    @PostMapping("/buyer/orders/{orderId}/confirm-received")
    public String confirmReceived(@PathVariable Long orderId, HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        CustomerOrder order = orderRepository.findById(orderId).orElseThrow();
        if (order.getEmail() == null || !order.getEmail().equalsIgnoreCase(buyer.getEmail())) {
            ra.addFlashAttribute("error", "You can only confirm your own order.");
            return "redirect:/buyer/orders";
        }
        if (order.getOrderStatus() != null && (order.getOrderStatus().equalsIgnoreCase("CANCELLED") || order.getOrderStatus().equalsIgnoreCase("CANCELED") || order.getOrderStatus().equalsIgnoreCase("REFUNDED"))) {
            ra.addFlashAttribute("error", "Cancelled or refunded orders cannot be confirmed received.");
            return "redirect:/buyer/orders";
        }
        boolean alreadyCompleted = isReviewEligible(order);
        if (!alreadyCompleted) {
            int earned = order.getLoyaltyPointsEarned() == null ? 0 : Math.max(0, order.getLoyaltyPointsEarned());
            buyer.setLoyaltyPointsBalance((buyer.getLoyaltyPointsBalance() == null ? 0 : buyer.getLoyaltyPointsBalance()) + earned);
            buyer.setLifetimeLoyaltyPoints((buyer.getLifetimeLoyaltyPoints() == null ? 0 : buyer.getLifetimeLoyaltyPoints()) + earned);
            buyer.setLifetimeSpend((buyer.getLifetimeSpend() == null ? BigDecimal.ZERO : buyer.getLifetimeSpend()).add(order.getTotal() == null ? BigDecimal.ZERO : order.getTotal()));
            if (buyer.getLifetimeSpend().compareTo(BigDecimal.valueOf(5000)) > 0) buyer.setLoyaltyTier("TrustCart Gold Green Member");
            buyerRepository.save(buyer);
        }
        order.setOrderStatus("COMPLETED");
        order.setTrackingStage("Buyer confirmed received");
        order.setTrackingNote("Buyer confirmed that the order was received. Review and rating are now enabled for purchased products. TrustPoints are credited after confirmation.");
        order.setEscrowStatus("Completed - funds eligible for seller payout after TrustCart rules.");
        orderRepository.save(order);
        ra.addFlashAttribute("success", alreadyCompleted ? "Order is already completed." : "Order confirmed received. TrustPoints were credited and you can now rate purchased products.");
        return "redirect:/buyer/orders#completed";
    }

    @PostMapping("/reviews/product/{id}")
    public String submitProductReview(@PathVariable Long id,
                                      @RequestParam String orderCode,
                                      @RequestParam(defaultValue = "5") int rating,
                                      @RequestParam String reviewText,
                                      @RequestParam(required = false) MultipartFile proofFile,
                                      HttpSession session, RedirectAttributes ra) throws IOException {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        Product product = productRepository.findById(id).orElseThrow();
        Optional<CustomerOrder> optionalOrder = orderRepository.findByOrderCodeIgnoreCase(orderCode);
        if (optionalOrder.isEmpty() || !buyer.getEmail().equalsIgnoreCase(optionalOrder.get().getEmail())) {
            ra.addFlashAttribute("error", "Review requires a valid TrustCart order under your buyer email.");
            return "redirect:/product/" + id;
        }
        CustomerOrder order = optionalOrder.get();
        boolean purchased = order.getItems().stream().anyMatch(item -> item.getProduct() != null && Objects.equals(item.getProduct().getId(), product.getId()));
        if (!purchased) {
            ra.addFlashAttribute("error", "You can only review products you purchased.");
            return "redirect:/product/" + id;
        }
        if (!isReviewEligible(order)) {
            ra.addFlashAttribute("error", "Reviews are enabled only after you confirm received delivery from My Orders.");
            return "redirect:/buyer/orders";
        }
        if (productReviewRepository.existsByBuyerAndProductAndCustomerOrder(buyer, product, order)) {
            ra.addFlashAttribute("error", "You already reviewed this product for the selected order.");
            return "redirect:/buyer/orders";
        }
        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setSeller(product.getSeller());
        review.setBuyer(buyer);
        review.setCustomerOrder(order);
        review.setRating(Math.max(1, Math.min(5, rating)));
        review.setReviewText(reviewText);
        review.setVerifiedPurchase(true);
        String lowerReview = reviewText == null ? "" : reviewText.toLowerCase(Locale.ROOT);
        boolean seriousAccusation = rating <= 2 && (lowerReview.contains("scam") || lowerReview.contains("fake") || lowerReview.contains("fraud") || lowerReview.contains("counterfeit") || lowerReview.contains("sabotage") || lowerReview.contains("harass") || lowerReview.contains("danger"));
        if (seriousAccusation) {
            review.setStatus("PENDING_REVIEW");
            review.setModerationNote("Auto-held for TrustCart Review Integrity check because it contains a serious accusation. Negative reviews are allowed, but severe claims are verified first.");
        } else {
            review.setStatus("PUBLISHED");
            review.setModerationNote("Verified purchase review auto-published.");
        }
        String proofUrl = saveUpload(proofFile);
        review.setAttachmentUrl(proofUrl);
        review.setAttachmentName(proofFile == null ? null : proofFile.getOriginalFilename());
        productReviewRepository.save(review);
        if ("PENDING_REVIEW".equalsIgnoreCase(review.getStatus())) {
            ra.addFlashAttribute("success", "Verified purchase review submitted and queued for TrustCart moderation because it contains a serious claim.");
        } else {
            ra.addFlashAttribute("success", "Verified purchase review submitted and published.");
        }
        return "redirect:/product/" + id;
    }

    @GetMapping("/autoship")
    public String autoship(Model model, HttpSession session) {
        addCommon(model, session);
        model.addAttribute("eligibleProducts", productRepository.findBySubscriptionEligibleTrueAndStatusOrderByCreatedAtDesc("APPROVED").stream().limit(48).toList());
        BuyerAccount buyer = currentBuyer(session);
        model.addAttribute("subscriptions", buyer == null ? List.of() : autoshipRepository.findByBuyerOrderByCreatedAtDesc(buyer));
        return "autoship";
    }

    @PostMapping("/autoship/create")
    public String createAutoship(@RequestParam Long productId, @RequestParam String frequency, @RequestParam(defaultValue = "1") int quantity,
                                 HttpSession session, RedirectAttributes ra) {
        BuyerAccount buyer = currentBuyer(session);
        if (buyer == null) return "redirect:/buyer/login";
        Product product = productRepository.findById(productId).orElseThrow();
        AutoshipSubscription sub = new AutoshipSubscription();
        sub.setBuyer(buyer);
        sub.setProduct(product);
        sub.setFrequency(frequency);
        sub.setQuantity(quantity);
        sub.setRecurringPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        sub.setSubscriptionDiscountPercent(product.getSubscriptionDiscountPercent());
        sub.setNextShipmentDate(LocalDate.now().plusMonths(1));
        sub.setProtectionNote("Autoship remains protected inside TrustCart. Seller exact address is not shown to buyers.");
        autoshipRepository.save(sub);
        ra.addFlashAttribute("success", "Autoship subscription created.");
        return "redirect:/autoship";
    }

    @GetMapping("/try-on")
    public String tryOn(Model model, HttpSession session) {
        addCommon(model, session);
        List<Product> items = productRepository.findByTryOnEligibleTrueAndStatusOrderByNameAsc("APPROVED");
        model.addAttribute("tryOnItems", items);
        model.addAttribute("menItems", items.stream().filter(p -> "MEN".equalsIgnoreCase(p.getTryOnGender())).toList());
        model.addAttribute("womenItems", items.stream().filter(p -> "WOMEN".equalsIgnoreCase(p.getTryOnGender())).toList());
        return "try-on";
    }
}
