package com.ssafy.home.commercial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommercialEntity {

    private Long commercialId;
    private String bizName;
    private String categoryLarge;
    private String categoryMedium;
    private String categorySmall;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private Double distance;
    private LocalDateTime createdAt;

    public Long getCommercialId() {
        return commercialId;
    }

    public void setCommercialId(Long commercialId) {
        this.commercialId = commercialId;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getCategoryLarge() {
        return categoryLarge;
    }

    public void setCategoryLarge(String categoryLarge) {
        this.categoryLarge = categoryLarge;
    }

    public String getCategoryMedium() {
        return categoryMedium;
    }

    public void setCategoryMedium(String categoryMedium) {
        this.categoryMedium = categoryMedium;
    }

    public String getCategorySmall() {
        return categorySmall;
    }

    public void setCategorySmall(String categorySmall) {
        this.categorySmall = categorySmall;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
