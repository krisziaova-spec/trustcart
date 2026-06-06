package com.trustcart.config;

import com.trustcart.model.*;
import com.trustcart.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Configuration
public class DataSeeder {
    @Bean
    @Order(2)
    CommandLineRunner seed(BuyerAccountRepository buyers, SellerRepository sellers, ProductRepository products, DiscountCodeRepository discounts, GiftRegistryRepository registries, GiftRegistryItemRepository registryItems) {
        return args -> {
            if (buyers.findByEmailIgnoreCase("buyer@trustcart.ph").isEmpty()) {
                BuyerAccount b = new BuyerAccount();
                b.setFullName("Juan Trust"); b.setEmail("buyer@trustcart.ph"); b.setPassword("trust123"); b.setPhone("09179990000"); b.setDefaultAddress("San Pablo City, Laguna"); b.setPreferredCity("San Pablo City"); b.setPreferredLatitude(14.0683); b.setPreferredLongitude(121.3256); b.setLoyaltyPointsBalance(350); b.setLifetimeLoyaltyPoints(350); buyers.save(b);
            }
            Seller tech = seller(sellers,"GreenTech Manila","greentech@trustcart.ph","Verified electronics reseller","Plastic-Free Packaging","Manila","Metro Manila",14.5995,120.9842);
            Seller local = seller(sellers,"Local Goods PH","localgoods@trustcart.ph","Local Filipino MSME","Locally Sourced","San Pablo City","Laguna",14.0683,121.3256);
            Seller eco = seller(sellers,"EcoHome Essentials","ecohome@trustcart.ph","Sustainable home products","Low-Waste Packaging","Calamba","Laguna",14.2117,121.1653);
            Seller daily = seller(sellers,"Daily Essentials Co.","daily@trustcart.ph","FMCG and household essentials","Autoship Essentials","San Pablo City","Laguna",14.0740,121.3250);
            Seller pantry = seller(sellers,"FreshPack Staples","freshpack@trustcart.ph","Packaged goods and consumer staples","Sealed Packaged Goods","Calamba","Laguna",14.2104,121.1650);
            Seller kitchen = seller(sellers,"Kusina San Pablo","kusina@trustcart.ph","Prepared meals and ready-to-cook local food","Nearby Food Seller","San Pablo City","Laguna",14.0702,121.3268);
            Seller chrys = seller(sellers,"Chrysanthemum Rice Depot","chrysanthemumrice@trustcart.ph","Local rice and grocery store","Verified Local Seller","San Pedro","Laguna",14.3440,121.0576);
            Seller lagunaFarm = seller(sellers,"Laguna Farmers Hub","lagunafarmers@trustcart.ph","Farm staples and rice supplier","Fulfilled by TrustCart Partner","San Pedro","Laguna",14.3475,121.0558); lagunaFarm.setCanUseFbt(true); sellers.save(lagunaFarm);
            Seller southGrain = seller(sellers,"South Grain Trading","southgrain@trustcart.ph","Rice trading store","Verified Local Seller","San Pedro","Laguna",14.3412,121.0610);
            Seller greenBasket = seller(sellers,"Green Basket San Pedro","greenbasketsp@trustcart.ph","Vegetables and pantry goods","Locally Sourced","San Pedro","Laguna",14.3501,121.0585);
            Seller caviteRice = seller(sellers,"Cavite Rice Center","caviterice@trustcart.ph","Rice and consumer staples","Fulfilled by TrustCart Partner","Imus","Cavite",14.3860,120.9368); caviteRice.setCanUseFbt(true); sellers.save(caviteRice);
            Seller malagasang = seller(sellers,"Malagasang Grocery Mart","malagasangmart@trustcart.ph","Community grocery store","Verified Local Seller","Imus","Cavite",14.3884,120.9345);
            Seller goldenHarvest = seller(sellers,"Golden Harvest Store","goldenharvest@trustcart.ph","Rice and dry goods","Verified Local Seller","Imus","Cavite",14.3833,120.9399);
            Seller caviteFresh = seller(sellers,"Cavite Fresh Goods","cavitefresh@trustcart.ph","Fresh vegetables and household goods","Locally Sourced","Imus","Cavite",14.3901,120.9405);
            Seller binanRice = seller(sellers,"Biñan Rice Depot","binanrice@trustcart.ph","Rice depot","Verified Local Seller","Biñan","Laguna",14.3036,121.0781);
            Seller southLuzon = seller(sellers,"South Luzon Grocery","southluzon@trustcart.ph","Mixed grocery and daily needs","Verified Seller","Biñan","Laguna",14.3064,121.0758);
            Seller dasmaFarm = seller(sellers,"Dasma Farmers Market","dasmafarmers@trustcart.ph","Farm goods and staples","Verified Local Seller","Dasmariñas","Cavite",14.3294,120.9367);
            Seller kadiwaDasma = seller(sellers,"Kadiwa Express Dasma","kadiwadasma@trustcart.ph","Rice, eggs, and vegetables","Verified Local Seller","Dasmariñas","Cavite",14.3310,120.9388);
            Seller rosaFresh = seller(sellers,"Santa Rosa Fresh Market","santarosafresh@trustcart.ph","Fresh market and pantry goods","Verified Local Seller","Santa Rosa","Laguna",14.3122,121.1114);
            Seller valleyRice = seller(sellers,"Green Valley Rice Store","greenvalleyrice@trustcart.ph","Rice and grains store","Verified Local Seller","Santa Rosa","Laguna",14.3147,121.1098);
            code(discounts,"WELCOME10","10% off first protected order for first-time buyers.",BigDecimal.ZERO,10,BigDecimal.ZERO,true,local);
            code(discounts,"GREEN5","5% off for green checkout buyers.",BigDecimal.valueOf(500),5,BigDecimal.ZERO,false,eco);
            code(discounts,"LOCAL50","₱50 off selected local Filipino products.",BigDecimal.valueOf(300),0,BigDecimal.valueOf(50),false,local);
            boolean freshProductSeed = products.count() == 0;
            if (freshProductSeed) {
                base(products,"Wireless Earbuds",ProductCategory.ELECTRONICS,899,tech,"https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Power Bank 20000mAh",ProductCategory.ELECTRONICS,1299,tech,"https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Bluetooth Speaker",ProductCategory.ELECTRONICS,749,tech,"https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Fast Charger Type-C",ProductCategory.MOBILE_ACCESSORIES,399,tech,"https://images.unsplash.com/photo-1583863788434-e58a36330cf0?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Shockproof Phone Case",ProductCategory.MOBILE_ACCESSORIES,199,tech,"https://images.unsplash.com/photo-1601593346740-925612772716?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Tempered Glass",ProductCategory.MOBILE_ACCESSORIES,149,tech,"https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Organic Cotton Shirt",ProductCategory.FASHION,349,local,"https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80",false,true);
                base(products,"Denim Pants",ProductCategory.FASHION,799,local,"https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80",false,true);
                base(products,"Eco Canvas Tote Bag",ProductCategory.FASHION,299,local,"https://images.unsplash.com/photo-1597484662317-9bd7bdda2907?auto=format&fit=crop&w=900&q=80",false,true);
                base(products,"Bamboo Toothbrush Set",ProductCategory.BEAUTY_PERSONAL_CARE,129,local,"https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Organic Facial Wash",ProductCategory.BEAUTY_PERSONAL_CARE,249,local,"https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Sunscreen SPF50",ProductCategory.BEAUTY_PERSONAL_CARE,399,local,"https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Reusable Food Container",ProductCategory.HOME_LIVING,299,eco,"https://images.unsplash.com/photo-1584346133934-a3afd2a33c4c?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"LED Desk Lamp",ProductCategory.HOME_LIVING,599,tech,"https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Bamboo Organizer",ProductCategory.HOME_LIVING,459,eco,"https://images.unsplash.com/photo-1618220179428-22790b461013?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Brown Rice 5kg",ProductCategory.GROCERIES,420,local,"https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Organic Coffee Beans",ProductCategory.GROCERIES,350,local,"https://images.unsplash.com/photo-1447933601403-0c6688de566e?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Muscovado Sugar 1kg",ProductCategory.GROCERIES,180,local,"https://images.unsplash.com/photo-1587486937303-32eaa2134b78?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Digital Thermometer",ProductCategory.HEALTH_WELLNESS,299,tech,"https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Resistance Band Set",ProductCategory.HEALTH_WELLNESS,399,eco,"https://images.unsplash.com/photo-1599058917212-d750089bc07e?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Reusable Water Bottle 1L",ProductCategory.HEALTH_WELLNESS,249,eco,"https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Baby Wipes Eco Pack",ProductCategory.BABY_KIDS,189,eco,"https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Educational Puzzle Toy",ProductCategory.BABY_KIDS,299,local,"https://images.unsplash.com/photo-1587654780291-39c9404d746b?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Kids Cotton Shirt",ProductCategory.BABY_KIDS,249,local,"https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Yoga Mat",ProductCategory.SPORTS_OUTDOORS,499,eco,"https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Jump Rope",ProductCategory.SPORTS_OUTDOORS,149,eco,"https://images.unsplash.com/photo-1605296867304-46d5465a13f1?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Sports Towel",ProductCategory.SPORTS_OUTDOORS,199,local,"https://images.unsplash.com/photo-1556197408-904afb6a4666?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Recycled Notebook",ProductCategory.SCHOOL_OFFICE,89,local,"https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Ballpen Set",ProductCategory.SCHOOL_OFFICE,99,local,"https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Desk Calculator",ProductCategory.SCHOOL_OFFICE,249,tech,"https://images.unsplash.com/photo-1587145820266-a5951ee6f620?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Helmet Cleaner",ProductCategory.AUTOMOTIVE_MOTORCYCLE,199,tech,"https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Motorcycle Phone Holder",ProductCategory.AUTOMOTIVE_MOTORCYCLE,349,tech,"https://images.unsplash.com/photo-1605514449459-5a9cfa0b9955?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Tire Pressure Gauge",ProductCategory.AUTOMOTIVE_MOTORCYCLE,299,tech,"https://images.unsplash.com/photo-1607860108855-64acf2078ed9?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Organic Pet Shampoo",ProductCategory.PET_SUPPLIES,299,eco,"https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Cat Litter 5L",ProductCategory.PET_SUPPLIES,249,local,"https://images.unsplash.com/photo-1574144611937-0df059b5ef3e?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Dog Chew Toy",ProductCategory.PET_SUPPLIES,199,local,"https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Metal Straw Set",ProductCategory.SUSTAINABLE_PRODUCTS,99,eco,"https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Reusable Shopping Bag",ProductCategory.SUSTAINABLE_PRODUCTS,149,eco,"https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Compostable Trash Bags",ProductCategory.SUSTAINABLE_PRODUCTS,189,eco,"https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?auto=format&fit=crop&w=900&q=80",true,false);
                base(products,"Handwoven Pouch",ProductCategory.LOCAL_FILIPINO_PRODUCTS,250,local,"https://images.unsplash.com/photo-1516762689617-e1cffcef479d?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Local Tablea Chocolate",ProductCategory.LOCAL_FILIPINO_PRODUCTS,180,local,"https://images.unsplash.com/photo-1606312619070-d48b4c652a52?auto=format&fit=crop&w=900&q=80",false,false);
                base(products,"Abaca Storage Basket",ProductCategory.LOCAL_FILIPINO_PRODUCTS,499,local,"https://images.unsplash.com/photo-1603204077779-bed963ea7d0e?auto=format&fit=crop&w=900&q=80",false,false);
                String[] men={"Men Basic T-Shirt - Green","Men Basic T-Shirt - Navy","Men Basic T-Shirt - White","Men Basic T-Shirt - Black"};
                String[] ma={"/img/tryon/men-tshirt-green.png","/img/tryon/men-tshirt-navy.png","/img/tryon/men-tshirt-white.png","/img/tryon/men-tshirt-black.png"};
                for(int i=0;i<men.length;i++) tryon(products,men[i],"MEN",ma[i],local,389+i*20);
                String[] women={"Women Simple Dress - Sage","Women Simple Dress - Rose","Women Simple Dress - Navy","Women Simple Dress - Cream"};
                String[] wa={"/img/tryon/women-dress-sage.png","/img/tryon/women-dress-rose.png","/img/tryon/women-dress-navy.png","/img/tryon/women-dress-cream.png"};
                for(int i=0;i<women.length;i++) tryon(products,women[i],"WOMEN",wa[i],local,459+i*25);
            }
            seedEverydaySubscriptionGoods(products, daily, pantry, eco);
            seedCommunityEssentialsAndSustainableGoods(products, daily, pantry, eco, local);
            seedPreparedFoodAndMealKits(products, kitchen, local);
            seedProfessionalTryOnItems(products, local);
            seedToneMatchFashion(products, local);
            repairDemoProductRecords(products);
            repairTryOnAndToneMatchRecords(products);
            normalizeEssentialCategories(products);

            seedDemoRiceAndLocalStores(products, chrys, lagunaFarm, southGrain, greenBasket, caviteRice, malagasang, goldenHarvest, caviteFresh, binanRice, southLuzon, dasmaFarm, kadiwaDasma, rosaFresh, valleyRice);

            if (registries.count() == 0) {
                BuyerAccount owner = buyers.findByEmailIgnoreCase("buyer@trustcart.ph").orElse(null);
                if (owner != null) {
                    GiftRegistry baby = new GiftRegistry();
                    baby.setBuyer(owner);
                    baby.setRegistryName("Baby Essentials for Ana");
                    baby.setRegistryType("Baby Shower");
                    baby.setRecipientName("Ana Santos");
                    baby.setRecipientEmail("ana@example.com");
                    baby.setEventDate(LocalDate.now().plusMonths(2));
                    baby.setDeliveryCity("San Pablo City, Laguna");
                    baby.setRegistryNote("Preferred practical gifts: baby care, eco packs, educational toys, and home essentials.");
                    baby.setShareCode("baby-essentials-for-ana");
                    registries.save(baby);
                    addGift(registryItems, baby, products, "Baby Wipes Eco Pack", 3, "Must have", "Eco pack preferred for newborn care.");
                    addGift(registryItems, baby, products, "Educational Puzzle Toy", 1, "Nice to have", "For early learning.");
                    addGift(registryItems, baby, products, "Kids Cotton Shirt", 2, "Must have", "Neutral colors preferred.");
                    addGift(registryItems, baby, products, "Reusable Food Container", 2, "Nice to have", "Useful for baby snacks later.");

                    GiftRegistry wedding = new GiftRegistry();
                    wedding.setBuyer(owner);
                    wedding.setRegistryName("Marco and Ella Newlywed Home List");
                    wedding.setRegistryType("Wedding / Newlywed");
                    wedding.setRecipientName("Marco and Ella");
                    wedding.setEventDate(LocalDate.now().plusMonths(4));
                    wedding.setDeliveryCity("Calamba, Laguna");
                    wedding.setRegistryNote("Preferred home and sustainable essentials for a new household.");
                    wedding.setShareCode("marco-ella-newlywed-home-list");
                    registries.save(wedding);
                    addGift(registryItems, wedding, products, "LED Desk Lamp", 1, "Nice to have", "Energy-saving home office item.");
                    addGift(registryItems, wedding, products, "Bamboo Organizer", 2, "Nice to have", "For home organization.");
                    addGift(registryItems, wedding, products, "Reusable Food Container", 4, "Must have", "Kitchen starter set.");
                    addGift(registryItems, wedding, products, "Abaca Storage Basket", 1, "Optional", "Local Filipino home decor.");
                }
            }

        };
    }
    private Seller seller(SellerRepository repo,String name,String email,String type,String badge,String city,String prov,double lat,double lon){
        return repo.findByEmailIgnoreCase(email).orElseGet(()->{Seller s=new Seller();s.setStoreName(name);s.setEmail(email);s.setPassword("trust123");s.setBusinessType(type);s.setSustainabilityBadge(badge);s.setStoreExactAddress(name+" Fulfillment Hub");s.setStoreCity(city);s.setStoreProvince(prov);s.setLatitude(lat);s.setLongitude(lon);s.setEcoCommitment("Verified commitment to buyer protection and sustainable packaging.");s.setVerificationNote("Business, product, and location verified.");s.setApprovedBy("TrustCart Verification");s.setStoreDescription(type + " serving " + city + " with protected checkout and seller-area visibility only.");s.setStoreProfileImageUrl("https://images.unsplash.com/photo-1556745753-b2904692b3cd?auto=format&fit=crop&w=700&q=80");s.setStoreBannerImageUrl("https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1600&q=80");return repo.save(s);});
    }
    private void code(DiscountCodeRepository repo,String code,String desc,BigDecimal min,int pct,BigDecimal amt,boolean first,Seller seller){
        repo.findByCodeIgnoreCase(code).orElseGet(()->{DiscountCode d=new DiscountCode();d.setCode(code);d.setDescription(desc);d.setMinimumSpend(min);d.setPercentOff(pct);d.setAmountOff(amt);d.setFirstOrderOnly(first);d.setSeller(seller);d.setCreatedBySeller(seller.getStoreName());return repo.save(d);});
    }

    private void seedDemoRiceAndLocalStores(ProductRepository repo, Seller... sellers) {
        String[] riceNames = {"Premium Dinorado Rice 25kg", "Jasmine Rice 10kg", "Well-Milled Rice 25kg", "Brown Rice 5kg", "Sinandomeng Rice 25kg", "Rice and Egg Bundle", "Local Vegetables Basket", "Canned Goods Bundle", "Cooking Oil 2L", "Household Grocery Pack", "Fresh Egg Tray", "Pancit Canton Grocery Pack", "Sugar 1kg Bundle", "TrustCart Pantry Starter"};
        int[] prices = {1450, 720, 1280, 420, 1320, 520, 299, 399, 289, 699, 245, 180, 210, 999};
        for (int i = 0; i < sellers.length; i++) {
            Seller seller = sellers[i];
            String productName = seller.getStoreName() + " - " + riceNames[i % riceNames.length];
            baseIfMissing(repo, productName, ProductCategory.GROCERIES, prices[i % prices.length], seller,
                    "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=80",
                    true, false,
                    "Fast-moving product for nearby seller search and community marketplace presentation.",
                    seller.isCanUseFbt() ? "TrustCart hub inventory sample" : "Seller-managed local fulfillment");
            repo.findByNameIgnoreCase(productName).ifPresent(p -> {
                if (seller.isCanUseFbt()) {
                    p.setFulfilledBy("TRUSTCART");
                    p.setFulfillmentStatus("TRUSTCART_APPROVED");
                    p.setTrustCartStock(p.getStock());
                    p.setFulfillmentNote("Inventory verified and stored at a TrustCart partner hub.");
                    p.setEstimatedDelivery("ETA: TrustCart hub delivery");
                    repo.save(p);
                }
            });
        }
    }

    private void seedCommunityEssentialsAndSustainableGoods(ProductRepository repo, Seller daily, Seller pantry, Seller eco, Seller local) {
        baseIfMissing(repo,"Community Hygiene Kit",ProductCategory.GROCERIES,299,daily,"https://images.unsplash.com/photo-1584305574647-0cc949a2bb9f?auto=format&fit=crop&w=900&q=80",true,false,"Barangay-ready hygiene bundle with soap, alcohol, tissue, and basic care items.","Community essentials bundle");
        baseIfMissing(repo,"Rice and Egg Community Bundle",ProductCategory.GROCERIES,549,local,"https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=80",true,false,"Local household food bundle for community pantry and family replenishment.","Local food essentials");
        baseIfMissing(repo,"Sardines 6-Pack",ProductCategory.GROCERIES,189,pantry,"/img/products/canned-tuna.png",true,false,"Shelf-stable canned goods pack for everyday household needs.","Sealed pantry pack");
        baseIfMissing(repo,"Instant Noodles Family Pack",ProductCategory.GROCERIES,165,pantry,"https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?auto=format&fit=crop&w=900&q=80",true,false,"Affordable household food pack for quick meals and emergency pantry use.","Budget pantry staple");
        baseIfMissing(repo,"Local Coffee Sachet Bundle",ProductCategory.GROCERIES,120,pantry,"https://images.unsplash.com/photo-1447933601403-0c6688de566e?auto=format&fit=crop&w=900&q=80",true,false,"Community-friendly coffee bundle for daily household consumption.","Sealed local pantry goods");
        baseIfMissing(repo,"Bath Soap Family Pack",ProductCategory.GROCERIES,145,daily,"https://images.unsplash.com/photo-1607006483224-37fb2fabc3fa?auto=format&fit=crop&w=900&q=80",true,false,"Family-size personal care basic for repeat replenishment.","Everyday hygiene essential");
        baseIfMissing(repo,"Sanitary Pads Value Pack",ProductCategory.GROCERIES,159,daily,"https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=900&q=80",true,false,"Essential care item for household and community support purchases.","Personal care essential");
        baseIfMissing(repo,"Soy Sauce and Vinegar Bundle",ProductCategory.GROCERIES,138,pantry,"https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=900&q=80",true,false,"Basic cooking condiments for Filipino household kitchens.","Kitchen staples bundle");
        baseIfMissing(repo,"Locally Packed Salt and Sugar Set",ProductCategory.GROCERIES,95,local,"https://images.unsplash.com/photo-1587486937303-32eaa2134b78?auto=format&fit=crop&w=900&q=80",true,false,"Affordable local pantry staples for daily cooking needs.","Local pantry essentials");
        baseIfMissing(repo,"Drinking Water Refill Voucher",ProductCategory.GROCERIES,75,daily,"https://images.unsplash.com/photo-1523362628745-0c100150b504?auto=format&fit=crop&w=900&q=80",true,false,"Refill voucher for drinking water access in local communities.","Refill-first household essential");

        baseIfMissing(repo,"Reusable Eco Bag 3-Pack",ProductCategory.SUSTAINABLE_PRODUCTS,179,eco,"https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=80",true,false,"Washable reusable bags for local market and grocery purchases.","Reusable alternative to single-use plastic");
        baseIfMissing(repo,"Bamboo Toothbrush Family Pack",ProductCategory.SUSTAINABLE_PRODUCTS,199,eco,"https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?auto=format&fit=crop&w=900&q=80",true,false,"Family pack of bamboo toothbrushes for low-waste personal care.","Bamboo and low-waste hygiene");
        baseIfMissing(repo,"Refillable Dish Soap Starter Kit",ProductCategory.SUSTAINABLE_PRODUCTS,249,eco,"https://images.unsplash.com/photo-1626806819282-2c1dc01a5e0c?auto=format&fit=crop&w=900&q=80",true,false,"Starter bottle with refill pouch to reduce plastic use.","Refill system for household cleaning");
        baseIfMissing(repo,"Eco Laundry Sheets",ProductCategory.SUSTAINABLE_PRODUCTS,229,eco,"https://images.unsplash.com/photo-1626806819282-2c1dc01a5e0c?auto=format&fit=crop&w=900&q=80",true,false,"Lightweight laundry sheets for lower packaging waste and easier delivery.","Low-waste laundry option");
        baseIfMissing(repo,"Bamboo Utensil Set",ProductCategory.SUSTAINABLE_PRODUCTS,159,local,"https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?auto=format&fit=crop&w=900&q=80",true,false,"Portable reusable utensil set for school, office, and travel.","Reusable dining essential");
        baseIfMissing(repo,"Beeswax Food Wrap Set",ProductCategory.SUSTAINABLE_PRODUCTS,219,eco,"https://images.unsplash.com/photo-1584346133934-a3afd2a33c4c?auto=format&fit=crop&w=900&q=80",true,false,"Reusable food wraps for household food storage.","Plastic-wrap alternative");
        baseIfMissing(repo,"Coconut Coir Scrub Pads",ProductCategory.SUSTAINABLE_PRODUCTS,99,local,"https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?auto=format&fit=crop&w=900&q=80",true,false,"Local coconut-fiber scrub pads for dishes and cleaning.","Locally sourced biodegradable scrub");
        baseIfMissing(repo,"Abaca Market Basket",ProductCategory.SUSTAINABLE_PRODUCTS,499,local,"https://images.unsplash.com/photo-1603204077779-bed963ea7d0e?auto=format&fit=crop&w=900&q=80",false,false,"Locally made abaca basket for market shopping and home storage.","Local Filipino sustainable craft");
        baseIfMissing(repo,"Stainless Tumbler 500ml",ProductCategory.SUSTAINABLE_PRODUCTS,299,eco,"https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=80",true,false,"Reusable tumbler for school, office, and community events.","Reusable bottle alternative");
        baseIfMissing(repo,"Compost Starter Kit",ProductCategory.SUSTAINABLE_PRODUCTS,349,eco,"https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?auto=format&fit=crop&w=900&q=80",false,false,"Starter kit for household composting and waste reduction.","Waste reduction starter kit");
        baseIfMissing(repo,"Solar Study Lamp",ProductCategory.SUSTAINABLE_PRODUCTS,399,eco,"https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80",false,false,"Rechargeable study lamp for energy-saving community use.","Energy-saving household item");
    }

    private void seedPreparedFoodAndMealKits(ProductRepository repo, Seller kitchen, Seller local) {
        preparedFoodIfMissing(repo,"Chicken Adobo Rice Meal",149,kitchen,"https://images.unsplash.com/photo-1562967916-eb82221dfb36?auto=format&fit=crop&w=900&q=80","Ready-to-eat Filipino meal available only from nearby food sellers.");
        preparedFoodIfMissing(repo,"Pancit Bihon Bilao",499,kitchen,"https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?auto=format&fit=crop&w=900&q=80","Freshly prepared local sharing tray for same-day nearby delivery.");
        preparedFoodIfMissing(repo,"Lumpiang Shanghai Party Tray",389,kitchen,"https://images.unsplash.com/photo-1567620832903-9fc6debc209f?auto=format&fit=crop&w=900&q=80","Ready-to-eat party tray available within close delivery range.");
        preparedFoodIfMissing(repo,"Ready-to-Cook Marinated Chicken",259,kitchen,"https://images.unsplash.com/photo-1604503468506-a8da13d82791?auto=format&fit=crop&w=900&q=80","Ready-to-cook local food pack for nearby household orders.");
        preparedFoodIfMissing(repo,"Sinigang Meal Kit",299,kitchen,"https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=900&q=80","Ready-to-cook soup kit with local ingredients for same-day fulfillment.");
        preparedFoodIfMissing(repo,"Local Kakanin Box",199,local,"https://images.unsplash.com/photo-1606312619070-d48b4c652a52?auto=format&fit=crop&w=900&q=80","Community-made Filipino snacks available through nearby sellers.");
    }

    private void preparedFoodIfMissing(ProductRepository repo, String name, int price, Seller seller, String image, String description) {
        if (repo.findByNameIgnoreCase(name).isPresent()) return;
        Product p = new Product();
        p.setName(name);
        p.setDescription(description + " Food listings are shown only within a close local range for quality and consumption safety.");
        p.setCategory(ProductCategory.PREPARED_FOODS);
        p.setPrice(BigDecimal.valueOf(price));
        p.setStock(35);
        p.setEcoFriendly(true);
        p.setSustainabilityTag("Nearby-only prepared food");
        p.setTrustScore(94);
        p.setGreenScore(90);
        p.setImageUrl(image);
        p.setProductOrigin(seller.getPublicLocationLabel());
        p.setWarrantyPolicy("Same-day protected order handling. Food availability depends on local seller range and preparation time.");
        p.setSubscriptionEligible(false);
        p.setPhotoAltText(name + " local prepared food photo");
        p.setStockStatus("Prepared today");
        p.setEstimatedDelivery("Nearby only · Same-day");
        p.setReviewSummary("Verified local food seller. Prepared-food visibility is restricted to nearby buyers.");
        p.setRedFlagSummary("Close-range purchase only to reduce spoilage and preserve food quality.");
        p.setSeller(seller);
        repo.save(p);
    }

    private void seedProfessionalTryOnItems(ProductRepository repo, Seller seller) {
        tryonIfMissing(repo,"Men Realistic T-Shirt Try-On","MEN","/img/tryon/men-realistic-tshirt.png",seller,399);
        tryonIfMissing(repo,"Women Realistic Dress Try-On","WOMEN","/img/tryon/women-realistic-dress.png",seller,499);
    }

    private void tryonIfMissing(ProductRepository repo, String name, String gender, String asset, Seller seller, int price) {
        if (repo.findByNameIgnoreCase(name).isPresent()) return;
        tryon(repo, name, gender, asset, seller, price);
    }

    private void seedToneMatchFashion(ProductRepository repo, Seller seller) {
        baseIfMissing(repo,"Spring Warm Coral Top",ProductCategory.FASHION,389,seller,"/img/products/tone-spring-coral-top.png",false,false,"ToneMatch Korean color analysis: Spring Warm. Best for peach, coral, ivory, light yellow, and warm beige clothing palettes.","ToneMatch: Spring Warm");
        baseIfMissing(repo,"Spring Warm Ivory Shirt",ProductCategory.FASHION,349,seller,"/img/products/tone-spring-ivory-shirt.png",false,false,"ToneMatch Korean color analysis: Spring Warm. Recommended for clear warm and bright clothing colors.","ToneMatch: Spring Warm");
        baseIfMissing(repo,"Summer Cool Lavender Blouse",ProductCategory.FASHION,429,seller,"/img/products/tone-summer-lavender-blouse.png",false,false,"ToneMatch Korean color analysis: Summer Cool. Best for lavender, powder blue, soft pink, cool gray, and pearl white.","ToneMatch: Summer Cool");
        baseIfMissing(repo,"Summer Cool Powder Blue Top",ProductCategory.FASHION,499,seller,"/img/products/tone-summer-powder-blue-top.png",false,false,"ToneMatch Korean color analysis: Summer Cool. Recommended for soft cool and muted clothing colors.","ToneMatch: Summer Cool");
        baseIfMissing(repo,"Autumn Warm Olive Overshirt",ProductCategory.FASHION,529,seller,"/img/products/tone-autumn-olive-overshirt.png",false,false,"ToneMatch Korean color analysis: Autumn Warm. Best for olive, camel, terracotta, mustard, and chocolate brown.","ToneMatch: Autumn Warm");
        baseIfMissing(repo,"Autumn Warm Camel Shirt",ProductCategory.FASHION,599,seller,"/img/products/tone-autumn-camel-shirt.png",false,false,"ToneMatch Korean color analysis: Autumn Warm. Recommended for earthy warm clothing palettes.","ToneMatch: Autumn Warm");
        baseIfMissing(repo,"Winter Cool Black Top",ProductCategory.FASHION,799,seller,"/img/products/tone-winter-black-top.png",false,false,"ToneMatch Korean color analysis: Winter Cool. Best for black, white, navy, royal blue, burgundy, and high contrast colors.","ToneMatch: Winter Cool");
        baseIfMissing(repo,"Winter Cool Navy Dress",ProductCategory.FASHION,699,seller,"/img/products/tone-winter-navy-dress.png",false,false,"ToneMatch Korean color analysis: Winter Cool. Recommended for sharp cool and high-contrast fashion colors.","ToneMatch: Winter Cool");
    }

    private void repairDemoProductRecords(ProductRepository repo) {
        setProductImage(repo,"Canned Tuna 6-Pack","/img/products/canned-tuna.png");
        setProductImage(repo,"Paper Towel 6 Rolls","/img/products/paper-towel.png");
        repo.findAll().stream()
                .filter(product -> product.getImageUrl() == null || product.getImageUrl().isBlank())
                .forEach(product -> {
                    product.setImageUrl("/img/products/placeholder-product.png");
                    repo.save(product);
                });
    }

    private void setProductImage(ProductRepository repo, String productName, String imageUrl) {
        repo.findByNameIgnoreCase(productName).ifPresent(product -> {
            product.setImageUrl(imageUrl);
            product.setPhotoAltText(productName + " product photo");
            repo.save(product);
        });
    }

    private void repairTryOnAndToneMatchRecords(ProductRepository repo) {
        archiveOldTryOnProduct(repo,"Men Basic T-Shirt - Green");
        archiveOldTryOnProduct(repo,"Men Basic T-Shirt - Navy");
        archiveOldTryOnProduct(repo,"Men Basic T-Shirt - White");
        archiveOldTryOnProduct(repo,"Men Basic T-Shirt - Black");
        archiveOldTryOnProduct(repo,"Women Simple Dress - Sage");
        archiveOldTryOnProduct(repo,"Women Simple Dress - Rose");
        archiveOldTryOnProduct(repo,"Women Simple Dress - Navy");
        archiveOldTryOnProduct(repo,"Women Simple Dress - Cream");
        setTryOnProduct(repo,"Men Realistic T-Shirt Try-On","MEN","/img/tryon/men-realistic-tshirt.png");
        setTryOnProduct(repo,"Women Realistic Dress Try-On","WOMEN","/img/tryon/women-realistic-dress.png");

        repairToneProduct(repo,"Spring Warm Coral Top","Spring Warm Coral Top","/img/products/tone-spring-coral-top.png","ToneMatch Korean color analysis: Spring Warm. Best for peach, coral, ivory, light yellow, and warm beige clothing palettes.","ToneMatch: Spring Warm");
        repairToneProduct(repo,"Spring Warm Ivory Shirt","Spring Warm Ivory Shirt","/img/products/tone-spring-ivory-shirt.png","ToneMatch Korean color analysis: Spring Warm. Recommended for clear warm and bright clothing colors.","ToneMatch: Spring Warm");
        repairToneProduct(repo,"Summer Cool Lavender Blouse","Summer Cool Lavender Blouse","/img/products/tone-summer-lavender-blouse.png","ToneMatch Korean color analysis: Summer Cool. Best for lavender, powder blue, soft pink, cool gray, and pearl white.","ToneMatch: Summer Cool");
        repairToneProduct(repo,"Summer Cool Powder Blue Cardigan","Summer Cool Powder Blue Top","/img/products/tone-summer-powder-blue-top.png","ToneMatch Korean color analysis: Summer Cool. Recommended for soft cool and muted clothing colors.","ToneMatch: Summer Cool");
        repairToneProduct(repo,"Summer Cool Powder Blue Top","Summer Cool Powder Blue Top","/img/products/tone-summer-powder-blue-top.png","ToneMatch Korean color analysis: Summer Cool. Recommended for soft cool and muted clothing colors.","ToneMatch: Summer Cool");
        repairToneProduct(repo,"Autumn Warm Olive Overshirt","Autumn Warm Olive Overshirt","/img/products/tone-autumn-olive-overshirt.png","ToneMatch Korean color analysis: Autumn Warm. Best for olive, camel, terracotta, mustard, and chocolate brown.","ToneMatch: Autumn Warm");
        repairToneProduct(repo,"Autumn Warm Camel Pants","Autumn Warm Camel Shirt","/img/products/tone-autumn-camel-shirt.png","ToneMatch Korean color analysis: Autumn Warm. Recommended for earthy warm clothing palettes.","ToneMatch: Autumn Warm");
        repairToneProduct(repo,"Autumn Warm Camel Shirt","Autumn Warm Camel Shirt","/img/products/tone-autumn-camel-shirt.png","ToneMatch Korean color analysis: Autumn Warm. Recommended for earthy warm clothing palettes.","ToneMatch: Autumn Warm");
        repairToneProduct(repo,"Winter Cool Black Blazer","Winter Cool Black Top","/img/products/tone-winter-black-top.png","ToneMatch Korean color analysis: Winter Cool. Best for black, white, navy, royal blue, burgundy, and high contrast colors.","ToneMatch: Winter Cool");
        repairToneProduct(repo,"Winter Cool Black Top","Winter Cool Black Top","/img/products/tone-winter-black-top.png","ToneMatch Korean color analysis: Winter Cool. Best for black, white, navy, royal blue, burgundy, and high contrast colors.","ToneMatch: Winter Cool");
        repairToneProduct(repo,"Winter Cool Navy Dress","Winter Cool Navy Dress","/img/products/tone-winter-navy-dress.png","ToneMatch Korean color analysis: Winter Cool. Recommended for sharp cool and high-contrast fashion colors.","ToneMatch: Winter Cool");
    }

    private void archiveOldTryOnProduct(ProductRepository repo, String productName) {
        repo.findByNameIgnoreCase(productName).ifPresent(product -> {
            product.setStatus("ARCHIVED");
            product.setTryOnEligible(false);
            product.setTryOnGender(null);
            product.setTryOnAssetUrl(null);
            repo.save(product);
        });
    }

    private void setTryOnProduct(ProductRepository repo, String productName, String gender, String assetUrl) {
        repo.findByNameIgnoreCase(productName).ifPresent(product -> {
            product.setCategory(ProductCategory.FASHION);
            product.setImageUrl(assetUrl);
            product.setPhotoAltText(productName + " try-on product photo");
            product.setTryOnEligible(true);
            product.setTryOnGender(gender);
            product.setTryOnAssetUrl(assetUrl);
            repo.save(product);
        });
    }

    private void repairToneProduct(ProductRepository repo, String currentName, String cleanName, String imageUrl, String description, String tag) {
        repo.findByNameIgnoreCase(currentName).ifPresent(product -> {
            product.setName(cleanName);
            product.setCategory(ProductCategory.FASHION);
            product.setImageUrl(imageUrl);
            product.setPhotoAltText(cleanName + " ToneMatch product photo");
            product.setDescription(description);
            product.setSustainabilityTag(tag);
            product.setTryOnEligible(false);
            product.setTryOnGender(null);
            product.setTryOnAssetUrl(null);
            repo.save(product);
        });
    }

    private void normalizeEssentialCategories(ProductRepository repo) {
        EnumSet<ProductCategory> legacyEssentialCategories = EnumSet.of(
                ProductCategory.FMCG,
                ProductCategory.CONVENIENCE_GOODS,
                ProductCategory.CONSUMER_STAPLES,
                ProductCategory.EVERYDAY_ESSENTIALS,
                ProductCategory.DAILY_NECESSITIES,
                ProductCategory.PACKAGED_GOODS
        );
        repo.findAll().stream()
                .filter(product -> product.getCategory() != null && legacyEssentialCategories.contains(product.getCategory()))
                .forEach(product -> {
                    product.setCategory(ProductCategory.GROCERIES);
                    repo.save(product);
                });
    }

    private void seedEverydaySubscriptionGoods(ProductRepository repo, Seller daily, Seller pantry, Seller eco) {
        baseIfMissing(repo,"Eco Laundry Detergent 1L",ProductCategory.GROCERIES,189,daily,"https://images.unsplash.com/photo-1626806819282-2c1dc01a5e0c?auto=format&fit=crop&w=900&q=80",true,false,"Fast-moving household detergent for repeat purchase.","Low-waste refill option available");
        baseIfMissing(repo,"Dishwashing Liquid Refill 1L",ProductCategory.GROCERIES,129,daily,"https://images.unsplash.com/photo-1626806819282-2c1dc01a5e0c?auto=format&fit=crop&w=900&q=80",true,false,"Repeat-use kitchen cleaning essential.","Refill-ready packaged goods");
        baseIfMissing(repo,"Hand Soap Refill Pack",ProductCategory.GROCERIES,99,daily,"https://images.unsplash.com/photo-1584305574647-0cc949a2bb9f?auto=format&fit=crop&w=900&q=80",true,false,"Convenience goods for household hygiene.","Reduced plastic refill pack");
        baseIfMissing(repo,"Toilet Tissue 12 Rolls",ProductCategory.GROCERIES,239,daily,"https://images.unsplash.com/photo-1583947581924-860bda6a26df?auto=format&fit=crop&w=900&q=80",true,false,"Daily necessity and subscription-ready household staple.","Bulk pack for fewer deliveries");
        baseIfMissing(repo,"Paper Towel 6 Rolls",ProductCategory.GROCERIES,199,daily,"/img/products/paper-towel.png",true,false,"Everyday cleaning and kitchen essential.","Consolidated delivery recommended");
        baseIfMissing(repo,"Trash Bags 30 Pieces",ProductCategory.GROCERIES,149,eco,"https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?auto=format&fit=crop&w=900&q=80",true,false,"Daily home necessity for scheduled replenishment.","Compostable option highlighted");
        baseIfMissing(repo,"Brown Rice 5kg Autoship Pack",ProductCategory.GROCERIES,420,pantry,"https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=80",true,false,"Consumer staple for repeat pantry replenishment.","Local staple source");
        baseIfMissing(repo,"Rolled Oats 1kg",ProductCategory.GROCERIES,210,pantry,"https://images.unsplash.com/photo-1517673132405-a56a62b18caf?auto=format&fit=crop&w=900&q=80",true,false,"Breakfast staple with monthly autoship option.","Sealed pantry pack");
        baseIfMissing(repo,"Instant Coffee 200g",ProductCategory.GROCERIES,230,pantry,"https://images.unsplash.com/photo-1447933601403-0c6688de566e?auto=format&fit=crop&w=900&q=80",true,false,"Packaged consumer good for repeat purchase.","Sealed packaged goods");
        baseIfMissing(repo,"Canned Tuna 6-Pack",ProductCategory.GROCERIES,349,pantry,"/img/products/canned-tuna.png",true,false,"Shelf-stable packaged goods for pantry subscription.","Batch-packed for delivery efficiency");
        baseIfMissing(repo,"Powdered Milk 1kg",ProductCategory.GROCERIES,399,pantry,"https://images.unsplash.com/photo-1563636619-e9143da7973b?auto=format&fit=crop&w=900&q=80",true,false,"Consumer staple suitable for scheduled replenishment.","Sealed family pack");
        baseIfMissing(repo,"Baby Wipes Monthly Pack",ProductCategory.GROCERIES,299,daily,"https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&w=900&q=80",true,false,"Everyday family essential for autoship.","Bulk pack reduces delivery frequency");
        baseIfMissing(repo,"Shampoo and Conditioner Set",ProductCategory.GROCERIES,289,daily,"https://images.unsplash.com/photo-1625772452859-1c03d5bf1137?auto=format&fit=crop&w=900&q=80",true,false,"Convenience personal care goods for repeat purchase.","Refill or bundle pack");
        baseIfMissing(repo,"Toothpaste Family Pack",ProductCategory.GROCERIES,179,daily,"https://images.unsplash.com/photo-1606811971618-4486d14f3f99?auto=format&fit=crop&w=900&q=80",true,false,"Daily oral care necessity for household autoship.","Family pack");
        baseIfMissing(repo,"All-Purpose Cleaner 1L",ProductCategory.GROCERIES,159,eco,"https://images.unsplash.com/photo-1585421514284-efb74c2b69ba?auto=format&fit=crop&w=900&q=80",true,false,"Fast-moving household cleaner for repeat purchase.","Low-tox cleaning formula");
        baseIfMissing(repo,"Cooking Oil 2L",ProductCategory.GROCERIES,289,pantry,"https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=900&q=80",true,false,"Consumer staple for kitchen replenishment.","Sealed pantry bottle");
    }

    private void baseIfMissing(ProductRepository repo,String name,ProductCategory cat,int price,Seller seller,String image,boolean sub,boolean tryEligible,String description,String sustainabilityTag){
        if (repo.findByNameIgnoreCase(name).isPresent()) return;
        Product p=new Product();p.setName(name);p.setDescription(description);p.setCategory(cat);p.setPrice(BigDecimal.valueOf(price));p.setStock(120);p.setEcoFriendly(true);p.setSustainabilityTag(sustainabilityTag);p.setTrustScore(92+(int)(Math.random()*6));p.setGreenScore(88+(int)(Math.random()*10));p.setImageUrl(image);p.setProductOrigin(seller.getPublicLocationLabel());p.setWarrantyPolicy("7-day buyer protection with digital refund tracking.");p.setSubscriptionEligible(sub);p.setSubscriptionDiscountPercent(5);p.setPhotoAltText(name+" photo");p.setReviewSummary("Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.");p.setRedFlagSummary("No fake-review pattern detected. Checkout is protected inside TrustCart.");p.setSeller(seller);p.setTryOnEligible(tryEligible);repo.save(p);
    }
    private void base(ProductRepository repo,String name,ProductCategory cat,int price,Seller seller,String image,boolean sub,boolean tryEligible){
        Product p=new Product();p.setName(name);p.setDescription("Verified product with protected checkout, review summary, trust score, and seller area visibility only.");p.setCategory(cat);p.setPrice(BigDecimal.valueOf(price));p.setStock(80);p.setEcoFriendly(true);p.setSustainabilityTag("Protected and verified listing");p.setTrustScore(90+(int)(Math.random()*8));p.setGreenScore(88+(int)(Math.random()*10));p.setImageUrl(image);p.setProductOrigin("Verified approved source");p.setWarrantyPolicy("7-day buyer protection with digital refund tracking.");p.setSubscriptionEligible(sub);p.setPhotoAltText(name+" photo");p.setReviewSummary("Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.");p.setRedFlagSummary("No fake-review pattern detected. Checkout is protected inside TrustCart.");p.setSeller(seller);repo.save(p);
    }
    private void tryon(ProductRepository repo,String name,String gender,String asset,Seller seller,int price){
        Product p=new Product();p.setName(name);p.setDescription("Virtual Try-On Preview enabled. Upload or take a photo and preview this item before checkout.");p.setCategory(ProductCategory.FASHION);p.setPrice(BigDecimal.valueOf(price));p.setStock(50);p.setEcoFriendly(true);p.setSustainabilityTag("Try before checkout to reduce returns");p.setTrustScore(95);p.setGreenScore(94);p.setImageUrl(asset);p.setProductOrigin("TrustCart Fashion Try-On Collection");p.setWarrantyPolicy("7-day buyer protection for protected orders.");p.setPhotoAltText(name+" try-on asset");p.setTryOnEligible(true);p.setTryOnGender(gender);p.setTryOnAssetUrl(asset);p.setReviewSummary("Try-On Preview available for this item.");p.setRedFlagSummary("Photo preview is processed in browser and not saved.");p.setSeller(seller);repo.save(p);
    }
    private void addGift(GiftRegistryItemRepository repo, GiftRegistry registry, ProductRepository products, String productName, int quantity, String priority, String note) {
        products.findByNameIgnoreCase(productName).ifPresent(product -> {
            GiftRegistryItem item = new GiftRegistryItem();
            item.setGiftRegistry(registry);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPriority(priority);
            item.setGiftNote(note);
            repo.save(item);
        });
    }


    @Bean
    @Order(3)
    CommandLineRunner seedWarehouses(TrustCartWarehouseRepository warehouses) {
        return args -> {
            warehouse(warehouses, "TC-MNL", "TrustCart Manila Warehouse", "Manila", "Metro Manila", "Pandacan / Manila logistics partner hub", 14.5995, 120.9842);
            warehouse(warehouses, "TC-QC", "TrustCart Quezon City Warehouse", "Quezon City", "Metro Manila", "North Metro Manila partner hub", 14.6760, 121.0437);
            warehouse(warehouses, "TC-MKT", "TrustCart Makati Warehouse", "Makati", "Metro Manila", "Central business district fulfillment hub", 14.5547, 121.0244);
            warehouse(warehouses, "TC-PAS", "TrustCart Pasig Warehouse", "Pasig", "Metro Manila", "Ortigas / Pasig partner fulfillment hub", 14.5764, 121.0851);
            warehouse(warehouses, "TC-SP", "TrustCart San Pedro Warehouse", "San Pedro", "Laguna", "San Pedro Laguna local fulfillment hub", 14.3440, 121.0576);
            warehouse(warehouses, "TC-CAL", "TrustCart Calamba Warehouse", "Calamba", "Laguna", "Calamba Laguna south hub", 14.2117, 121.1653);
            warehouse(warehouses, "TC-IMS", "TrustCart Imus Warehouse", "Imus", "Cavite", "Imus Cavite partner warehouse", 14.3860, 120.9368);
            warehouse(warehouses, "TC-DAS", "TrustCart Dasmariñas Warehouse", "Dasmariñas", "Cavite", "Dasmariñas Cavite local hub", 14.3294, 120.9367);
            warehouse(warehouses, "TC-CEB", "TrustCart Cebu City Warehouse", "Cebu City", "Cebu", "Visayas fulfillment warehouse", 10.3157, 123.8854);
            warehouse(warehouses, "TC-DVO", "TrustCart Davao Warehouse", "Davao City", "Davao del Sur", "Mindanao fulfillment warehouse", 7.1907, 125.4553);
            warehouse(warehouses, "TC-BAG", "TrustCart Baguio Warehouse", "Baguio", "Benguet", "Northern Luzon fulfillment partner hub", 16.4023, 120.5960);
            warehouse(warehouses, "TC-ILO", "TrustCart Iloilo Warehouse", "Iloilo City", "Iloilo", "Western Visayas fulfillment partner hub", 10.7202, 122.5621);
        };
    }

    private void warehouse(TrustCartWarehouseRepository repo, String code, String name, String city, String province, String address, double lat, double lon) {
        if (repo.findByCodeIgnoreCase(code).isPresent()) return;
        TrustCartWarehouse w = new TrustCartWarehouse();
        w.setCode(code);
        w.setName(name);
        w.setCity(city);
        w.setProvince(province);
        w.setAddress(address);
        w.setLatitude(lat);
        w.setLongitude(lon);
        w.setActive(true);
        repo.save(w);
    }

}
