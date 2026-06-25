package com.ssafy.home.batch.domain;

public enum HousingInfoType {
    POLICY,
    LIVING,
    MARKET;

    public static HousingInfoType from(String value) {
        if (value == null || value.isBlank()) {
            return POLICY;
        }
        try {
            return HousingInfoType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return POLICY;
        }
    }
}
