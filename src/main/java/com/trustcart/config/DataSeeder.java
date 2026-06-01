package com.trustcart.config;

import com.trustcart.model.*;
import com.trustcart.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(BuyerAccountRepository buyers, SellerRepository sellers, ProductRepository products, DiscountCodeRepository discounts, GiftRegistryRepository registries, GiftRegistryItemRepository registryItems) {
        return args -> {
            if (buyers.findByEmailIgnoreCase("buyer@trustcart.ph").isEmpty()) {
                BuyerAccount b = new BuyerAccount();
                b.setFullName("Juan Trust"); b.setEmail("buyer@trustcart.ph"); b.setPassword("trust123"); b.setPhone("09179990000"); b.setDefaultAddress("San Pablo City, Laguna"); b.setPreferredCity("San Pablo City"); b.setPreferredLatitude(14.0683); b.setPreferredLongitude(121.3256); b.setLoyaltyPointsBalance(350); b.setLifetimeLoyaltyPoints(350); buyers.save(b);
            }
            Seller tech = seller(sellers,"GreenTech Manila","greentech@trustcart.ph","Verified electronics reseller","Plastic-Free Packaging","Manila","Metro Manila",14.5995,120.9842);
            Seller local = seller(sellers,"Local Goods PH","localgoods@trustcart.ph","Local Filipino MSME","Locally Sourced","San Pablo City","Laguna",14.0683,121.3256);
            Seller eco = seller(sellers,"EcoHome Essentials","ecohome@trustcart.ph","Sustainable home products","Low-Waste Packaging","Calamba","Laguna",14.2117,121.1653);
            code(discounts,"WELCOME10","10% off first protected order for first-time buyers.",BigDecimal.ZERO,10,BigDecimal.ZERO,true,local);
            code(discounts,"GREEN5","5% off for green checkout buyers.",BigDecimal.valueOf(500),5,BigDecimal.ZERO,false,eco);
            code(discounts,"LOCAL50","₱50 off selected local Filipino products.",BigDecimal.valueOf(300),0,BigDecimal.valueOf(50),false,local);
            if (products.count() == 0) {
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
        return repo.findByEmailIgnoreCase(email).orElseGet(()->{Seller s=new Seller();s.setStoreName(name);s.setEmail(email);s.setPassword("trust123");s.setBusinessType(type);s.setSustainabilityBadge(badge);s.setStoreExactAddress(name+" Fulfillment Hub");s.setStoreCity(city);s.setStoreProvince(prov);s.setLatitude(lat);s.setLongitude(lon);s.setEcoCommitment("Verified commitment to buyer protection and sustainable packaging.");s.setVerificationNote("Business, product, and location verified.");s.setApprovedBy("TrustCart Verification");return repo.save(s);});
    }
    private void code(DiscountCodeRepository repo,String code,String desc,BigDecimal min,int pct,BigDecimal amt,boolean first,Seller seller){
        repo.findByCodeIgnoreCase(code).orElseGet(()->{DiscountCode d=new DiscountCode();d.setCode(code);d.setDescription(desc);d.setMinimumSpend(min);d.setPercentOff(pct);d.setAmountOff(amt);d.setFirstOrderOnly(first);d.setSeller(seller);d.setCreatedBySeller(seller.getStoreName());return repo.save(d);});
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

}
