package com.trustcart.model;

public enum PaymentMethod {
    CASH_ON_DELIVERY("Cash on Delivery"),
    GCASH("GCash Payment"),
    MAYA("Maya Payment"),
    CARD("Credit/Debit Card"),
    BANK_TRANSFER("Bank Transfer");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
