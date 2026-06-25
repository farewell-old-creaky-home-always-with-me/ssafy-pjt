package com.ssafy.home.batch.domain;

public enum HousingNewsCategory {
    GENERAL,
    MARKET,
    POLICY,
    DEVELOPMENT,
    SUBSCRIPTION;

    public static HousingNewsCategory from(String value) {
        if (value == null || value.isBlank()) {
            return GENERAL;
        }
        try {
            return HousingNewsCategory.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return GENERAL;
        }
    }
}
