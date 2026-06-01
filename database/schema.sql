-- TrustCart PostgreSQL Schema and seed data
-- Compatible with Supabase PostgreSQL and Spring Boot JPA.
-- This file resets and loads the buyer-seller live version.

DROP TABLE IF EXISTS autoship_subscription CASCADE;
DROP TABLE IF EXISTS discount_code CASCADE;
DROP TABLE IF EXISTS refund_request CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS customer_orders CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS seller CASCADE;
DROP TABLE IF EXISTS buyer_account CASCADE;

CREATE TABLE buyer_account (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    default_address VARCHAR(1000),
    password VARCHAR(255),
    preferred_city VARCHAR(255),
    preferred_latitude DOUBLE PRECISION,
    preferred_longitude DOUBLE PRECISION,
    preferred_radius_km INTEGER DEFAULT 5,
    nearby_seller_first BOOLEAN NOT NULL DEFAULT TRUE,
    pickup_interested BOOLEAN NOT NULL DEFAULT FALSE,
    loyalty_points_balance INTEGER DEFAULT 0,
    lifetime_loyalty_points INTEGER DEFAULT 0,
    lifetime_spend NUMERIC(12,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE seller (
    id BIGSERIAL PRIMARY KEY,
    store_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50),
    password VARCHAR(255),
    business_type VARCHAR(255),
    sustainability_badge VARCHAR(255),
    reliability_score INTEGER NOT NULL DEFAULT 0,
    response_rate_score INTEGER NOT NULL DEFAULT 0,
    complaint_rate_score INTEGER NOT NULL DEFAULT 0,
    return_rate_score INTEGER NOT NULL DEFAULT 0,
    green_compliance_score INTEGER NOT NULL DEFAULT 0,
    business_verified BOOLEAN NOT NULL DEFAULT FALSE,
    identity_verified BOOLEAN NOT NULL DEFAULT FALSE,
    document_verified BOOLEAN NOT NULL DEFAULT FALSE,
    product_compliance_checked BOOLEAN NOT NULL DEFAULT FALSE,
    invited_or_approved_only BOOLEAN NOT NULL DEFAULT TRUE,
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
    pickup_available BOOLEAN NOT NULL DEFAULT TRUE,
    store_location_verified BOOLEAN NOT NULL DEFAULT FALSE,
    location_proof_url VARCHAR(1000),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP
);

CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1400),
    category VARCHAR(80),
    price NUMERIC(12,2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    eco_friendly BOOLEAN NOT NULL DEFAULT FALSE,
    sustainability_tag VARCHAR(255),
    trust_cart_shield BOOLEAN NOT NULL DEFAULT TRUE,
    authentic_item_checked BOOLEAN NOT NULL DEFAULT TRUE,
    verified_reviews_only BOOLEAN NOT NULL DEFAULT TRUE,
    suspicious_review_flag BOOLEAN NOT NULL DEFAULT FALSE,
    plastic_free_packaging BOOLEAN NOT NULL DEFAULT FALSE,
    locally_sourced BOOLEAN NOT NULL DEFAULT FALSE,
    low_waste_delivery BOOLEAN NOT NULL DEFAULT FALSE,
    trust_score INTEGER NOT NULL DEFAULT 0,
    green_score INTEGER NOT NULL DEFAULT 0,
    seller_verification_score INTEGER NOT NULL DEFAULT 0,
    product_authenticity_score INTEGER NOT NULL DEFAULT 0,
    review_quality_score INTEGER NOT NULL DEFAULT 0,
    delivery_reliability_score INTEGER NOT NULL DEFAULT 0,
    sustainability_score INTEGER NOT NULL DEFAULT 0,
    return_risk_score INTEGER NOT NULL DEFAULT 0,
    review_summary VARCHAR(1400),
    red_flag_summary VARCHAR(1200),
    image_url VARCHAR(1200),
    product_origin VARCHAR(255),
    warranty_policy VARCHAR(255),
    subscription_eligible BOOLEAN NOT NULL DEFAULT FALSE,
    subscription_discount_percent INTEGER DEFAULT 5,
    photo_alt_text VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    seller_id BIGINT NOT NULL REFERENCES seller(id)
);

CREATE TABLE customer_orders (
    id BIGSERIAL PRIMARY KEY,
    order_code VARCHAR(255) UNIQUE,
    full_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    shipping_address VARCHAR(1000),
    payment_method VARCHAR(80),
    payment_status VARCHAR(100),
    order_status VARCHAR(80) NOT NULL DEFAULT 'PLACED',
    eco_packaging BOOLEAN NOT NULL DEFAULT FALSE,
    no_extra_plastic BOOLEAN NOT NULL DEFAULT FALSE,
    consolidated_delivery BOOLEAN NOT NULL DEFAULT FALSE,
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
    order_id BIGINT NOT NULL REFERENCES customer_orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id),
    product_name VARCHAR(255),
    seller_name VARCHAR(255),
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12,2),
    line_total NUMERIC(12,2)
);

CREATE TABLE refund_request (
    id BIGSERIAL PRIMARY KEY,
    order_code VARCHAR(255),
    email VARCHAR(255),
    reason VARCHAR(1500),
    evidence_url VARCHAR(255),
    status VARCHAR(80) NOT NULL DEFAULT 'SUBMITTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_id BIGINT REFERENCES customer_orders(id)
);

CREATE TABLE discount_code (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(80) UNIQUE NOT NULL,
    description VARCHAR(600),
    minimum_spend NUMERIC(12,2) DEFAULT 0,
    percent_off INTEGER DEFAULT 0,
    amount_off NUMERIC(12,2) DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    first_order_only BOOLEAN NOT NULL DEFAULT FALSE,
    subscription_boost BOOLEAN NOT NULL DEFAULT FALSE,
    max_redemptions INTEGER DEFAULT 0,
    times_redeemed INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    seller_id BIGINT,
    created_by_seller VARCHAR(255)
);

CREATE TABLE autoship_subscription (
    id BIGSERIAL PRIMARY KEY,
    buyer_id BIGINT NOT NULL REFERENCES buyer_account(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    frequency VARCHAR(80) NOT NULL DEFAULT 'MONTHLY',
    quantity INTEGER DEFAULT 1,
    recurring_price NUMERIC(12,2) DEFAULT 0,
    subscription_discount_percent INTEGER DEFAULT 5,
    next_shipment_date DATE,
    status VARCHAR(80) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    protection_note VARCHAR(1000)
);
