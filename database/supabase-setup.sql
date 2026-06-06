-- TrustCart Supabase PostgreSQL setup - COMPLETE CLEAN FULL DATA VERSION
-- This file resets and loads the full TrustCart buyer-seller marketplace.
-- Includes: buyer/seller accounts, full product catalog, 20 virtual try-on items,
-- discount codes, orders structure, refunds, autoship, TrustPoints-ready fields,
-- live-selling-ready website data fields, and sustainability/trust scoring fields.
-- Run this in Supabase SQL Editor only when you want to reset/reload the prototype data.

DROP TABLE IF EXISTS support_ticket CASCADE;
DROP TABLE IF EXISTS gift_registry_item CASCADE;
DROP TABLE IF EXISTS gift_registry CASCADE;
DROP TABLE IF EXISTS autoship_subscription CASCADE;
DROP TABLE IF EXISTS discount_code CASCADE;
DROP TABLE IF EXISTS refund_request CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS customer_order CASCADE;
DROP TABLE IF EXISTS customer_orders CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS seller CASCADE;
DROP TABLE IF EXISTS buyer_account CASCADE;

CREATE TABLE buyer_account (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    default_address VARCHAR(1000),
    password VARCHAR(255),
    preferred_city VARCHAR(255),
    preferred_latitude DOUBLE PRECISION,
    preferred_longitude DOUBLE PRECISION,
    preferred_radius_km INTEGER DEFAULT 5,
    nearby_seller_first BOOLEAN DEFAULT TRUE,
    pickup_interested BOOLEAN DEFAULT FALSE,
    loyalty_points_balance INTEGER DEFAULT 0,
    lifetime_loyalty_points INTEGER DEFAULT 0,
    lifetime_spend NUMERIC(12,2) DEFAULT 0,
    loyalty_tier VARCHAR(255) DEFAULT 'Starter Green Member',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    report_count INTEGER DEFAULT 0,
    admin_safety_note VARCHAR(1500),
    credibility_score INTEGER DEFAULT 95,
    risk_level VARCHAR(50) DEFAULT 'LOW',
    verification_status VARCHAR(80) DEFAULT 'VERIFIED',
    risk_note VARCHAR(1500) DEFAULT 'Good standing.',
    id_proof_url VARCHAR(1200),
    face_verification_url VARCHAR(1200),
    verification_requested_at TIMESTAMP,
    risk_reviewed_at TIMESTAMP,
    deactivated_at TIMESTAMP,
    blocked_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE seller (
    id BIGSERIAL PRIMARY KEY,
    store_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    password VARCHAR(255),
    business_type VARCHAR(255),
    sustainability_badge VARCHAR(255),
    reliability_score INTEGER DEFAULT 90,
    response_rate_score INTEGER DEFAULT 95,
    complaint_rate_score INTEGER DEFAULT 95,
    return_rate_score INTEGER DEFAULT 94,
    green_compliance_score INTEGER DEFAULT 90,
    business_verified BOOLEAN DEFAULT TRUE,
    identity_verified BOOLEAN DEFAULT TRUE,
    document_verified BOOLEAN DEFAULT TRUE,
    product_compliance_checked BOOLEAN DEFAULT TRUE,
    invited_or_approved_only BOOLEAN DEFAULT TRUE,
    document_proof_url VARCHAR(1000),
    eco_commitment VARCHAR(1000),
    verification_note VARCHAR(1000),
    approved_by VARCHAR(255),
    store_exact_address VARCHAR(1000),
    store_city VARCHAR(255),
    store_province VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    service_radius_km INTEGER DEFAULT 5,
    pickup_available BOOLEAN DEFAULT TRUE,
    store_location_verified BOOLEAN DEFAULT TRUE,
    can_use_fbt BOOLEAN DEFAULT FALSE,
    fulfillment_preference VARCHAR(255) DEFAULT 'SELLER',
    requirements_status VARCHAR(255) DEFAULT 'COMPLETED',
    requirements_note VARCHAR(1200) DEFAULT 'Requirements completed and approved by TrustCart.',
    location_proof_url VARCHAR(1000),
    store_profile_image_url VARCHAR(1200),
    store_banner_image_url VARCHAR(1200),
    store_description VARCHAR(1200),
    status VARCHAR(50) DEFAULT 'APPROVED',
    credibility_score INTEGER DEFAULT 95,
    risk_level VARCHAR(50) DEFAULT 'LOW',
    risk_status VARCHAR(80) DEFAULT 'GOOD_STANDING',
    risk_note VARCHAR(1500) DEFAULT 'Good standing.',
    risk_reviewed_at TIMESTAMP,
    warning_sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(1800),
    category VARCHAR(80),
    price NUMERIC(12,2),
    stock INTEGER DEFAULT 0,
    eco_friendly BOOLEAN DEFAULT FALSE,
    sustainability_tag VARCHAR(255),
    trust_cart_shield BOOLEAN DEFAULT TRUE,
    authentic_item_checked BOOLEAN DEFAULT TRUE,
    verified_reviews_only BOOLEAN DEFAULT TRUE,
    suspicious_review_flag BOOLEAN DEFAULT FALSE,
    plastic_free_packaging BOOLEAN DEFAULT TRUE,
    locally_sourced BOOLEAN DEFAULT FALSE,
    low_waste_delivery BOOLEAN DEFAULT TRUE,
    trust_score INTEGER DEFAULT 90,
    green_score INTEGER DEFAULT 90,
    seller_verification_score INTEGER DEFAULT 25,
    product_authenticity_score INTEGER DEFAULT 24,
    review_quality_score INTEGER DEFAULT 23,
    delivery_reliability_score INTEGER DEFAULT 18,
    sustainability_score INTEGER DEFAULT 10,
    return_risk_score INTEGER DEFAULT 94,
    review_summary VARCHAR(1400),
    red_flag_summary VARCHAR(1200),
    image_url VARCHAR(1200),
    product_origin VARCHAR(255),
    warranty_policy VARCHAR(255),
    subscription_eligible BOOLEAN DEFAULT FALSE,
    subscription_discount_percent INTEGER DEFAULT 5,
    photo_alt_text VARCHAR(255),
    status VARCHAR(50) DEFAULT 'APPROVED',
    try_on_eligible BOOLEAN DEFAULT FALSE,
    try_on_gender VARCHAR(20),
    try_on_asset_url VARCHAR(1000),
    stock_status VARCHAR(80) DEFAULT 'In Stock',
    estimated_delivery VARCHAR(80) DEFAULT 'ETA: 1-2 days',
    fulfilled_by VARCHAR(255) DEFAULT 'SELLER',
    fulfillment_status VARCHAR(255) DEFAULT 'SELLER_MANAGED',
    trust_cart_stock INTEGER DEFAULT 0,
    fulfillment_note VARCHAR(1200) DEFAULT 'Seller stores, packs, and ships this product.',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    seller_id BIGINT REFERENCES seller(id)
);


CREATE TABLE support_ticket (
    id BIGSERIAL PRIMARY KEY,
    ticket_code VARCHAR(255) UNIQUE,
    type VARCHAR(255) DEFAULT 'GENERAL',
    status VARCHAR(255) DEFAULT 'OPEN',
    priority VARCHAR(255) DEFAULT 'NORMAL',
    reporter_name VARCHAR(255),
    reporter_email VARCHAR(255),
    subject VARCHAR(1200),
    message VARCHAR(2500),
    product_id BIGINT REFERENCES product(id),
    seller_id BIGINT REFERENCES seller(id),
    buyer_id BIGINT REFERENCES buyer_account(id),
    reported_buyer_email VARCHAR(255),
    admin_note VARCHAR(2500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer_order (
    id BIGSERIAL PRIMARY KEY,
    order_code VARCHAR(255) UNIQUE,
    full_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    shipping_address VARCHAR(1000),
    payment_method VARCHAR(80),
    payment_status VARCHAR(100),
    order_status VARCHAR(80) DEFAULT 'PLACED',
    eco_packaging BOOLEAN DEFAULT FALSE,
    no_extra_plastic BOOLEAN DEFAULT FALSE,
    consolidated_delivery BOOLEAN DEFAULT FALSE,
    delivery_option VARCHAR(80) DEFAULT 'STANDARD_DELIVERY',
    buyer_market_location VARCHAR(255),
    platform_protection_note VARCHAR(1000),
    subtotal NUMERIC(12,2) DEFAULT 0,
    shipping_fee NUMERIC(12,2) DEFAULT 0,
    eco_packaging_fee NUMERIC(12,2) DEFAULT 0,
    eco_delivery_discount NUMERIC(12,2) DEFAULT 0,
    discount NUMERIC(12,2) DEFAULT 0,
    promo_discount NUMERIC(12,2) DEFAULT 0,
    loyalty_points_discount NUMERIC(12,2) DEFAULT 0,
    discount_code VARCHAR(80),
    discount_code_description VARCHAR(255),
    loyalty_points_earned INTEGER DEFAULT 0,
    loyalty_points_redeemed INTEGER DEFAULT 0,
    loyalty_tier_after_order VARCHAR(100),
    total NUMERIC(12,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES customer_order(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES product(id),
    product_name VARCHAR(255),
    seller_name VARCHAR(255),
    quantity INTEGER,
    unit_price NUMERIC(12,2),
    line_total NUMERIC(12,2)
);

CREATE TABLE refund_request (
    id BIGSERIAL PRIMARY KEY,
    order_code VARCHAR(255),
    email VARCHAR(255),
    reason VARCHAR(1500),
    evidence_url VARCHAR(255),
    status VARCHAR(80) DEFAULT 'SUBMITTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_id BIGINT REFERENCES customer_order(id)
);

CREATE TABLE discount_code (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(80) UNIQUE,
    description VARCHAR(600),
    minimum_spend NUMERIC(12,2) DEFAULT 0,
    percent_off INTEGER DEFAULT 0,
    amount_off NUMERIC(12,2) DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    first_order_only BOOLEAN DEFAULT FALSE,
    subscription_boost BOOLEAN DEFAULT FALSE,
    max_redemptions INTEGER DEFAULT 0,
    times_redeemed INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    seller_id BIGINT REFERENCES seller(id),
    created_by_seller VARCHAR(255)
);



CREATE TABLE gift_registry (
    id BIGSERIAL PRIMARY KEY,
    registry_name VARCHAR(255),
    registry_type VARCHAR(100),
    recipient_name VARCHAR(255),
    recipient_email VARCHAR(255),
    event_date DATE,
    delivery_city VARCHAR(255),
    registry_note VARCHAR(1000),
    protected_delivery_note VARCHAR(1000) DEFAULT 'Recipient exact address is hidden. Gifts must be purchased inside TrustCart to remain covered by Buyer Protection.',
    share_code VARCHAR(255) UNIQUE,
    privacy VARCHAR(80) DEFAULT 'PUBLIC_LINK',
    status VARCHAR(80) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    buyer_id BIGINT NOT NULL REFERENCES buyer_account(id)
);

CREATE TABLE gift_registry_item (
    id BIGSERIAL PRIMARY KEY,
    gift_registry_id BIGINT NOT NULL REFERENCES gift_registry(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id),
    quantity INTEGER DEFAULT 1,
    purchased_quantity INTEGER DEFAULT 0,
    priority VARCHAR(100) DEFAULT 'Nice to have',
    gift_note VARCHAR(800),
    status VARCHAR(80) DEFAULT 'NEEDED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE autoship_subscription (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT REFERENCES buyer_account(id),
    product_id BIGINT REFERENCES product(id),
    frequency VARCHAR(80) DEFAULT 'MONTHLY',
    quantity INTEGER DEFAULT 1,
    recurring_price NUMERIC(12,2) DEFAULT 0,
    subscription_discount_percent INTEGER DEFAULT 5,
    next_shipment_date DATE,
    status VARCHAR(80) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    protection_note VARCHAR(1000)
);

-- Starter buyer account
INSERT INTO buyer_account(full_name,email,password,phone,default_address,preferred_city,preferred_latitude,preferred_longitude,preferred_radius_km,nearby_seller_first,pickup_interested,loyalty_points_balance,lifetime_loyalty_points,lifetime_spend,loyalty_tier) VALUES
('Juan Trust','buyer@trustcart.ph','trust123','09179990000','San Pablo City, Laguna','San Pablo City',14.0683,121.3256,5,true,false,350,350,0,'Starter Green Member');

-- Verified seller accounts
INSERT INTO seller(store_name,email,password,phone,business_type,sustainability_badge,reliability_score,response_rate_score,complaint_rate_score,return_rate_score,green_compliance_score,business_verified,identity_verified,document_verified,product_compliance_checked,invited_or_approved_only,document_proof_url,eco_commitment,verification_note,approved_by,store_exact_address,store_city,store_province,latitude,longitude,service_radius_km,pickup_available,store_location_verified,location_proof_url,status) VALUES
('GreenTech Manila','greentech@trustcart.ph','trust123','09170000001','Verified electronics reseller','Plastic-Free Packaging',95,96,95,94,88,true,true,true,true,true,'Verified business proof','Plastic-reduced electronics packaging','Business, product, and location verified.','TrustCart Verification','GreenTech Manila Fulfillment Hub','Manila','Metro Manila',14.5995,120.9842,8,true,true,'Verified map pin proof','APPROVED'),
('Local Goods PH','localgoods@trustcart.ph','trust123','09170000002','Local Filipino MSME','Locally Sourced',93,96,95,94,90,true,true,true,true,true,'Verified business proof','Local sourcing and recyclable packaging','Business, product, and location verified.','TrustCart Verification','Local Goods Hub','San Pablo City','Laguna',14.0683,121.3256,6,true,true,'Verified map pin proof','APPROVED'),
('EcoHome Essentials','ecohome@trustcart.ph','trust123','09170000003','Sustainable home products','Low-Waste Packaging',91,96,95,94,92,true,true,true,true,true,'Verified business proof','Low-waste products and eco-packaging','Business, product, and location verified.','TrustCart Verification','EcoHome Laguna Sorting Hub','Calamba','Laguna',14.2117,121.1653,10,true,true,'Verified map pin proof','APPROVED');

-- Platform and seller-created discount codes
INSERT INTO discount_code(code,description,minimum_spend,percent_off,amount_off,active,first_order_only,subscription_boost,max_redemptions,times_redeemed,seller_id,created_by_seller) VALUES
('WELCOME10','10% off first protected order for first-time buyers.',0,10,0,true,true,false,0,0,2,'Local Goods PH'),
('GREEN5','5% off for green checkout buyers.',500,5,0,true,false,false,0,0,3,'EcoHome Essentials'),
('LOCAL50','₱50 off selected local Filipino products.',300,0,50,true,false,false,0,0,2,'Local Goods PH');

-- Full product catalog: 42 marketplace products, at least 3 per category
INSERT INTO product(name,description,category,price,stock,eco_friendly,sustainability_tag,trust_cart_shield,authentic_item_checked,verified_reviews_only,suspicious_review_flag,plastic_free_packaging,locally_sourced,low_waste_delivery,trust_score,green_score,seller_verification_score,product_authenticity_score,review_quality_score,delivery_reliability_score,sustainability_score,return_risk_score,review_summary,red_flag_summary,image_url,product_origin,warranty_policy,subscription_eligible,subscription_discount_percent,photo_alt_text,status,try_on_eligible,try_on_gender,try_on_asset_url,stock_status,estimated_delivery,seller_id) VALUES
('Wireless Earbuds','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','ELECTRONICS',899,80,true,'Protected and verified listing',true,true,true,false,false,false,true,93,91,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Wireless Earbuds product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Power Bank 20000mAh','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','ELECTRONICS',1299,80,true,'Protected and verified listing',true,true,true,false,false,false,true,94,92,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Power Bank 20000mAh product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Bluetooth Speaker','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','ELECTRONICS',749,80,true,'Protected and verified listing',true,true,true,false,false,false,true,95,93,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Bluetooth Speaker product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Fast Charger Type-C','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','MOBILE_ACCESSORIES',399,80,true,'Protected and verified listing',true,true,true,false,false,false,true,96,94,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1583863788434-e58a36330cf0?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Fast Charger Type-C product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Shockproof Phone Case','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','MOBILE_ACCESSORIES',199,80,true,'Protected and verified listing',true,true,true,false,false,false,true,97,95,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1601593346740-925612772716?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Shockproof Phone Case product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Tempered Glass','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','MOBILE_ACCESSORIES',149,80,true,'Protected and verified listing',true,true,true,false,false,false,true,92,96,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Tempered Glass product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Organic Cotton Shirt','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','FASHION',349,80,true,'Protected and verified listing',true,true,true,false,true,true,true,93,97,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Organic Cotton Shirt product photo','APPROVED',true,NULL,NULL,'In Stock','ETA: Today',2),
('Denim Pants','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','FASHION',799,80,true,'Protected and verified listing',true,true,true,false,true,true,true,94,90,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Denim Pants product photo','APPROVED',true,NULL,NULL,'In Stock','ETA: Today',2),
('Eco Canvas Tote Bag','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','FASHION',299,80,true,'Protected and verified listing',true,true,true,false,true,true,true,95,91,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1597484662317-9bd7bdda2907?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Eco Canvas Tote Bag product photo','APPROVED',true,NULL,NULL,'In Stock','ETA: Today',2),
('Bamboo Toothbrush Set','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','BEAUTY_PERSONAL_CARE',129,80,true,'Protected and verified listing',true,true,true,false,true,true,true,96,92,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Bamboo Toothbrush Set product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Organic Facial Wash','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','BEAUTY_PERSONAL_CARE',249,80,true,'Protected and verified listing',true,true,true,false,true,true,true,97,93,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Organic Facial Wash product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Sunscreen SPF50','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','BEAUTY_PERSONAL_CARE',399,80,true,'Protected and verified listing',true,true,true,false,true,true,true,92,94,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1620916297397-a4a5402a3c6c?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Sunscreen SPF50 product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Reusable Food Container','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','HOME_LIVING',299,80,true,'Protected and verified listing',true,true,true,false,true,false,true,93,95,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1584346133934-a3afd2a33c4c?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Reusable Food Container product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('LED Desk Lamp','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','HOME_LIVING',599,80,true,'Protected and verified listing',true,true,true,false,true,false,true,94,96,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'LED Desk Lamp product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Bamboo Organizer','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','HOME_LIVING',459,80,true,'Protected and verified listing',true,true,true,false,true,false,true,95,97,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1618220179428-22790b461013?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Bamboo Organizer product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Brown Rice 5kg','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','GROCERIES',420,80,true,'Protected and verified listing',true,true,true,false,true,true,true,96,90,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Brown Rice 5kg product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Organic Coffee Beans','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','GROCERIES',350,80,true,'Protected and verified listing',true,true,true,false,true,true,true,97,91,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1447933601403-0c6688de566e?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Organic Coffee Beans product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Muscovado Sugar 1kg','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','GROCERIES',180,80,true,'Protected and verified listing',true,true,true,false,true,true,true,92,92,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1587486937303-32eaa2134b78?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Muscovado Sugar 1kg product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Digital Thermometer','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','HEALTH_WELLNESS',299,80,true,'Protected and verified listing',true,true,true,false,false,false,true,93,93,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Digital Thermometer product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Resistance Band Set','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','HEALTH_WELLNESS',399,80,true,'Protected and verified listing',true,true,true,false,false,false,true,94,94,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1599058917212-d750089bc07e?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Resistance Band Set product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Reusable Water Bottle 1L','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','HEALTH_WELLNESS',249,80,true,'Protected and verified listing',true,true,true,false,false,false,true,95,95,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Reusable Water Bottle 1L product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Baby Wipes Eco Pack','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','BABY_KIDS',189,80,true,'Protected and verified listing',true,true,true,false,false,false,true,96,96,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Baby Wipes Eco Pack product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Educational Puzzle Toy','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','BABY_KIDS',299,80,true,'Protected and verified listing',true,true,true,false,false,true,true,97,97,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1587654780291-39c9404d746b?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Educational Puzzle Toy product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Kids Cotton Shirt','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','BABY_KIDS',249,80,true,'Protected and verified listing',true,true,true,false,false,true,true,92,90,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Kids Cotton Shirt product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Yoga Mat','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','SPORTS_OUTDOORS',499,80,true,'Protected and verified listing',true,true,true,false,false,false,true,93,91,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Yoga Mat product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Jump Rope','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','SPORTS_OUTDOORS',149,80,true,'Protected and verified listing',true,true,true,false,false,false,true,94,92,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1605296867304-46d5465a13f1?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Jump Rope product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Sports Towel','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','SPORTS_OUTDOORS',199,80,true,'Protected and verified listing',true,true,true,false,false,true,true,95,93,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1556197408-904afb6a4666?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Sports Towel product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Recycled Notebook','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','SCHOOL_OFFICE',89,80,true,'Protected and verified listing',true,true,true,false,false,true,true,96,94,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Recycled Notebook product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Ballpen Set','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','SCHOOL_OFFICE',99,80,true,'Protected and verified listing',true,true,true,false,false,true,true,97,95,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Ballpen Set product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Desk Calculator','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','SCHOOL_OFFICE',249,80,true,'Protected and verified listing',true,true,true,false,false,false,true,92,96,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1587145820266-a5951ee6f620?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Desk Calculator product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Helmet Cleaner','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','AUTOMOTIVE_MOTORCYCLE',199,80,true,'Protected and verified listing',true,true,true,false,false,false,true,93,97,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Helmet Cleaner product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Motorcycle Phone Holder','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','AUTOMOTIVE_MOTORCYCLE',349,80,true,'Protected and verified listing',true,true,true,false,false,false,true,94,90,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1605514449459-5a9cfa0b9955?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Motorcycle Phone Holder product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Tire Pressure Gauge','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','AUTOMOTIVE_MOTORCYCLE',299,80,true,'Protected and verified listing',true,true,true,false,false,false,true,95,91,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1607860108855-64acf2078ed9?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Tire Pressure Gauge product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',1),
('Organic Pet Shampoo','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','PET_SUPPLIES',299,80,true,'Protected and verified listing',true,true,true,false,false,false,true,96,92,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Organic Pet Shampoo product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Cat Litter 5L','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','PET_SUPPLIES',249,80,true,'Protected and verified listing',true,true,true,false,false,true,true,97,93,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1574144611937-0df059b5ef3e?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Cat Litter 5L product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Dog Chew Toy','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','PET_SUPPLIES',199,80,true,'Protected and verified listing',true,true,true,false,false,true,true,92,94,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1601758228041-f3b2795255f1?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Dog Chew Toy product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Metal Straw Set','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','SUSTAINABLE_PRODUCTS',99,80,true,'Protected and verified listing',true,true,true,false,true,false,true,93,95,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Metal Straw Set product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Reusable Shopping Bag','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','SUSTAINABLE_PRODUCTS',149,80,true,'Protected and verified listing',true,true,true,false,true,false,true,94,96,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Reusable Shopping Bag product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Compostable Trash Bags','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','SUSTAINABLE_PRODUCTS',189,80,true,'Protected and verified listing',true,true,true,false,true,false,true,95,97,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',true,5,'Compostable Trash Bags product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: 1-2 days',3),
('Handwoven Pouch','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','LOCAL_FILIPINO_PRODUCTS',250,80,true,'Protected and verified listing',true,true,true,false,true,true,true,96,90,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1516762689617-e1cffcef479d?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Handwoven Pouch product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Local Tablea Chocolate','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','LOCAL_FILIPINO_PRODUCTS',180,80,true,'Protected and verified listing',true,true,true,false,true,true,true,97,91,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1606312619070-d48b4c652a52?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Local Tablea Chocolate product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2),
('Abaca Storage Basket','Verified product with protected checkout, review summary, trust score, green score, seller-area visibility only, and platform buyer protection.','LOCAL_FILIPINO_PRODUCTS',499,80,true,'Protected and verified listing',true,true,true,false,true,true,true,92,92,25,24,23,18,10,94,'Verified purchase reviews only. Product quality, seller reliability, and return risk are summarized for buyers.','No fake-review pattern detected. Checkout is protected inside TrustCart.','https://images.unsplash.com/photo-1603204077779-bed963ea7d0e?auto=format&fit=crop&w=900&q=80','Verified approved source','7-day buyer protection with digital refund request tracking.',false,5,'Abaca Storage Basket product photo','APPROVED',false,NULL,NULL,'In Stock','ETA: Today',2);

-- Virtual Try-On catalog: simple realistic demo items with color options
INSERT INTO product(name,description,category,price,stock,eco_friendly,sustainability_tag,trust_cart_shield,authentic_item_checked,verified_reviews_only,suspicious_review_flag,plastic_free_packaging,locally_sourced,low_waste_delivery,trust_score,green_score,seller_verification_score,product_authenticity_score,review_quality_score,delivery_reliability_score,sustainability_score,return_risk_score,review_summary,red_flag_summary,image_url,product_origin,warranty_policy,subscription_eligible,subscription_discount_percent,photo_alt_text,status,try_on_eligible,try_on_gender,try_on_asset_url,stock_status,estimated_delivery,seller_id) VALUES
('Men Basic T-Shirt - Green','Virtual Try-On Preview enabled. Upload or take a photo and preview this simple men''s t-shirt color before checkout. The preview is processed in the browser and is not permanently saved.','FASHION',389,50,true,'Try before checkout to reduce returns',true,true,true,false,true,true,true,95,94,25,24,23,18,10,94,'Realistic Try-On Preview available for this item.','Photo preview is processed in browser and not saved.','/img/tryon/men-tshirt-green.png','TrustCart Men Fashion Try-On Collection','7-day buyer protection for protected orders.',false,0,'Men Basic T-Shirt - Green try-on asset','APPROVED',true,'MEN','/img/tryon/men-tshirt-green.png','In Stock','ETA: 1-2 days',2),
('Men Basic T-Shirt - Navy','Virtual Try-On Preview enabled. Upload or take a photo and preview this simple men''s t-shirt color before checkout. The preview is processed in the browser and is not permanently saved.','FASHION',409,50,true,'Try before checkout to reduce returns',true,true,true,false,true,true,true,95,94,25,24,23,18,10,94,'Realistic Try-On Preview available for this item.','Photo preview is processed in browser and not saved.','/img/tryon/men-tshirt-navy.png','TrustCart Men Fashion Try-On Collection','7-day buyer protection for protected orders.',false,0,'Men Basic T-Shirt - Navy try-on asset','APPROVED',true,'MEN','/img/tryon/men-tshirt-navy.png','In Stock','ETA: 1-2 days',2),
('Men Basic T-Shirt - White','Virtual Try-On Preview enabled. Upload or take a photo and preview this simple men''s t-shirt color before checkout. The preview is processed in the browser and is not permanently saved.','FASHION',429,50,true,'Try before checkout to reduce returns',true,true,true,false,true,true,true,95,94,25,24,23,18,10,94,'Realistic Try-On Preview available for this item.','Photo preview is processed in browser and not saved.','/img/tryon/men-tshirt-white.png','TrustCart Men Fashion Try-On Collection','7-day buyer protection for protected orders.',false,0,'Men Basic T-Shirt - White try-on asset','APPROVED',true,'MEN','/img/tryon/men-tshirt-white.png','In Stock','ETA: 1-2 days',2),
('Men Basic T-Shirt - Black','Virtual Try-On Preview enabled. Upload or take a photo and preview this simple men''s t-shirt color before checkout. The preview is processed in the browser and is not permanently saved.','FASHION',449,50,true,'Try before checkout to reduce returns',true,true,true,false,true,true,true,95,94,25,24,23,18,10,94,'Realistic Try-On Preview available for this item.','Photo preview is processed in browser and not saved.','/img/tryon/men-tshirt-black.png','TrustCart Men Fashion Try-On Collection','7-day buyer protection for protected orders.',false,0,'Men Basic T-Shirt - Black try-on asset','APPROVED',true,'MEN','/img/tryon/men-tshirt-black.png','In Stock','ETA: 1-2 days',2),
('Women Simple Dress - Sage','Virtual Try-On Preview enabled. Upload or take a photo and preview this simple women''s dress color before checkout. The preview is processed in the browser and is not permanently saved.','FASHION',459,50,true,'Try before checkout to reduce returns',true,true,true,false,true,true,true,95,94,25,24,23,18,10,94,'Realistic Try-On Preview available for this item.','Photo preview is processed in browser and not saved.','/img/tryon/women-dress-sage.png','TrustCart Women Fashion Try-On Collection','7-day buyer protection for protected orders.',false,0,'Women Simple Dress - Sage try-on asset','APPROVED',true,'WOMEN','/img/tryon/women-dress-sage.png','In Stock','ETA: 1-2 days',2),
('Women Simple Dress - Rose','Virtual Try-On Preview enabled. Upload or take a photo and preview this simple women''s dress color before checkout. The preview is processed in the browser and is not permanently saved.','FASHION',484,50,true,'Try before checkout to reduce returns',true,true,true,false,true,true,true,95,94,25,24,23,18,10,94,'Realistic Try-On Preview available for this item.','Photo preview is processed in browser and not saved.','/img/tryon/women-dress-rose.png','TrustCart Women Fashion Try-On Collection','7-day buyer protection for protected orders.',false,0,'Women Simple Dress - Rose try-on asset','APPROVED',true,'WOMEN','/img/tryon/women-dress-rose.png','In Stock','ETA: 1-2 days',2),
('Women Simple Dress - Navy','Virtual Try-On Preview enabled. Upload or take a photo and preview this simple women''s dress color before checkout. The preview is processed in the browser and is not permanently saved.','FASHION',509,50,true,'Try before checkout to reduce returns',true,true,true,false,true,true,true,95,94,25,24,23,18,10,94,'Realistic Try-On Preview available for this item.','Photo preview is processed in browser and not saved.','/img/tryon/women-dress-navy.png','TrustCart Women Fashion Try-On Collection','7-day buyer protection for protected orders.',false,0,'Women Simple Dress - Navy try-on asset','APPROVED',true,'WOMEN','/img/tryon/women-dress-navy.png','In Stock','ETA: 1-2 days',2),
('Women Simple Dress - Cream','Virtual Try-On Preview enabled. Upload or take a photo and preview this simple women''s dress color before checkout. The preview is processed in the browser and is not permanently saved.','FASHION',534,50,true,'Try before checkout to reduce returns',true,true,true,false,true,true,true,95,94,25,24,23,18,10,94,'Realistic Try-On Preview available for this item.','Photo preview is processed in browser and not saved.','/img/tryon/women-dress-cream.png','TrustCart Women Fashion Try-On Collection','7-day buyer protection for protected orders.',false,0,'Women Simple Dress - Cream try-on asset','APPROVED',true,'WOMEN','/img/tryon/women-dress-cream.png','In Stock','ETA: 1-2 days',2);


-- Buyers can create autoship records from the website.

-- Quick verification counts after run:
-- SELECT COUNT(*) AS product_count FROM product;
-- SELECT COUNT(*) AS try_on_count FROM product WHERE try_on_eligible = true;
-- SELECT COUNT(*) AS seller_count FROM seller;
-- SELECT COUNT(*) AS registry_count FROM gift_registry;

-- TrustCart chat, support source tabs, and warehouse incoming-stock tables
ALTER TABLE support_ticket ADD COLUMN IF NOT EXISTS source_role VARCHAR(50) DEFAULT 'BUYER';

CREATE TABLE IF NOT EXISTS chat_thread (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL REFERENCES buyer_account(id),
    seller_id BIGINT NOT NULL REFERENCES seller(id),
    product_id BIGINT REFERENCES product(id),
    subject VARCHAR(255),
    status VARCHAR(50) DEFAULT 'OPEN',
    last_message_preview VARCHAR(255),
    last_sender_role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chat_message (
    id BIGSERIAL PRIMARY KEY,
    thread_id BIGINT NOT NULL REFERENCES chat_thread(id),
    sender_role VARCHAR(50),
    sender_name VARCHAR(255),
    sender_email VARCHAR(255),
    message VARCHAR(2500),
    internal_flag BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS trust_cart_warehouse (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    city VARCHAR(255),
    province VARCHAR(255),
    address VARCHAR(1200),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS incoming_stock_shipment (
    id BIGSERIAL PRIMARY KEY,
    shipment_code VARCHAR(255) UNIQUE,
    seller_id BIGINT NOT NULL REFERENCES seller(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    warehouse_id BIGINT NOT NULL REFERENCES trust_cart_warehouse(id),
    quantity_planned INTEGER DEFAULT 0,
    quantity_received INTEGER DEFAULT 0,
    status VARCHAR(255) DEFAULT 'PENDING_DROP_OFF',
    preferred_dropoff_date DATE,
    seller_note VARCHAR(1600),
    admin_note VARCHAR(1600),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Final operational migration appended for attachments/reviews/escrow
VALUES
('TC-MNL','TrustCart Manila Warehouse','Manila','Metro Manila','Pandacan / Manila logistics partner hub',14.5995,120.9842,TRUE),
('TC-QC','TrustCart Quezon City Warehouse','Quezon City','Metro Manila','North Metro Manila partner hub',14.6760,121.0437,TRUE),
('TC-MKT','TrustCart Makati Warehouse','Makati','Metro Manila','Central business district fulfillment hub',14.5547,121.0244,TRUE),
('TC-PAS','TrustCart Pasig Warehouse','Pasig','Metro Manila','Ortigas / Pasig partner fulfillment hub',14.5764,121.0851,TRUE),
('TC-SP','TrustCart San Pedro Warehouse','San Pedro','Laguna','San Pedro Laguna local fulfillment hub',14.3440,121.0576,TRUE),
('TC-CAL','TrustCart Calamba Warehouse','Calamba','Laguna','Calamba Laguna south hub',14.2117,121.1653,TRUE),
('TC-IMS','TrustCart Imus Warehouse','Imus','Cavite','Imus Cavite partner warehouse',14.3860,120.9368,TRUE),
('TC-DAS','TrustCart Dasmariñas Warehouse','Dasmariñas','Cavite','Dasmariñas Cavite local hub',14.3294,120.9367,TRUE),
('TC-CEB','TrustCart Cebu City Warehouse','Cebu City','Cebu','Visayas fulfillment warehouse',10.3157,123.8854,TRUE),
('TC-DVO','TrustCart Davao Warehouse','Davao City','Davao del Sur','Mindanao fulfillment warehouse',7.1907,125.4553,TRUE),
('TC-BAG','TrustCart Baguio Warehouse','Baguio','Benguet','Northern Luzon fulfillment partner hub',16.4023,120.5960,TRUE),
('TC-ILO','TrustCart Iloilo Warehouse','Iloilo City','Iloilo','Western Visayas fulfillment partner hub',10.7202,122.5621,TRUE)
ON CONFLICT (code) DO NOTHING;

-- Final 100/100 operational updates: attachments, reviews, escrow tracking, and exports
ALTER TABLE IF EXISTS chat_message ADD COLUMN IF NOT EXISTS attachment_url VARCHAR(1200);
ALTER TABLE IF EXISTS chat_message ADD COLUMN IF NOT EXISTS attachment_name VARCHAR(500);
ALTER TABLE IF EXISTS support_ticket ADD COLUMN IF NOT EXISTS attachment_url VARCHAR(1200);
ALTER TABLE IF EXISTS support_ticket ADD COLUMN IF NOT EXISTS attachment_name VARCHAR(500);
ALTER TABLE IF EXISTS customer_order ADD COLUMN IF NOT EXISTS escrow_status VARCHAR(500) DEFAULT 'TRUSTCART_PROTECTED';
ALTER TABLE IF EXISTS customer_order ADD COLUMN IF NOT EXISTS payment_reference VARCHAR(255);
ALTER TABLE IF EXISTS customer_order ADD COLUMN IF NOT EXISTS tracking_stage VARCHAR(255) DEFAULT 'Order Placed';
ALTER TABLE IF EXISTS customer_order ADD COLUMN IF NOT EXISTS tracking_note VARCHAR(1200) DEFAULT 'Your order is protected by TrustCart.';
ALTER TABLE IF EXISTS customer_order ADD COLUMN IF NOT EXISTS estimated_delivery_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS product_review (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    customer_order_id BIGINT,
    buyer_id BIGINT,
    seller_id BIGINT,
    rating INTEGER DEFAULT 5,
    review_text VARCHAR(2500),
    verified_purchase BOOLEAN DEFAULT TRUE,
    attachment_url VARCHAR(1200),
    attachment_name VARCHAR(500),
    status VARCHAR(255) DEFAULT 'PUBLISHED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
