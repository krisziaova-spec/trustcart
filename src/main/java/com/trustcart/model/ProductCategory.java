package com.trustcart.model;

public enum ProductCategory {
    ELECTRONICS("Electronics", "💻"),
    MOBILE_ACCESSORIES("Mobile Accessories", "📱"),
    FASHION("Fashion", "👕"),
    BEAUTY_PERSONAL_CARE("Beauty & Personal Care", "💄"),
    HOME_LIVING("Home & Living", "🏠"),
    GROCERIES("Groceries", "🛒"),

    FMCG("Fast-Moving Consumer Goods", "🛍️"),
    CONVENIENCE_GOODS("Convenience Goods", "🏪"),
    CONSUMER_STAPLES("Consumer Staples", "📦"),
    EVERYDAY_ESSENTIALS("Everyday Essentials", "🧺"),
    DAILY_NECESSITIES("Daily Necessities", "🧻"),
    PACKAGED_GOODS("Packaged Goods", "🥫"),

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
}
