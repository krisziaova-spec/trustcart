package com.trustcart.config;

import com.trustcart.model.*;
import com.trustcart.repository.BuyerAccountRepository;
import com.trustcart.repository.DiscountCodeRepository;
import com.trustcart.repository.ProductRepository;
import com.trustcart.repository.SellerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(SellerRepository sellerRepository,
                               ProductRepository productRepository,
                               BuyerAccountRepository buyerRepository,
                               DiscountCodeRepository discountCodeRepository) {
        return args -> {
            buyerRepository.findByEmailIgnoreCase("buyer@trustcart.ph").orElseGet(() -> {
                BuyerAccount buyer = new BuyerAccount("Juan Trust", "buyer@trustcart.ph", "09179990000", "trust123", "San Pablo City", 14.0683, 121.3256, 5);
                buyer.setDefaultAddress("San Pablo City, Laguna");
                buyer.setLoyaltyPointsBalance(350);
                buyer.setLifetimeLoyaltyPoints(350);
                return buyerRepository.save(buyer);
            });

            Seller greenTech = sellerRepository.findByEmailIgnoreCase("greentech@trustcart.ph").orElseGet(() -> {
                Seller seller = new Seller("GreenTech Manila", "greentech@trustcart.ph", "09170000001", "Verified electronics reseller", "Plastic-Free Packaging", 95, SellerStatus.APPROVED);
                seller.setPassword("trust123");
                setLocation(seller, "GreenTech Manila Fulfillment Office, Sampaloc", "Manila", "Metro Manila", 14.5995, 120.9842, 8, true);
                seller.setEcoCommitment("Plastic-reduced electronics packaging");
                seller.markVerifiedDefaults();
                return sellerRepository.save(seller);
            });

            Seller localGoods = sellerRepository.findByEmailIgnoreCase("localgoods@trustcart.ph").orElseGet(() -> {
                Seller seller = new Seller("Local Goods PH", "localgoods@trustcart.ph", "09170000002", "Local Filipino MSME", "Locally Sourced", 93, SellerStatus.APPROVED);
                seller.setPassword("trust123");
                setLocation(seller, "Local Goods Hub, San Pablo City", "San Pablo City", "Laguna", 14.0683, 121.3256, 6, true);
                seller.setEcoCommitment("Local sourcing and recyclable packaging");
                seller.markVerifiedDefaults();
                return sellerRepository.save(seller);
            });

            Seller ecoHome = sellerRepository.findByEmailIgnoreCase("ecohome@trustcart.ph").orElseGet(() -> {
                Seller seller = new Seller("EcoHome Essentials", "ecohome@trustcart.ph", "09170000003", "Sustainable home products", "Low-Waste Packaging", 91, SellerStatus.APPROVED);
                seller.setPassword("trust123");
                setLocation(seller, "EcoHome Laguna Sorting Hub, Calamba", "Calamba", "Laguna", 14.2117, 121.1653, 10, true);
                seller.setEcoCommitment("Low-waste products and eco-packaging");
                seller.markVerifiedDefaults();
                return sellerRepository.save(seller);
            });

            saveDiscountIfMissing(discountCodeRepository, "WELCOME10", "10% off first protected order for first-time buyers.", BigDecimal.valueOf(0), 10, BigDecimal.ZERO, localGoods);
            saveDiscountIfMissing(discountCodeRepository, "GREEN5", "5% off for green checkout buyers.", BigDecimal.valueOf(500), 5, BigDecimal.ZERO, ecoHome);
            saveDiscountIfMissing(discountCodeRepository, "LOCAL50", "₱50 off selected local Filipino products.", BigDecimal.valueOf(300), 0, BigDecimal.valueOf(50), localGoods);

            List<SeedProduct> products = List.of(
                    new SeedProduct(greenTech, "Wireless Earbuds", "Clear audio earbuds from an approved seller. Includes authenticity and warranty check.", ProductCategory.ELECTRONICS, 899, 100, true, "Plastic-Free Packaging", 94),
                    new SeedProduct(greenTech, "Power Bank 20000mAh", "High-capacity power bank with safety-tested battery cells.", ProductCategory.ELECTRONICS, 1299, 80, true, "Verified Tech Item", 92),
                    new SeedProduct(greenTech, "Bluetooth Speaker", "Portable speaker with clear bass and verified buyer feedback.", ProductCategory.ELECTRONICS, 749, 120, false, "Verified Seller", 90),
                    new SeedProduct(greenTech, "Fast Charger Type-C", "Fast charging adapter with Type-C compatibility.", ProductCategory.MOBILE_ACCESSORIES, 399, 150, true, "Minimal Packaging", 91),
                    new SeedProduct(greenTech, "Shockproof Phone Case", "Durable phone case with anti-scratch protection.", ProductCategory.MOBILE_ACCESSORIES, 199, 200, true, "Recyclable Packaging", 89),
                    new SeedProduct(greenTech, "Tempered Glass", "Clear tempered glass screen protector.", ProductCategory.MOBILE_ACCESSORIES, 149, 250, false, "Verified Accessory", 88),
                    new SeedProduct(localGoods, "Organic Cotton Shirt", "Soft cotton shirt from a local approved seller.", ProductCategory.FASHION, 349, 95, true, "Organic Cotton", 93),
                    new SeedProduct(localGoods, "Denim Pants", "Comfort fit denim pants with verified sizing guide.", ProductCategory.FASHION, 799, 60, false, "Trusted Local Seller", 89),
                    new SeedProduct(localGoods, "Eco Canvas Tote Bag", "Reusable tote bag for sustainable shopping.", ProductCategory.FASHION, 299, 130, true, "Reusable Product", 96),
                    new SeedProduct(localGoods, "Bamboo Toothbrush Set", "Eco-friendly toothbrush set for daily use.", ProductCategory.BEAUTY_PERSONAL_CARE, 129, 180, true, "Bamboo Material", 95),
                    new SeedProduct(localGoods, "Organic Facial Wash", "Gentle facial wash from verified local supplier.", ProductCategory.BEAUTY_PERSONAL_CARE, 249, 90, true, "Natural Ingredients", 90),
                    new SeedProduct(localGoods, "Sunscreen SPF50", "Daily sunscreen with SPF50 protection.", ProductCategory.BEAUTY_PERSONAL_CARE, 399, 85, false, "Verified Product", 88),
                    new SeedProduct(ecoHome, "Reusable Food Container", "Food container for reducing single-use plastic waste.", ProductCategory.HOME_LIVING, 299, 140, true, "Reusable Product", 96),
                    new SeedProduct(greenTech, "LED Desk Lamp", "Energy-saving LED desk lamp for study or work.", ProductCategory.HOME_LIVING, 599, 70, true, "Energy Efficient", 90),
                    new SeedProduct(ecoHome, "Bamboo Organizer", "Bamboo desk organizer for home and office.", ProductCategory.HOME_LIVING, 459, 75, true, "Bamboo Material", 94),
                    new SeedProduct(localGoods, "Brown Rice 5kg", "Locally sourced brown rice pack.", ProductCategory.GROCERIES, 420, 55, true, "Locally Sourced", 92),
                    new SeedProduct(localGoods, "Organic Coffee Beans", "Locally roasted organic coffee beans.", ProductCategory.GROCERIES, 350, 65, true, "Local Farmer Support", 95),
                    new SeedProduct(localGoods, "Muscovado Sugar 1kg", "Unrefined muscovado sugar from local suppliers.", ProductCategory.GROCERIES, 180, 100, true, "Locally Sourced", 91),
                    new SeedProduct(greenTech, "Digital Thermometer", "Basic digital thermometer for home use.", ProductCategory.HEALTH_WELLNESS, 299, 110, false, "Verified Seller", 89),
                    new SeedProduct(ecoHome, "Resistance Band Set", "Exercise resistance bands for home fitness.", ProductCategory.HEALTH_WELLNESS, 399, 90, true, "Minimal Packaging", 90),
                    new SeedProduct(ecoHome, "Reusable Water Bottle 1L", "BPA-free water bottle for daily hydration.", ProductCategory.HEALTH_WELLNESS, 249, 140, true, "Reusable Product", 96),
                    new SeedProduct(ecoHome, "Baby Wipes Eco Pack", "Baby wipes in eco-conscious packaging.", ProductCategory.BABY_KIDS, 189, 85, true, "Eco Pack", 90),
                    new SeedProduct(localGoods, "Educational Puzzle Toy", "Learning puzzle toy for kids.", ProductCategory.BABY_KIDS, 299, 60, false, "Verified Toy", 88),
                    new SeedProduct(localGoods, "Kids Cotton Shirt", "Comfortable cotton shirt for children.", ProductCategory.BABY_KIDS, 249, 70, true, "Cotton Fabric", 90),
                    new SeedProduct(ecoHome, "Yoga Mat", "Comfortable yoga mat for workout sessions.", ProductCategory.SPORTS_OUTDOORS, 499, 60, true, "Durable Product", 89),
                    new SeedProduct(ecoHome, "Jump Rope", "Basic jump rope for cardio training.", ProductCategory.SPORTS_OUTDOORS, 149, 150, false, "Verified Product", 87),
                    new SeedProduct(localGoods, "Sports Towel", "Quick-dry sports towel.", ProductCategory.SPORTS_OUTDOORS, 199, 100, true, "Reusable Product", 90),
                    new SeedProduct(localGoods, "Recycled Notebook", "Notebook made with recycled paper.", ProductCategory.SCHOOL_OFFICE, 89, 250, true, "Recycled Paper", 97),
                    new SeedProduct(localGoods, "Ballpen Set", "Affordable ballpen set for school and office.", ProductCategory.SCHOOL_OFFICE, 99, 300, false, "Verified Seller", 88),
                    new SeedProduct(greenTech, "Desk Calculator", "Basic calculator for school and office use.", ProductCategory.SCHOOL_OFFICE, 249, 100, false, "Verified Tech Item", 89),
                    new SeedProduct(greenTech, "Helmet Cleaner", "Cleaning spray for motorcycle helmets.", ProductCategory.AUTOMOTIVE_MOTORCYCLE, 199, 90, false, "Verified Product", 88),
                    new SeedProduct(greenTech, "Motorcycle Phone Holder", "Sturdy holder for motorcycle navigation.", ProductCategory.AUTOMOTIVE_MOTORCYCLE, 349, 70, false, "Verified Accessory", 89),
                    new SeedProduct(greenTech, "Tire Pressure Gauge", "Compact tire pressure gauge.", ProductCategory.AUTOMOTIVE_MOTORCYCLE, 299, 80, false, "Verified Tool", 90),
                    new SeedProduct(ecoHome, "Organic Pet Shampoo", "Gentle shampoo for pets.", ProductCategory.PET_SUPPLIES, 299, 75, true, "Natural Ingredients", 91),
                    new SeedProduct(localGoods, "Cat Litter 5L", "Absorbent cat litter pack.", ProductCategory.PET_SUPPLIES, 249, 90, false, "Verified Supplier", 88),
                    new SeedProduct(localGoods, "Dog Chew Toy", "Durable dog chew toy.", ProductCategory.PET_SUPPLIES, 199, 120, false, "Verified Product", 87),
                    new SeedProduct(ecoHome, "Metal Straw Set", "Reusable metal straw set with pouch.", ProductCategory.SUSTAINABLE_PRODUCTS, 99, 200, true, "Reusable Product", 98),
                    new SeedProduct(ecoHome, "Reusable Shopping Bag", "Foldable shopping bag for groceries.", ProductCategory.SUSTAINABLE_PRODUCTS, 149, 180, true, "Reusable Product", 97),
                    new SeedProduct(ecoHome, "Compostable Trash Bags", "Compostable trash bags for home use.", ProductCategory.SUSTAINABLE_PRODUCTS, 189, 130, true, "Compostable Material", 95),
                    new SeedProduct(localGoods, "Handwoven Pouch", "Locally handwoven pouch by Filipino makers.", ProductCategory.LOCAL_FILIPINO_PRODUCTS, 250, 80, true, "Locally Sourced", 96),
                    new SeedProduct(localGoods, "Local Tablea Chocolate", "Traditional Filipino tablea chocolate.", ProductCategory.LOCAL_FILIPINO_PRODUCTS, 180, 100, true, "Local Farmer Support", 94),
                    new SeedProduct(localGoods, "Abaca Storage Basket", "Abaca basket made by local artisans.", ProductCategory.LOCAL_FILIPINO_PRODUCTS, 499, 55, true, "Abaca Material", 97)
            );
            for (SeedProduct seed : products) {
                addIfMissing(productRepository, seed);
            }
        };
    }

    private record SeedProduct(Seller seller, String name, String description, ProductCategory category, int price, int stock, boolean eco, String tag, int trust) {}

    private void saveDiscountIfMissing(DiscountCodeRepository repository, String code, String description, BigDecimal minimum, int percent, BigDecimal amount, Seller seller) {
        repository.findByCodeIgnoreCase(code).orElseGet(() -> {
            DiscountCode discount = new DiscountCode(code, description, minimum, percent, amount, true);
            if ("WELCOME10".equalsIgnoreCase(code)) {
                discount.setFirstOrderOnly(true);
            }
            discount.setSellerId(seller.getId());
            discount.setCreatedBySeller(seller.getStoreName());
            return repository.save(discount);
        });
    }

    private void setLocation(Seller seller, String exactAddress, String city, String province, double lat, double lng, int radius, boolean pickup) {
        seller.setStoreExactAddress(exactAddress);
        seller.setStoreCity(city);
        seller.setStoreProvince(province);
        seller.setLatitude(lat);
        seller.setLongitude(lng);
        seller.setServiceRadiusKm(radius);
        seller.setPickupAvailable(pickup);
        seller.setLocationProofUrl("Verified map pin proof");
        seller.setStoreLocationVerified(true);
    }

    private void addIfMissing(ProductRepository productRepository, SeedProduct seed) {
        if (productRepository.findByNameIgnoreCase(seed.name()).isPresent()) return;
        Product product = new Product(
                seed.name(), seed.description(), seed.category(), BigDecimal.valueOf(seed.price()), seed.stock(),
                seed.eco(), seed.tag(), seed.trust(),
                "Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.",
                imageFor(seed.name()), ProductStatus.APPROVED, seed.seller()
        );
        product.setTrustCartShield(true);
        product.setAuthenticItemChecked(true);
        product.setVerifiedReviewsOnly(true);
        product.setSuspiciousReviewFlag(false);
        product.setPlasticFreePackaging(seed.eco());
        product.setLocallySourced(seed.category() == ProductCategory.LOCAL_FILIPINO_PRODUCTS || seed.tag().toLowerCase().contains("local"));
        product.setLowWasteDelivery(seed.eco());
        product.setSellerVerificationScore(25);
        product.setProductAuthenticityScore(seed.eco() ? 24 : 23);
        product.setReviewQualityScore(23);
        product.setDeliveryReliabilityScore(18);
        product.setSustainabilityScore(seed.eco() ? 10 : 6);
        product.setTrustScore(Math.min(100, seed.trust()));
        product.setGreenScore(seed.eco() ? Math.min(100, seed.trust() + 1) : 70);
        product.setReturnRiskScore(94);
        product.setRedFlagSummary("No fake-review pattern detected. Checkout is protected inside TrustCart.");
        product.setProductOrigin(seed.category() == ProductCategory.LOCAL_FILIPINO_PRODUCTS ? "Philippines / Local MSME source" : "Verified approved source");
        product.setWarrantyPolicy("7-day buyer protection with digital refund request tracking.");
        product.setPhotoAltText(seed.name() + " product photo");
        product.setSubscriptionEligible(isAutoshipCategory(seed.category()));
        product.setSubscriptionDiscountPercent(isAutoshipCategory(seed.category()) ? 5 : 0);
        productRepository.save(product);
    }

    private boolean isAutoshipCategory(ProductCategory category) {
        return category == ProductCategory.GROCERIES || category == ProductCategory.BEAUTY_PERSONAL_CARE ||
               category == ProductCategory.HEALTH_WELLNESS || category == ProductCategory.PET_SUPPLIES ||
               category == ProductCategory.HOME_LIVING || category == ProductCategory.SUSTAINABLE_PRODUCTS;
    }

    private String imageFor(String name) {
        String key = name.toLowerCase();
        if (key.contains("earbuds")) return "https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?auto=format&fit=crop&w=900&q=80";
        if (key.contains("power bank")) return "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=900&q=80";
        if (key.contains("speaker")) return "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=80";
        if (key.contains("charger")) return "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?auto=format&fit=crop&w=900&q=80";
        if (key.contains("phone case")) return "https://images.unsplash.com/photo-1601593346740-925612772716?auto=format&fit=crop&w=900&q=80";
        if (key.contains("tempered")) return "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80";
        if (key.contains("shirt")) return "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80";
        if (key.contains("denim")) return "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80";
        if (key.contains("tote")) return "https://images.unsplash.com/photo-1597484662317-9bd7bdda2907?auto=format&fit=crop&w=900&q=80";
        if (key.contains("toothbrush")) return "https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?auto=format&fit=crop&w=900&q=80";
        if (key.contains("facial")) return "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=900&q=80";
        if (key.contains("sunscreen")) return "https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&w=900&q=80";
        if (key.contains("container")) return "https://images.unsplash.com/photo-1584346133934-a3afd2a33c4c?auto=format&fit=crop&w=900&q=80";
        if (key.contains("lamp")) return "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80";
        if (key.contains("organizer")) return "https://images.unsplash.com/photo-1618220179428-22790b461013?auto=format&fit=crop&w=900&q=80";
        if (key.contains("rice")) return "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=80";
        if (key.contains("coffee")) return "https://images.unsplash.com/photo-1447933601403-0c6688de566e?auto=format&fit=crop&w=900&q=80";
        if (key.contains("sugar")) return "https://images.unsplash.com/photo-1587486937303-32eaa2134b78?auto=format&fit=crop&w=900&q=80";
        if (key.contains("thermometer")) return "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=900&q=80";
        if (key.contains("resistance")) return "https://images.unsplash.com/photo-1599058917212-d750089bc07e?auto=format&fit=crop&w=900&q=80";
        if (key.contains("bottle")) return "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=80";
        if (key.contains("baby wipes")) return "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&w=900&q=80";
        if (key.contains("puzzle")) return "https://images.unsplash.com/photo-1587654780291-39c9404d746b?auto=format&fit=crop&w=900&q=80";
        if (key.contains("yoga")) return "https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=900&q=80";
        if (key.contains("jump rope")) return "https://images.unsplash.com/photo-1605296867304-46d5465a13f1?auto=format&fit=crop&w=900&q=80";
        if (key.contains("notebook")) return "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=900&q=80";
        if (key.contains("ballpen")) return "https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?auto=format&fit=crop&w=900&q=80";
        if (key.contains("calculator")) return "https://images.unsplash.com/photo-1587145820266-a5951ee6f620?auto=format&fit=crop&w=900&q=80";
        if (key.contains("helmet")) return "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=900&q=80";
        if (key.contains("phone holder")) return "https://images.unsplash.com/photo-1605514449459-5a9cfa0b9955?auto=format&fit=crop&w=900&q=80";
        if (key.contains("tire")) return "https://images.unsplash.com/photo-1607860108855-64acf2078ed9?auto=format&fit=crop&w=900&q=80";
        if (key.contains("pet shampoo")) return "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=900&q=80";
        if (key.contains("cat litter")) return "https://images.unsplash.com/photo-1574144611937-0df059b5ef3e?auto=format&fit=crop&w=900&q=80";
        if (key.contains("dog")) return "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&w=900&q=80";
        if (key.contains("straw")) return "https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?auto=format&fit=crop&w=900&q=80";
        if (key.contains("shopping bag")) return "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=80";
        if (key.contains("trash")) return "https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?auto=format&fit=crop&w=900&q=80";
        if (key.contains("pouch")) return "https://images.unsplash.com/photo-1516762689617-e1cffcef479d?auto=format&fit=crop&w=900&q=80";
        if (key.contains("tablea") || key.contains("chocolate")) return "https://images.unsplash.com/photo-1606312619070-d48b4c652a52?auto=format&fit=crop&w=900&q=80";
        if (key.contains("abaca") || key.contains("basket")) return "https://images.unsplash.com/photo-1603204077779-bed963ea7d0e?auto=format&fit=crop&w=900&q=80";
        return "https://images.unsplash.com/photo-1516321497487-e288fb19713f?auto=format&fit=crop&w=900&q=80";
    }
}
