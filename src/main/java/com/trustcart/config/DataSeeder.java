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

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(SellerRepository sellerRepository, ProductRepository productRepository, BuyerAccountRepository buyerRepository, DiscountCodeRepository discountCodeRepository) {
        return args -> {
            if (sellerRepository.count() > 0 || productRepository.count() > 0) {
                return;
            }

            BuyerAccount seedBuyer = new BuyerAccount("Juan Trust", "buyer@trustcart.ph", "09179990000", "trust123", "San Pablo City", 14.0683, 121.3256, 5);
            seedBuyer.setDefaultAddress("San Pablo City, Laguna");
            seedBuyer.setLoyaltyPointsBalance(350);
            seedBuyer.setLifetimeLoyaltyPoints(350);
            buyerRepository.save(seedBuyer);

            discountCodeRepository.save(new DiscountCode("WELCOME100", "₱100 off for first TrustCart orders with ₱800 minimum spend.", BigDecimal.valueOf(800), 0, BigDecimal.valueOf(100), true));
            discountCodeRepository.save(new DiscountCode("GREEN5", "5% off when buyers choose green checkout options.", BigDecimal.valueOf(500), 5, BigDecimal.ZERO, true));
            discountCodeRepository.save(new DiscountCode("LOCAL50", "₱50 off selected local Filipino products.", BigDecimal.valueOf(300), 0, BigDecimal.valueOf(50), true));

            Seller greenTech = sellerRepository.save(new Seller(
                    "GreenTech Manila", "greentech@trustcart.ph", "09170000001",
                    "Verified electronics reseller", "Plastic-Free Packaging", 95, SellerStatus.APPROVED));
            Seller localGoods = sellerRepository.save(new Seller(
                    "Local Goods PH", "localgoods@trustcart.ph", "09170000002",
                    "Local Filipino MSME", "Locally Sourced", 93, SellerStatus.APPROVED));
            Seller ecoHome = sellerRepository.save(new Seller(
                    "EcoHome Essentials", "ecohome@trustcart.ph", "09170000003",
                    "Sustainable home products", "Low-Waste Packaging", 91, SellerStatus.APPROVED));
            Seller pendingSeller = sellerRepository.save(new Seller(
                    "Pending Verification Seller", "pending@trustcart.ph", "09170000004",
                    "Applicant", "For Review", 0, SellerStatus.PENDING));

            greenTech.setPassword("trust123");
            localGoods.setPassword("trust123");
            ecoHome.setPassword("trust123");
            pendingSeller.setPassword("trust123");

            setLocation(greenTech, "GreenTech Manila Fulfillment Office, Sampaloc", "Manila", "Metro Manila", 14.5995, 120.9842, 8, true);
            setLocation(localGoods, "Local Goods Hub, San Pablo City", "San Pablo City", "Laguna", 14.0683, 121.3256, 6, true);
            setLocation(ecoHome, "EcoHome Laguna Sorting Hub, Calamba", "Calamba", "Laguna", 14.2117, 121.1653, 10, true);
            setLocation(pendingSeller, "Pending Store Location", "Lipa City", "Batangas", 13.9411, 121.1631, 5, false);
            sellerRepository.save(greenTech);
            sellerRepository.save(localGoods);
            sellerRepository.save(ecoHome);
            sellerRepository.save(pendingSeller);

            add(productRepository, greenTech, "Wireless Earbuds", "Clear audio earbuds from an approved seller. Includes authenticity and warranty check.", ProductCategory.ELECTRONICS, 899, 100, true, "Plastic-Free Packaging", 94);
            add(productRepository, greenTech, "Power Bank 20000mAh", "High-capacity power bank with safety-tested battery cells.", ProductCategory.ELECTRONICS, 1299, 80, true, "Verified Tech Item", 92);
            add(productRepository, greenTech, "Bluetooth Speaker", "Portable speaker with clear bass and verified buyer feedback.", ProductCategory.ELECTRONICS, 749, 120, false, "Verified Seller", 90);

            add(productRepository, greenTech, "Fast Charger Type-C", "Fast charging adapter with Type-C compatibility.", ProductCategory.MOBILE_ACCESSORIES, 399, 150, true, "Minimal Packaging", 91);
            add(productRepository, greenTech, "Shockproof Phone Case", "Durable phone case with anti-scratch protection.", ProductCategory.MOBILE_ACCESSORIES, 199, 200, true, "Recyclable Packaging", 89);
            add(productRepository, greenTech, "Tempered Glass", "Clear tempered glass screen protector.", ProductCategory.MOBILE_ACCESSORIES, 149, 250, false, "Verified Accessory", 88);

            add(productRepository, localGoods, "Organic Cotton Shirt", "Soft cotton shirt from a local approved seller.", ProductCategory.FASHION, 349, 95, true, "Organic Cotton", 93);
            add(productRepository, localGoods, "Denim Pants", "Comfort fit denim pants with verified sizing guide.", ProductCategory.FASHION, 799, 60, false, "Trusted Local Seller", 89);
            add(productRepository, localGoods, "Eco Canvas Tote Bag", "Reusable tote bag for sustainable shopping.", ProductCategory.FASHION, 299, 130, true, "Reusable Product", 96);

            add(productRepository, localGoods, "Bamboo Toothbrush Set", "Eco-friendly toothbrush set for daily use.", ProductCategory.BEAUTY_PERSONAL_CARE, 129, 180, true, "Bamboo Material", 95);
            add(productRepository, localGoods, "Organic Facial Wash", "Gentle facial wash from verified local supplier.", ProductCategory.BEAUTY_PERSONAL_CARE, 249, 90, true, "Natural Ingredients", 90);
            add(productRepository, localGoods, "Sunscreen SPF50", "Daily sunscreen with SPF50 protection.", ProductCategory.BEAUTY_PERSONAL_CARE, 399, 85, false, "Verified Product", 88);

            add(productRepository, ecoHome, "Reusable Food Container", "Food container for reducing single-use plastic waste.", ProductCategory.HOME_LIVING, 299, 140, true, "Reusable Product", 96);
            add(productRepository, greenTech, "LED Desk Lamp", "Energy-saving LED desk lamp for study or work.", ProductCategory.HOME_LIVING, 599, 70, true, "Energy Efficient", 90);
            add(productRepository, ecoHome, "Bamboo Organizer", "Bamboo desk organizer for home and office.", ProductCategory.HOME_LIVING, 459, 75, true, "Bamboo Material", 94);

            add(productRepository, localGoods, "Brown Rice 5kg", "Locally sourced brown rice pack.", ProductCategory.GROCERIES, 420, 55, true, "Locally Sourced", 92);
            add(productRepository, localGoods, "Organic Coffee Beans", "Locally roasted organic coffee beans.", ProductCategory.GROCERIES, 350, 65, true, "Local Farmer Support", 95);
            add(productRepository, localGoods, "Muscovado Sugar 1kg", "Unrefined muscovado sugar from local suppliers.", ProductCategory.GROCERIES, 180, 100, true, "Locally Sourced", 91);

            add(productRepository, greenTech, "Digital Thermometer", "Basic digital thermometer for home use.", ProductCategory.HEALTH_WELLNESS, 299, 110, false, "Verified Seller", 89);
            add(productRepository, ecoHome, "Resistance Band Set", "Exercise resistance bands for home fitness.", ProductCategory.HEALTH_WELLNESS, 399, 90, true, "Minimal Packaging", 90);
            add(productRepository, ecoHome, "Reusable Water Bottle 1L", "BPA-free water bottle for daily hydration.", ProductCategory.HEALTH_WELLNESS, 249, 140, true, "Reusable Product", 96);

            add(productRepository, ecoHome, "Baby Wipes Eco Pack", "Baby wipes in eco-conscious packaging.", ProductCategory.BABY_KIDS, 189, 85, true, "Eco Pack", 90);
            add(productRepository, localGoods, "Educational Puzzle Toy", "Learning puzzle toy for kids.", ProductCategory.BABY_KIDS, 299, 60, false, "Verified Toy", 88);
            add(productRepository, localGoods, "Kids Cotton Shirt", "Comfortable cotton shirt for children.", ProductCategory.BABY_KIDS, 249, 70, true, "Cotton Fabric", 90);

            add(productRepository, ecoHome, "Yoga Mat", "Comfortable yoga mat for workout sessions.", ProductCategory.SPORTS_OUTDOORS, 499, 60, true, "Durable Product", 89);
            add(productRepository, ecoHome, "Jump Rope", "Basic jump rope for cardio training.", ProductCategory.SPORTS_OUTDOORS, 149, 150, false, "Verified Product", 87);
            add(productRepository, localGoods, "Sports Towel", "Quick-dry sports towel.", ProductCategory.SPORTS_OUTDOORS, 199, 100, true, "Reusable Product", 90);

            add(productRepository, localGoods, "Recycled Notebook", "Notebook made with recycled paper.", ProductCategory.SCHOOL_OFFICE, 89, 250, true, "Recycled Paper", 97);
            add(productRepository, localGoods, "Ballpen Set", "Affordable ballpen set for school and office.", ProductCategory.SCHOOL_OFFICE, 99, 300, false, "Verified Seller", 88);
            add(productRepository, greenTech, "Desk Calculator", "Basic calculator for school and office use.", ProductCategory.SCHOOL_OFFICE, 249, 100, false, "Verified Tech Item", 89);

            add(productRepository, greenTech, "Helmet Cleaner", "Cleaning spray for motorcycle helmets.", ProductCategory.AUTOMOTIVE_MOTORCYCLE, 199, 90, false, "Verified Product", 88);
            add(productRepository, greenTech, "Motorcycle Phone Holder", "Sturdy holder for motorcycle navigation.", ProductCategory.AUTOMOTIVE_MOTORCYCLE, 349, 70, false, "Verified Accessory", 89);
            add(productRepository, greenTech, "Tire Pressure Gauge", "Compact tire pressure gauge.", ProductCategory.AUTOMOTIVE_MOTORCYCLE, 299, 80, false, "Verified Tool", 90);

            add(productRepository, ecoHome, "Organic Pet Shampoo", "Gentle shampoo for pets.", ProductCategory.PET_SUPPLIES, 299, 75, true, "Natural Ingredients", 91);
            add(productRepository, localGoods, "Cat Litter 5L", "Absorbent cat litter pack.", ProductCategory.PET_SUPPLIES, 249, 90, false, "Verified Supplier", 88);
            add(productRepository, localGoods, "Dog Chew Toy", "Durable dog chew toy.", ProductCategory.PET_SUPPLIES, 199, 120, false, "Verified Product", 87);

            add(productRepository, ecoHome, "Metal Straw Set", "Reusable metal straw set with pouch.", ProductCategory.SUSTAINABLE_PRODUCTS, 99, 200, true, "Reusable Product", 98);
            add(productRepository, ecoHome, "Reusable Shopping Bag", "Foldable shopping bag for groceries.", ProductCategory.SUSTAINABLE_PRODUCTS, 149, 180, true, "Reusable Product", 97);
            add(productRepository, ecoHome, "Compostable Trash Bags", "Compostable trash bags for home use.", ProductCategory.SUSTAINABLE_PRODUCTS, 189, 130, true, "Compostable Material", 95);

            add(productRepository, localGoods, "Handwoven Pouch", "Locally handwoven pouch by Filipino makers.", ProductCategory.LOCAL_FILIPINO_PRODUCTS, 250, 80, true, "Locally Sourced", 96);
            add(productRepository, localGoods, "Local Tablea Chocolate", "Traditional Filipino tablea chocolate.", ProductCategory.LOCAL_FILIPINO_PRODUCTS, 180, 100, true, "Local Farmer Support", 94);
            add(productRepository, localGoods, "Abaca Storage Basket", "Abaca basket made by local artisans.", ProductCategory.LOCAL_FILIPINO_PRODUCTS, 499, 55, true, "Abaca Material", 97);
        };
    }

    private void setLocation(Seller seller, String exactAddress, String city, String province, double lat, double lng, int radiusKm, boolean pickupAvailable) {
        seller.setStoreExactAddress(exactAddress);
        seller.setStoreCity(city);
        seller.setStoreProvince(province);
        seller.setLatitude(lat);
        seller.setLongitude(lng);
        seller.setServiceRadiusKm(radiusKm);
        seller.setPickupAvailable(pickupAvailable);
        seller.setLocationProofUrl("Verified location proof uploaded");
        if (seller.getStatus() == SellerStatus.APPROVED) {
            seller.setStoreLocationVerified(true);
        }
    }

    private void add(ProductRepository productRepository, Seller seller, String name, String description,
                     ProductCategory category, int price, int stock, boolean ecoFriendly,
                     String sustainabilityTag, int trustScore) {
        Product product = new Product(
                name,
                description,
                category,
                BigDecimal.valueOf(price),
                stock,
                ecoFriendly,
                sustainabilityTag,
                trustScore,
                "Verified buyers report clear product quality, reliable seller response, and no major red flags.",
                productPhotoUrl(name, category),
                ProductStatus.APPROVED,
                seller
        );
        boolean local = sustainabilityTag.toLowerCase().contains("local") || category == ProductCategory.LOCAL_FILIPINO_PRODUCTS;
        boolean plasticFree = sustainabilityTag.toLowerCase().contains("plastic") || sustainabilityTag.toLowerCase().contains("bamboo") || sustainabilityTag.toLowerCase().contains("reusable") || sustainabilityTag.toLowerCase().contains("abaca");
        product.setTrustCartShield(true);
        product.setAuthenticItemChecked(true);
        product.setVerifiedReviewsOnly(true);
        product.setSuspiciousReviewFlag(false);
        product.setPlasticFreePackaging(plasticFree || ecoFriendly);
        product.setLocallySourced(local);
        product.setLowWasteDelivery(ecoFriendly);
        product.setGreenScore(ecoFriendly ? Math.min(100, trustScore + 1) : 68);
        product.setSellerVerificationScore(25);
        product.setProductAuthenticityScore(24);
        product.setReviewQualityScore(Math.min(25, Math.max(20, trustScore - 70)));
        product.setDeliveryReliabilityScore(Math.min(20, Math.max(17, trustScore - 75)));
        product.setSustainabilityScore(ecoFriendly ? 10 : 6);
        product.setReturnRiskScore(Math.min(100, Math.max(88, trustScore)));
        product.setRedFlagSummary("No fake-review pattern or authenticity issue detected in this listing.");
        product.setProductOrigin(local ? "Philippines / Local MSME source" : "Verified approved source");
        product.setWarrantyPolicy("7-day buyer protection with digital refund request tracking.");
        boolean autoshipEligible = category == ProductCategory.GROCERIES || category == ProductCategory.BEAUTY_PERSONAL_CARE || category == ProductCategory.HOME_LIVING || category == ProductCategory.PET_SUPPLIES || category == ProductCategory.HEALTH_WELLNESS || category == ProductCategory.SUSTAINABLE_PRODUCTS;
        product.setSubscriptionEligible(autoshipEligible);
        product.setSubscriptionDiscountPercent(autoshipEligible ? 5 : 0);
        product.setPhotoAltText(name + " product photo");
        product.applyDefaultTrustBreakdown();
        productRepository.save(product);
    }
    private String productPhotoUrl(String name, ProductCategory category) {
        String key = name.toLowerCase();
        if (key.contains("earbuds")) return "https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?auto=format&fit=crop&w=900&q=80";
        if (key.contains("power bank")) return "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=900&q=80";
        if (key.contains("speaker")) return "https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=900&q=80";
        if (key.contains("charger")) return "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?auto=format&fit=crop&w=900&q=80";
        if (key.contains("phone case")) return "https://images.unsplash.com/photo-1601593346740-925612772716?auto=format&fit=crop&w=900&q=80";
        if (key.contains("tempered")) return "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80";
        if (key.contains("shirt")) return "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80";
        if (key.contains("denim")) return "https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80";
        if (key.contains("tote")) return "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=900&q=80";
        if (key.contains("toothbrush")) return "https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?auto=format&fit=crop&w=900&q=80";
        if (key.contains("facial")) return "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=900&q=80";
        if (key.contains("sunscreen")) return "https://images.unsplash.com/photo-1526947425960-945c6e72858f?auto=format&fit=crop&w=900&q=80";
        if (key.contains("container")) return "https://images.unsplash.com/photo-1584346133934-a3afd2a33c4c?auto=format&fit=crop&w=900&q=80";
        if (key.contains("lamp")) return "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80";
        if (key.contains("organizer")) return "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=900&q=80";
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
        if (key.contains("helmet")) return "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=900&q=80";
        if (key.contains("pet")) return "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=900&q=80";
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
