package com.trustcart.model;

public enum DeliveryOption {
    STANDARD_DELIVERY("Standard delivery"),
    ECO_CONSOLIDATED_DELIVERY("Eco consolidated delivery"),
    TRUSTCART_PICKUP_HUB("TrustCart pickup hub - seller address hidden");

    private final String displayName;

    DeliveryOption(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
