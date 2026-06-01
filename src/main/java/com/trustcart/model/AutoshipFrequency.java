package com.trustcart.model;

public enum AutoshipFrequency {
    EVERY_2_WEEKS("Every 2 weeks", 14),
    MONTHLY("Monthly", 30),
    EVERY_2_MONTHS("Every 2 months", 60),
    QUARTERLY("Quarterly", 90);

    private final String displayName;
    private final int daysBetween;

    AutoshipFrequency(String displayName, int daysBetween) {
        this.displayName = displayName;
        this.daysBetween = daysBetween;
    }

    public String getDisplayName() { return displayName; }
    public int getDaysBetween() { return daysBetween; }
}
