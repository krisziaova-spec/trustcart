package com.trustcart.controller;

import com.trustcart.model.CustomerOrder;
import com.trustcart.model.DiscountCode;
import com.trustcart.model.OrderItem;
import com.trustcart.model.Product;
import com.trustcart.model.ProductCategory;
import com.trustcart.model.Seller;
import com.trustcart.model.SupportTicket;
import com.trustcart.model.ProductReview;
import com.trustcart.model.BuyerAccount;
import com.trustcart.model.ChatMessage;
import com.trustcart.model.ChatThread;
import com.trustcart.model.IncomingStockShipment;
import com.trustcart.model.TrustCartWarehouse;
import com.trustcart.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/seller")
public class SellerController {
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final CustomerOrderRepository orderRepository;
    private final SupportTicketRepository ticketRepository;
    private final BuyerAccountRepository buyerRepository;
    private final ChatThreadRepository chatThreadRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final IncomingStockShipmentRepository incomingStockRepository;
    private final TrustCartWarehouseRepository warehouseRepository;
    private final ProductReviewRepository productReviewRepository;

    public SellerController(SellerRepository sellerRepository, ProductRepository productRepository,
                            DiscountCodeRepository discountCodeRepository, CustomerOrderRepository orderRepository,
                            SupportTicketRepository ticketRepository, BuyerAccountRepository buyerRepository,
                            ChatThreadRepository chatThreadRepository, ChatMessageRepository chatMessageRepository,
                            IncomingStockShipmentRepository incomingStockRepository, TrustCartWarehouseRepository warehouseRepository,
                            ProductReviewRepository productReviewRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.buyerRepository = buyerRepository;
        this.chatThreadRepository = chatThreadRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.incomingStockRepository = incomingStockRepository;
        this.warehouseRepository = warehouseRepository;
        this.productReviewRepository = productReviewRepository;
    }


    private String saveUpload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String originalName = file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename();
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

    private String firstNonBlank(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }

    private BigDecimal sumLineTotals(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getLineTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> sellerOrderItems(List<CustomerOrder> sellerOrders, Seller seller) {
        return sellerOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getSeller() != null
                        && Objects.equals(item.getProduct().getSeller().getId(), seller.getId()))
                .toList();
    }

    private BigDecimal percentFee(BigDecimal base, String rate) {
        if (base == null) return BigDecimal.ZERO;
        return base.multiply(new BigDecimal(rate)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal subtractSafe(BigDecimal base, BigDecimal... deductions) {
        BigDecimal result = base == null ? BigDecimal.ZERO : base;
        for (BigDecimal deduction : deductions) {
            if (deduction != null) result = result.subtract(deduction);
        }
        return result.max(BigDecimal.ZERO);
    }

    private Map<String, BigDecimal> topProductSales(List<OrderItem> items) {
        return items.stream()
                .collect(Collectors.groupingBy(item -> item.getProductName() == null ? "Unknown Product" : item.getProductName(),
                        Collectors.mapping(item -> item.getLineTotal() == null ? BigDecimal.ZERO : item.getLineTotal(),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, java.util.LinkedHashMap::new));
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

    private Seller currentSeller(HttpSession session) {
        Object id = session.getAttribute("sellerId");
        if (id instanceof Long sellerId) return sellerRepository.findById(sellerId).orElse(null);
        return null;
    }

    private void sellerCommon(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        model.addAttribute("sellerLoggedIn", seller != null);
        model.addAttribute("seller", seller);
        model.addAttribute("categories", ProductCategory.storefrontCategories());
    }

    @GetMapping
    public String sellerCentre(Model model, HttpSession session) {
        sellerCommon(model, session);
        return "seller-centre";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        sellerCommon(model, session);
        return "seller-login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes ra) {
        Optional<Seller> seller = sellerRepository.findByEmailIgnoreCase(email);
        if (seller.isPresent() && Objects.equals(seller.get().getPassword(), password)) {
            Seller account = seller.get();
            if (!("APPROVED".equalsIgnoreCase(account.getStatus()) || "ACTIVE".equalsIgnoreCase(account.getStatus()))) {
                ra.addFlashAttribute("error", "Your seller account is currently " + account.getStatus() + ". Please wait for admin approval or contact TrustCart support.");
                return "redirect:/seller/login";
            }
            session.setAttribute("sellerId", account.getId());
            return "redirect:/seller/dashboard";
        }
        ra.addFlashAttribute("error", "Invalid seller email or password.");
        return "redirect:/seller/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("sellerId");
        return "redirect:/seller";
    }

    @GetMapping("/apply")
    public String apply(Model model, HttpSession session) {
        sellerCommon(model, session);
        return "seller-apply";
    }

    @PostMapping("/apply")
    public String doApply(@RequestParam String storeName, @RequestParam String email, @RequestParam String password,
                          @RequestParam String businessType, @RequestParam String storeExactAddress,
                          @RequestParam String storeCity, @RequestParam String storeProvince,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) Double latitude,
                          @RequestParam(required = false) Double longitude,
                          @RequestParam(required = false, defaultValue = "SELLER") String fulfillmentPreference,
                          HttpSession session, RedirectAttributes ra) {
        if (sellerRepository.existsByEmailIgnoreCase(email)) {
            ra.addFlashAttribute("error", "Seller email already exists.");
            return "redirect:/seller/apply";
        }
        Seller seller = new Seller();
        seller.setStoreName(storeName);
        seller.setEmail(email);
        seller.setPassword(password);
        seller.setBusinessType(businessType);
        seller.setPhone(phone);
        seller.setStoreExactAddress(storeExactAddress);
        seller.setStoreCity(storeCity);
        seller.setStoreProvince(storeProvince);
        seller.setLatitude(latitude == null ? 14.0683 : latitude);
        seller.setLongitude(longitude == null ? 121.3256 : longitude);
        seller.setStatus("PENDING");
        seller.setStoreLocationVerified(false);
        seller.setApprovedAt(null);
        seller.setApprovedBy(null);
        seller.setBusinessVerified(false);
        seller.setIdentityVerified(false);
        seller.setDocumentVerified(false);
        seller.setProductComplianceChecked(false);
        seller.setRequirementsStatus("REQUIREMENTS_SENT");
        seller.setRequirementsNote("Email sent: please submit valid government ID, proof of address, business permit if applicable, and selfie with ID. Admin approval is required before login.");
        seller.setFulfillmentPreference(fulfillmentPreference);
        seller.setCanUseFbt(false);
        seller.setStoreDescription("Verified TrustCart seller with protected checkout and seller-area visibility only.");
        seller.setStoreProfileImageUrl("https://images.unsplash.com/photo-1556745753-b2904692b3cd?auto=format&fit=crop&w=700&q=80");
        seller.setStoreBannerImageUrl("https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1600&q=80");
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Seller application submitted. TrustCart has sent the requirements email. You can log in only after admin approval.");
        return "redirect:/seller/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        List<Product> sellerProducts = productRepository.findBySellerOrderByCreatedAtDesc(seller);
        List<DiscountCode> sellerDiscounts = discountCodeRepository.findBySellerOrderByCreatedAtDesc(seller);
        List<CustomerOrder> sellerOrders = orderRepository.findOrdersForSeller(seller);
        BigDecimal salesTotal = orderRepository.sumSalesForSeller(seller);
        Long unitsSold = orderRepository.sumUnitsForSeller(seller);
        model.addAttribute("products", sellerProducts);
        model.addAttribute("discounts", sellerDiscounts);
        model.addAttribute("orders", sellerOrders.stream().limit(20).toList());
        model.addAttribute("salesTotal", salesTotal == null ? BigDecimal.ZERO : salesTotal);
        model.addAttribute("unitsSold", unitsSold == null ? 0 : unitsSold);
        model.addAttribute("orderCount", sellerOrders.size());
        model.addAttribute("messageThreads", chatThreadRepository.findBySellerOrderByUpdatedAtDesc(seller).stream().limit(5).toList());
        model.addAttribute("sellerTickets", ticketRepository.findBySellerOrderByCreatedAtDesc(seller).stream().limit(5).toList());
        model.addAttribute("reviewAppealsOpen", productReviewRepository.findBySellerOrderByCreatedAtDesc(seller).stream().filter(ProductReview::isAppealOpen).count());
        return "seller-dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (currentSeller(session) == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        return "seller-profile-form";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String storeName,
                                @RequestParam(required = false) String businessType,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String storeDescription,
                                @RequestParam(required = false) String storeProfileImageUrl,
                                @RequestParam(required = false) String storeBannerImageUrl,
                                @RequestParam(required = false) MultipartFile storeProfileImage,
                                @RequestParam(required = false) MultipartFile storeBannerImage,
                                @RequestParam(required = false) String sustainabilityBadge,
                                @RequestParam(required = false) String ecoCommitment,
                                @RequestParam(required = false) String storeCity,
                                @RequestParam(required = false) String storeProvince,
                                @RequestParam(required = false) Double latitude,
                                @RequestParam(required = false) Double longitude,
                                @RequestParam(required = false, defaultValue = "false") boolean pickupAvailable,
                                @RequestParam(required = false, defaultValue = "5") Integer serviceRadiusKm,
                                HttpSession session, RedirectAttributes ra) throws IOException {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        seller.setStoreName(storeName);
        seller.setBusinessType(businessType);
        seller.setPhone(phone);
        seller.setStoreDescription(storeDescription);
        seller.setStoreProfileImageUrl(firstNonBlank(saveUpload(storeProfileImage), storeProfileImageUrl));
        seller.setStoreBannerImageUrl(firstNonBlank(saveUpload(storeBannerImage), storeBannerImageUrl));
        seller.setSustainabilityBadge(sustainabilityBadge);
        seller.setEcoCommitment(ecoCommitment);
        seller.setStoreCity(storeCity);
        seller.setStoreProvince(storeProvince);
        if (latitude != null) seller.setLatitude(latitude);
        if (longitude != null) seller.setLongitude(longitude);
        seller.setPickupAvailable(pickupAvailable);
        seller.setServiceRadiusKm(serviceRadiusKm == null ? 5 : serviceRadiusKm);
        sellerRepository.save(seller);
        ra.addFlashAttribute("success", "Storefront profile updated.");
        return "redirect:/seller/dashboard";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model, HttpSession session) {
        if (currentSeller(session) == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        return "seller-product-form";
    }

    @PostMapping("/products")
    public String createProduct(@RequestParam String name, @RequestParam String description,
                                @RequestParam ProductCategory category, @RequestParam BigDecimal price,
                                @RequestParam Integer stock,
                                @RequestParam(required = false) String imageUrl,
                                @RequestParam(required = false) MultipartFile productImage,
                                @RequestParam(required = false) String productOrigin,
                                @RequestParam(required = false) String sustainabilityTag,
                                @RequestParam(required = false, defaultValue = "false") boolean ecoFriendly,
                                @RequestParam(required = false, defaultValue = "false") boolean locallySourced,
                                @RequestParam(required = false, defaultValue = "false") boolean subscriptionEligible,
                                @RequestParam(required = false, defaultValue = "5") int subscriptionDiscountPercent,
                                @RequestParam(required = false, defaultValue = "false") boolean tryOnEligible,
                                @RequestParam(required = false) String tryOnGender,
                                @RequestParam(required = false) String tryOnAssetUrl,
                                @RequestParam(required = false, defaultValue = "false") boolean requestFbt,
                                HttpSession session) throws IOException {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setPrice(price);
        p.setStock(stock);
        p.setImageUrl(firstNonBlank(saveUpload(productImage), firstNonBlank(imageUrl, "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=80")));
        p.setPhotoAltText(name + " product photo");
        p.setProductOrigin(productOrigin == null || productOrigin.isBlank() ? seller.getPublicLocationLabel() : productOrigin);
        p.setSustainabilityTag(sustainabilityTag == null || sustainabilityTag.isBlank() ? "Seller-managed verified listing" : sustainabilityTag);
        p.setEcoFriendly(ecoFriendly);
        p.setLocallySourced(locallySourced);
        p.setSubscriptionEligible(subscriptionEligible);
        p.setSubscriptionDiscountPercent(Math.max(0, subscriptionDiscountPercent));
        p.setTryOnEligible(tryOnEligible);
        p.setTryOnGender(tryOnGender);
        p.setTryOnAssetUrl(tryOnAssetUrl);
        p.setTrustScore(92);
        p.setGreenScore(ecoFriendly ? 95 : 75);
        p.setReviewSummary("Seller-uploaded product. Buyer protection applies after checkout.");
        p.setRedFlagSummary("New seller product. Transactions must remain inside TrustCart.");
        p.setSeller(seller);
        p.setStatus("APPROVED");
        if (requestFbt && seller.isCanUseFbt()) {
            p.setFulfilledBy("TRUSTCART");
            p.setFulfillmentStatus("TRUSTCART_APPROVED");
            p.setTrustCartStock(stock);
            p.setFulfillmentNote("Inventory is recorded as received and managed by TrustCart for prototype fulfillment.");
            p.setEstimatedDelivery("ETA: TrustCart hub delivery");
        } else if (requestFbt) {
            p.setFulfilledBy("SELLER");
            p.setFulfillmentStatus("PENDING_TRUSTCART_REVIEW");
            p.setFulfillmentNote("Seller requested Fulfilled by TrustCart. Admin must approve after inventory verification.");
        } else {
            p.setFulfilledBy("SELLER");
            p.setFulfillmentStatus("SELLER_MANAGED");
            p.setFulfillmentNote("Seller stores, packs, and ships this product.");
        }
        productRepository.save(p);
        return "redirect:/seller/dashboard";
    }

    @GetMapping("/discounts/new")
    public String newDiscount(Model model, HttpSession session) {
        if (currentSeller(session) == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        return "seller-discount-form";
    }

    @PostMapping("/discounts")
    public String createDiscount(@RequestParam String code, @RequestParam String description,
                                 @RequestParam(defaultValue = "0") BigDecimal minimumSpend,
                                 @RequestParam(defaultValue = "0") int percentOff,
                                 @RequestParam(defaultValue = "0") BigDecimal amountOff,
                                 @RequestParam(required = false, defaultValue = "false") boolean firstOrderOnly,
                                 @RequestParam(required = false, defaultValue = "false") boolean subscriptionBoost,
                                 HttpSession session, RedirectAttributes ra) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        if (discountCodeRepository.findByCodeIgnoreCase(code).isPresent()) {
            ra.addFlashAttribute("error", "Discount code already exists.");
            return "redirect:/seller/discounts/new";
        }
        DiscountCode dc = new DiscountCode();
        dc.setCode(code.toUpperCase().trim());
        dc.setDescription(description);
        dc.setMinimumSpend(minimumSpend);
        dc.setPercentOff(percentOff);
        dc.setAmountOff(amountOff);
        dc.setFirstOrderOnly(firstOrderOnly);
        dc.setSubscriptionBoost(subscriptionBoost);
        dc.setSeller(seller);
        dc.setCreatedBySeller(seller.getStoreName());
        discountCodeRepository.save(dc);
        return "redirect:/seller/dashboard";
    }



    @GetMapping("/fulfillment")
    public String fulfillmentDashboard(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        sellerCommon(model, session);

        List<Product> products = productRepository.findBySellerOrderByCreatedAtDesc(seller);
        List<Product> fbtProducts = products.stream().filter(Product::isFulfilledByTrustCart).toList();
        List<Product> pendingFbtProducts = products.stream()
                .filter(p -> "PENDING_TRUSTCART_REVIEW".equalsIgnoreCase(p.getFulfillmentStatus()))
                .toList();
        List<Product> fbsProducts = products.stream().filter(p -> !p.isFulfilledByTrustCart()).toList();
        List<CustomerOrder> sellerOrders = orderRepository.findOrdersForSeller(seller);
        List<OrderItem> sellerItems = sellerOrderItems(sellerOrders, seller);
        List<OrderItem> fbtItems = sellerItems.stream()
                .filter(item -> item.getProduct() != null && item.getProduct().isFulfilledByTrustCart())
                .toList();
        List<OrderItem> fbsItems = sellerItems.stream()
                .filter(item -> item.getProduct() == null || !item.getProduct().isFulfilledByTrustCart())
                .toList();
        List<CustomerOrder> fbtOrders = sellerOrders.stream()
                .filter(order -> order.getItems().stream().anyMatch(item -> item.getProduct() != null
                        && item.getProduct().getSeller() != null
                        && Objects.equals(item.getProduct().getSeller().getId(), seller.getId())
                        && item.getProduct().isFulfilledByTrustCart()))
                .toList();

        BigDecimal fbtSales = sumLineTotals(fbtItems);
        BigDecimal fbsSales = sumLineTotals(fbsItems);
        BigDecimal fbtFees = percentFee(fbtSales, "0.08");
        BigDecimal platformFees = percentFee(fbtSales.add(fbsSales), "0.03");

        model.addAttribute("products", products);
        model.addAttribute("fbtProducts", fbtProducts);
        model.addAttribute("pendingFbtProducts", pendingFbtProducts);
        model.addAttribute("fbsProducts", fbsProducts);
        model.addAttribute("fbtOrders", fbtOrders);
        model.addAttribute("fbtSales", fbtSales);
        model.addAttribute("fbsSales", fbsSales);
        model.addAttribute("fbtFees", fbtFees);
        model.addAttribute("platformFees", platformFees);
        model.addAttribute("estimatedNetPayout", subtractSafe(fbtSales.add(fbsSales), fbtFees, platformFees));
        model.addAttribute("trustCartStockTotal", fbtProducts.stream().mapToInt(Product::getTrustCartStock).sum());
        model.addAttribute("fbtUnitsSold", fbtItems.stream().mapToInt(OrderItem::getQuantity).sum());
        model.addAttribute("fbsUnitsSold", fbsItems.stream().mapToInt(OrderItem::getQuantity).sum());
        model.addAttribute("incomingStocks", incomingStockRepository.findBySellerOrderByCreatedAtDesc(seller));
        model.addAttribute("warehouses", warehouseRepository.findByActiveTrueOrderByCityAsc());
        model.addAttribute("stockProducts", products);
        return "seller-fulfillment";
    }

    @GetMapping("/analytics")
    public String sellerAnalytics(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        sellerCommon(model, session);

        List<Product> products = productRepository.findBySellerOrderByCreatedAtDesc(seller);
        List<CustomerOrder> sellerOrders = orderRepository.findOrdersForSeller(seller);
        List<OrderItem> sellerItems = sellerOrderItems(sellerOrders, seller);
        List<OrderItem> fbtItems = sellerItems.stream()
                .filter(item -> item.getProduct() != null && item.getProduct().isFulfilledByTrustCart())
                .toList();
        List<OrderItem> fbsItems = sellerItems.stream()
                .filter(item -> item.getProduct() == null || !item.getProduct().isFulfilledByTrustCart())
                .toList();

        BigDecimal grossSales = sumLineTotals(sellerItems);
        BigDecimal fbtSales = sumLineTotals(fbtItems);
        BigDecimal fbsSales = sumLineTotals(fbsItems);
        BigDecimal fbtFees = percentFee(fbtSales, "0.08");
        BigDecimal platformFees = percentFee(grossSales, "0.03");

        model.addAttribute("products", products);
        model.addAttribute("orders", sellerOrders);
        model.addAttribute("grossSales", grossSales);
        model.addAttribute("fbtSales", fbtSales);
        model.addAttribute("fbsSales", fbsSales);
        model.addAttribute("fbtFees", fbtFees);
        model.addAttribute("platformFees", platformFees);
        model.addAttribute("netPayout", subtractSafe(grossSales, fbtFees, platformFees));
        model.addAttribute("unitsSold", sellerItems.stream().mapToInt(OrderItem::getQuantity).sum());
        model.addAttribute("fbtOrderCount", sellerOrders.stream().filter(order -> order.getItems().stream().anyMatch(item -> item.getProduct() != null && item.getProduct().isFulfilledByTrustCart())).count());
        model.addAttribute("fbsOrderCount", sellerOrders.stream().filter(order -> order.getItems().stream().anyMatch(item -> item.getProduct() == null || !item.getProduct().isFulfilledByTrustCart())).count());
        model.addAttribute("topProductSales", topProductSales(sellerItems));
        return "seller-analytics";
    }


    @GetMapping("/analytics/export")
    public ResponseEntity<String> exportSellerAnalytics(HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return csvDownload("seller-analytics.csv", "Please login as seller first\n");
        List<CustomerOrder> sellerOrders = orderRepository.findOrdersForSeller(seller);
        StringBuilder csv = new StringBuilder("Order Code,Buyer,Status,Payment,Escrow,Total,Created At\n");
        for (CustomerOrder order : sellerOrders) {
            csv.append(csvCell(order.getOrderCode())).append(',')
               .append(csvCell(order.getFullName())).append(',')
               .append(csvCell(order.getOrderStatus())).append(',')
               .append(csvCell(order.getPaymentStatus())).append(',')
               .append(csvCell(order.getEscrowStatus())).append(',')
               .append(csvCell(order.getTotal())).append(',')
               .append(csvCell(order.getCreatedAt())).append('\n');
        }
        return csvDownload("trustcart-seller-analytics.csv", csv.toString());
    }

    @GetMapping("/fulfillment/export")
    public ResponseEntity<String> exportSellerFulfillment(HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return csvDownload("seller-fulfillment.csv", "Please login as seller first\n");
        StringBuilder csv = new StringBuilder("Product,Fulfilled By,Fulfillment Status,Seller Stock,TrustCart Stock,Note\n");
        for (Product p : productRepository.findBySellerOrderByCreatedAtDesc(seller)) {
            csv.append(csvCell(p.getName())).append(',')
               .append(csvCell(p.getFulfillmentLabel())).append(',')
               .append(csvCell(p.getFulfillmentStatus())).append(',')
               .append(csvCell(p.getStock())).append(',')
               .append(csvCell(p.getTrustCartStock())).append(',')
               .append(csvCell(p.getFulfillmentNote())).append('\n');
        }
        return csvDownload("trustcart-seller-fulfillment.csv", csv.toString());
    }

    @GetMapping("/incoming-stocks/export")
    public ResponseEntity<String> exportIncomingStocks(HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return csvDownload("incoming-stocks.csv", "Please login as seller first\n");
        StringBuilder csv = new StringBuilder("Shipment Code,Product,Warehouse,Planned,Received,Status,Drop-off Date,Admin Note\n");
        for (IncomingStockShipment stock : incomingStockRepository.findBySellerOrderByCreatedAtDesc(seller)) {
            csv.append(csvCell(stock.getShipmentCode())).append(',')
               .append(csvCell(stock.getProduct() == null ? "" : stock.getProduct().getName())).append(',')
               .append(csvCell(stock.getWarehouse() == null ? "" : stock.getWarehouse().getDisplayLabel())).append(',')
               .append(csvCell(stock.getQuantityPlanned())).append(',')
               .append(csvCell(stock.getQuantityReceived())).append(',')
               .append(csvCell(stock.getStatus())).append(',')
               .append(csvCell(stock.getPreferredDropoffDate())).append(',')
               .append(csvCell(stock.getAdminNote())).append('\n');
        }
        return csvDownload("trustcart-incoming-stocks.csv", csv.toString());
    }

    @PostMapping("/products/{id}/request-fbt")
    public String requestFbtForProduct(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        Product product = productRepository.findById(id).orElseThrow();
        if (product.getSeller() == null || !Objects.equals(product.getSeller().getId(), seller.getId())) {
            ra.addFlashAttribute("error", "You can request FBT only for your own products.");
            return "redirect:/seller/fulfillment";
        }
        product.setFulfilledBy("SELLER");
        product.setFulfillmentStatus("PENDING_TRUSTCART_REVIEW");
        product.setFulfillmentNote("Seller requested Fulfilled by TrustCart. TrustCart Admin must verify inventory before approval.");
        productRepository.save(product);
        ra.addFlashAttribute("success", "FBT request submitted. TrustCart Admin will review inventory requirements.");
        return "redirect:/seller/fulfillment";
    }


    @PostMapping("/incoming-stocks")
    public String createIncomingStock(@RequestParam Long productId,
                                      @RequestParam Long warehouseId,
                                      @RequestParam(defaultValue = "0") int quantityPlanned,
                                      @RequestParam(required = false) String preferredDropoffDate,
                                      @RequestParam(required = false) String sellerNote,
                                      HttpSession session, RedirectAttributes ra) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        Product product = productRepository.findById(productId).orElseThrow();
        if (product.getSeller() == null || !Objects.equals(product.getSeller().getId(), seller.getId())) {
            ra.addFlashAttribute("error", "You can create incoming stock only for your own product.");
            return "redirect:/seller/fulfillment";
        }
        TrustCartWarehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        IncomingStockShipment stock = new IncomingStockShipment();
        stock.setShipmentCode("TC-IN-" + System.currentTimeMillis());
        stock.setSeller(seller);
        stock.setProduct(product);
        stock.setWarehouse(warehouse);
        stock.setQuantityPlanned(Math.max(1, quantityPlanned));
        if (preferredDropoffDate != null && !preferredDropoffDate.isBlank()) {
            try { stock.setPreferredDropoffDate(java.time.LocalDate.parse(preferredDropoffDate)); } catch (Exception ignored) { }
        }
        stock.setSellerNote(sellerNote);
        stock.setStatus("PENDING_DROP_OFF");
        incomingStockRepository.save(stock);
        product.setFulfillmentStatus("PENDING_TRUSTCART_REVIEW");
        product.setFulfillmentNote("Incoming stock created for TrustCart warehouse review: " + stock.getShipmentCode());
        productRepository.save(product);
        ra.addFlashAttribute("success", "Incoming stock created. Drop-off code: " + stock.getShipmentCode());
        return "redirect:/seller/fulfillment";
    }

    @GetMapping("/messages")
    public String sellerMessages(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        model.addAttribute("threads", chatThreadRepository.findBySellerOrderByUpdatedAtDesc(seller));
        return "seller-messages";
    }

    @GetMapping("/messages/{threadId}")
    public String sellerMessageThread(@PathVariable Long threadId, Model model, HttpSession session, RedirectAttributes ra) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        ChatThread thread = chatThreadRepository.findById(threadId).orElseThrow();
        if (thread.getSeller() == null || !Objects.equals(thread.getSeller().getId(), seller.getId())) {
            ra.addFlashAttribute("error", "You can only view messages for your own store.");
            return "redirect:/seller/messages";
        }
        sellerCommon(model, session);
        model.addAttribute("thread", thread);
        model.addAttribute("messages", chatMessageRepository.findByThreadOrderByCreatedAtAsc(thread));
        return "seller-chat-thread";
    }

    @PostMapping("/messages/{threadId}/reply")
    public String sellerReply(@PathVariable Long threadId, @RequestParam String message,
                              @RequestParam(required = false) MultipartFile attachment,
                              HttpSession session, RedirectAttributes ra) throws IOException {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        ChatThread thread = chatThreadRepository.findById(threadId).orElseThrow();
        if (thread.getSeller() == null || !Objects.equals(thread.getSeller().getId(), seller.getId())) {
            ra.addFlashAttribute("error", "You can only reply to messages for your own store.");
            return "redirect:/seller/messages";
        }
        saveChatMessage(thread, "SELLER", seller.getStoreName(), seller.getEmail(), message, attachment);
        return "redirect:/seller/messages/" + threadId;
    }

    @GetMapping("/support/contact")
    public String sellerSupport(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        return "seller-support-contact";
    }

    @PostMapping("/support/contact")
    public String submitSellerSupport(@RequestParam String subject, @RequestParam String message,
                                      @RequestParam(required = false) MultipartFile proofFile,
                                      HttpSession session, RedirectAttributes ra) throws IOException {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketCode("TC-TICKET-" + System.currentTimeMillis());
        ticket.setType("SELLER_SUPPORT");
        ticket.setSourceRole("SELLER");
        ticket.setStatus("OPEN");
        ticket.setPriority("NORMAL");
        ticket.setSeller(seller);
        ticket.setReporterName(seller.getStoreName());
        ticket.setReporterEmail(seller.getEmail());
        ticket.setSubject(subject);
        ticket.setMessage(message);
        String proofUrl = saveUpload(proofFile);
        ticket.setAttachmentUrl(proofUrl);
        ticket.setAttachmentName(proofFile == null ? null : proofFile.getOriginalFilename());
        ticketRepository.save(ticket);
        ra.addFlashAttribute("success", "Seller support ticket opened. TrustCart Admin will review it in the Seller Concerns tab.");
        return "redirect:/seller/support/contact";
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


    @GetMapping("/reviews")
    public String sellerReviews(Model model, HttpSession session) {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        sellerCommon(model, session);
        List<ProductReview> reviews = productReviewRepository.findBySellerOrderByCreatedAtDesc(seller);
        model.addAttribute("reviews", reviews);
        model.addAttribute("publishedReviews", reviews.stream().filter(r -> "PUBLISHED".equalsIgnoreCase(r.getStatus())).toList());
        model.addAttribute("appealedReviews", reviews.stream().filter(ProductReview::isAppealOpen).toList());
        model.addAttribute("hiddenReviews", reviews.stream().filter(r -> "HIDDEN".equalsIgnoreCase(r.getStatus()) || "REMOVED".equalsIgnoreCase(r.getStatus()) || "FLAGGED".equalsIgnoreCase(r.getStatus()) || "PENDING_REVIEW".equalsIgnoreCase(r.getStatus())).toList());
        return "seller-reviews";
    }

    @PostMapping("/reviews/{reviewId}/appeal")
    public String appealReview(@PathVariable Long reviewId,
                               @RequestParam String reason,
                               @RequestParam(required = false) MultipartFile proofFile,
                               HttpSession session, RedirectAttributes ra) throws IOException {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";
        ProductReview review = productReviewRepository.findById(reviewId).orElseThrow();
        if (review.getSeller() == null || !Objects.equals(review.getSeller().getId(), seller.getId())) {
            ra.addFlashAttribute("error", "You can appeal only reviews for your own store.");
            return "redirect:/seller/reviews";
        }
        String proofUrl = saveUpload(proofFile);
        review.setAppealOpen(true);
        review.setAppealReason(reason);
        review.setAppealProofUrl(proofUrl);
        review.setAppealProofName(proofFile == null ? null : proofFile.getOriginalFilename());
        review.setAppealedAt(LocalDateTime.now());
        if ("PUBLISHED".equalsIgnoreCase(review.getStatus())) {
            review.setStatus("FLAGGED");
            review.setModerationNote("Seller requested review investigation. Review is flagged for TrustCart Admin review, not automatically deleted.");
        }
        productReviewRepository.save(review);

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketCode("TC-REV-" + System.currentTimeMillis());
        ticket.setType("REVIEW_APPEAL");
        ticket.setSourceRole("SELLER");
        ticket.setStatus("OPEN");
        ticket.setPriority("HIGH");
        ticket.setSeller(seller);
        ticket.setBuyer(review.getBuyer());
        ticket.setProduct(review.getProduct());
        ticket.setProductReview(review);
        ticket.setReporterName(seller.getStoreName());
        ticket.setReporterEmail(seller.getEmail());
        ticket.setSubject("Review appeal for " + (review.getProduct() == null ? "product" : review.getProduct().getName()));
        ticket.setMessage(reason);
        ticket.setAttachmentUrl(proofUrl);
        ticket.setAttachmentName(proofFile == null ? null : proofFile.getOriginalFilename());
        ticketRepository.save(ticket);
        ra.addFlashAttribute("success", "Review appeal submitted. TrustCart Admin will investigate with the proof you provided.");
        return "redirect:/seller/reviews";
    }

    @GetMapping("/report-buyer")
    public String reportBuyerForm(Model model, HttpSession session, RedirectAttributes ra) {
        Seller seller = currentSeller(session);
        if (seller == null) {
            ra.addFlashAttribute("error", "Please log in as seller first.");
            return "redirect:/seller/login";
        }
        sellerCommon(model, session);
        return "seller-report-buyer";
    }

    @PostMapping("/report-buyer")
    public String reportBuyer(@RequestParam String buyerEmail,
                              @RequestParam String reason,
                              @RequestParam(required = false) String orderCode,
                              @RequestParam(required = false) MultipartFile proofFile,
                              HttpSession session, RedirectAttributes ra) throws IOException {
        Seller seller = currentSeller(session);
        if (seller == null) return "redirect:/seller/login";

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketCode("TC-TICKET-" + System.currentTimeMillis());
        ticket.setType("BUYER_REPORT");
        ticket.setSourceRole("SELLER");
        ticket.setStatus("OPEN");
        ticket.setPriority("HIGH");
        ticket.setReporterName(seller.getStoreName());
        ticket.setReporterEmail(seller.getEmail());
        ticket.setSeller(seller);
        ticket.setReportedBuyerEmail(buyerEmail);
        ticket.setSubject("Seller reported buyer account" + (orderCode == null || orderCode.isBlank() ? "" : " - Order " + orderCode));
        ticket.setMessage(reason);
        String proofUrl = saveUpload(proofFile);
        ticket.setAttachmentUrl(proofUrl);
        ticket.setAttachmentName(proofFile == null ? null : proofFile.getOriginalFilename());
        buyerRepository.findByEmailIgnoreCase(buyerEmail).ifPresent(buyer -> {
            ticket.setBuyer(buyer);
            buyer.setReportCount((buyer.getReportCount() == null ? 0 : buyer.getReportCount()) + 1);
            buyerRepository.save(buyer);
        });
        ticketRepository.save(ticket);
        ra.addFlashAttribute("success", "Buyer report submitted to TrustCart Admin for review.");
        return "redirect:/seller/dashboard";
    }

}
