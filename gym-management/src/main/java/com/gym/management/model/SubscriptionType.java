package com.gym.management.model;

import java.math.BigDecimal;

public enum SubscriptionType {
    ONE_MONTH("1 Month", 1, new BigDecimal("49.99")),
    SIX_MONTHS("6 Months", 6, new BigDecimal("249.99")),
    STUDENT("Student", 1, new BigDecimal("29.99"));

    private final String label;
    private final int durationMonths;
    private final BigDecimal defaultPrice;

    SubscriptionType(String label, int durationMonths, BigDecimal defaultPrice) {
        this.label = label;
        this.durationMonths = durationMonths;
        this.defaultPrice = defaultPrice;
    }

    public String getLabel() {
        return label;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }
}
