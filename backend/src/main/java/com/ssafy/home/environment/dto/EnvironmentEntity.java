package com.ssafy.home.environment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EnvironmentEntity {

    private Long envId;
    private String itemName;
    private BigDecimal value;
    private String unit;
    private LocalDate measuredDate;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double distance;

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getMeasuredDate() {
        return measuredDate;
    }

    public void setMeasuredDate(LocalDate measuredDate) {
        this.measuredDate = measuredDate;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
