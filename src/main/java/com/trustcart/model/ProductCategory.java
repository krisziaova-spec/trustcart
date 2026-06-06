package com.trustcart.model;

public enum ProductCategory {
    ELECTRONICS("Electronics", "💻"),
    MOBILE_ACCESSORIES("Mobile Accessories", "📱"),
    FASHION("Clothing & Fashion", "👕"),
    BEAUTY_PERSONAL_CARE("Beauty & Personal Care", "💄"),
    HOME_LIVING("Home & Living", "🏠"),
    GROCERIES("Groceries & Daily Essentials", "🛒"),

    // Kept for database/backward compatibility. These are now grouped under Groceries & Daily Essentials on the storefront.
    FMCG("Groceries & Daily Essentials", "🛒"),
    CONVENIENCE_GOODS("Groceries & Daily Essentials", "🛒"),
    CONSUMER_STAPLES("Groceries & Daily Essentials", "🛒"),
    EVERYDAY_ESSENTIALS("Groceries & Daily Essentials", "🛒"),
    DAILY_NECESSITIES("Groceries & Daily Essentials", "🛒"),
    PACKAGED_GOODS("Groceries & Daily Essentials", "🛒"),

    HEALTH_WELLNESS("Health & Wellness", "💚"),
    BABY_KIDS("Baby & Kids", "🧸"),
    SPORTS_OUTDOORS("Sports & Outdoors", "🏀"),
    SCHOOL_OFFICE("School & Office Supplies", "📚"),
    AUTOMOTIVE_MOTORCYCLE("Automotive & Motorcycle", "🏍️"),
    PET_SUPPLIES("Pet Supplies", "🐾"),
    SUSTAINABLE_PRODUCTS("Sustainable Products", "♻️"),
    LOCAL_FILIPINO_PRODUCTS("Local Filipino Products", "🇵🇭");

    private final String displayName;
    private final String icon;

    ProductCategory(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public static ProductCategory[] storefrontCategories() {
        return new ProductCategory[] {
                ELECTRONICS,
                MOBILE_ACCESSORIES,
                FASHION,
                BEAUTY_PERSONAL_CARE,
                HOME_LIVING,
                GROCERIES,
                HEALTH_WELLNESS,
                BABY_KIDS,
                SPORTS_OUTDOORS,
                SCHOOL_OFFICE,
                AUTOMOTIVE_MOTORCYCLE,
                PET_SUPPLIES,
                SUSTAINABLE_PRODUCTS,
                LOCAL_FILIPINO_PRODUCTS
        };
    }
}
