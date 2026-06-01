package com.trustcart.model;

public enum ProductCategory {
    ELECTRONICS("Electronics"),
    MOBILE_ACCESSORIES("Mobile Accessories"),
    FASHION("Fashion"),
    BEAUTY_PERSONAL_CARE("Beauty & Personal Care"),
    HOME_LIVING("Home & Living"),
    GROCERIES("Groceries"),
    HEALTH_WELLNESS("Health & Wellness"),
    BABY_KIDS("Baby & Kids"),
    SPORTS_OUTDOORS("Sports & Outdoors"),
    SCHOOL_OFFICE("School & Office Supplies"),
    AUTOMOTIVE_MOTORCYCLE("Automotive & Motorcycle"),
    PET_SUPPLIES("Pet Supplies"),
    SUSTAINABLE_PRODUCTS("Sustainable Products"),
    LOCAL_FILIPINO_PRODUCTS("Local Filipino Products");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
