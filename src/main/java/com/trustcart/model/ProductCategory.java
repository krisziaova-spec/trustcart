package com.trustcart.model;

import java.util.Arrays;
import java.util.Comparator;

public enum ProductCategory {
    ELECTRONICS("Electronics & Gadgets", "💻", true),
    MOBILE_ACCESSORIES("Electronics & Gadgets", "📱", false),
    FASHION("Clothing & Fashion", "👕", true),
    BEAUTY_PERSONAL_CARE("Beauty & Personal Care", "💄", true),
    HOME_LIVING("Home, Cleaning & Living", "🏠", true),
    GROCERIES("Groceries & Daily Essentials", "🛒", true),
    PREPARED_FOOD("Ready-to-Eat & Pre-Order Meals", "🍱", true),
    // Legacy/plural value kept so existing production rows do not crash Hibernate enum loading.
    PREPARED_FOODS("Ready-to-Eat & Pre-Order Meals", "🍱", false),

    // Legacy daily-needs labels kept for database compatibility, but hidden from public navigation.
    FMCG("Groceries & Daily Essentials", "🛍️", false),
    CONVENIENCE_GOODS("Groceries & Daily Essentials", "🏪", false),
    CONSUMER_STAPLES("Groceries & Daily Essentials", "📦", false),
    EVERYDAY_ESSENTIALS("Groceries & Daily Essentials", "🧺", false),
    DAILY_NECESSITIES("Groceries & Daily Essentials", "🧻", false),
    PACKAGED_GOODS("Groceries & Daily Essentials", "🥫", false),

    HEALTH_WELLNESS("Health & Wellness", "💚", true),
    BABY_KIDS("Baby & Kids", "🧸", true),
    SPORTS_OUTDOORS("Sports & Outdoors", "🏀", true),
    SCHOOL_OFFICE("School & Office Supplies", "📚", true),
    AUTOMOTIVE_MOTORCYCLE("Automotive & Motorcycle", "🏍️", true),
    PET_SUPPLIES("Pet Supplies", "🐾", true),
    SUSTAINABLE_PRODUCTS("Organic & Sustainable Products", "♻️", true),
    LOCAL_FILIPINO_PRODUCTS("Local Filipino Products", "🇵🇭", true);

    private final String displayName;
    private final String icon;
    private final boolean publicVisible;

    ProductCategory(String displayName, String icon, boolean publicVisible) {
        this.displayName = displayName;
        this.icon = icon;
        this.publicVisible = publicVisible;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isPublicVisible() {
        return publicVisible;
    }

    public static ProductCategory[] publicValues() {
        return Arrays.stream(values())
                .filter(ProductCategory::isPublicVisible)
                .sorted(Comparator.comparingInt(ProductCategory::getHomepagePriority))
                .toArray(ProductCategory[]::new);
    }

    public int getHomepagePriority() {
        return switch (this) {
            case GROCERIES -> 1;
            case PREPARED_FOOD, PREPARED_FOODS -> 2;
            case SUSTAINABLE_PRODUCTS -> 3;
            case HOME_LIVING -> 4;
            case BEAUTY_PERSONAL_CARE -> 5;
            case BABY_KIDS -> 6;
            case LOCAL_FILIPINO_PRODUCTS -> 7;
            case HEALTH_WELLNESS -> 8;
            case PET_SUPPLIES -> 9;
            case SCHOOL_OFFICE -> 10;
            case FASHION -> 20;
            case ELECTRONICS, MOBILE_ACCESSORIES -> 21;
            case SPORTS_OUTDOORS -> 30;
            case AUTOMOTIVE_MOTORCYCLE -> 31;
            default -> 90;
        };
    }

    public boolean isPreparedFood() {
        return this == PREPARED_FOOD || this == PREPARED_FOODS;
    }

    public boolean isLegacyDailyNeedsLabel() {
        return this == FMCG || this == CONVENIENCE_GOODS || this == CONSUMER_STAPLES
                || this == EVERYDAY_ESSENTIALS || this == DAILY_NECESSITIES || this == PACKAGED_GOODS;
    }
}
