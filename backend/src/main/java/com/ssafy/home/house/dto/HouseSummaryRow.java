package com.ssafy.home.house.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HouseSummaryRow {

    private Long houseId;
    private String aptName;
    private String jibun;
    private Integer buildYear;
    private String houseType;
    private String latestDealType;
    private Integer latestDealAmount;
    private Integer latestDepositAmount;
    private Integer latestMonthlyRent;
    private LocalDate latestDealDate;
    private BigDecimal latestArea;
    private Integer latestFloor;

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public String getAptName() {
        return aptName;
    }

    public void setAptName(String aptName) {
        this.aptName = aptName;
    }

    public String getJibun() {
        return jibun;
    }

    public void setJibun(String jibun) {
        this.jibun = jibun;
    }

    public Integer getBuildYear() {
        return buildYear;
    }

    public void setBuildYear(Integer buildYear) {
        this.buildYear = buildYear;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getLatestDealType() {
        return latestDealType;
    }

    public void setLatestDealType(String latestDealType) {
        this.latestDealType = latestDealType;
    }

    public Integer getLatestDealAmount() {
        return latestDealAmount;
    }

    public void setLatestDealAmount(Integer latestDealAmount) {
        this.latestDealAmount = latestDealAmount;
    }

    public Integer getLatestDepositAmount() {
        return latestDepositAmount;
    }

    public void setLatestDepositAmount(Integer latestDepositAmount) {
        this.latestDepositAmount = latestDepositAmount;
    }

    public Integer getLatestMonthlyRent() {
        return latestMonthlyRent;
    }

    public void setLatestMonthlyRent(Integer latestMonthlyRent) {
        this.latestMonthlyRent = latestMonthlyRent;
    }

    public LocalDate getLatestDealDate() {
        return latestDealDate;
    }

    public void setLatestDealDate(LocalDate latestDealDate) {
        this.latestDealDate = latestDealDate;
    }

    public BigDecimal getLatestArea() {
        return latestArea;
    }

    public void setLatestArea(BigDecimal latestArea) {
        this.latestArea = latestArea;
    }

    public Integer getLatestFloor() {
        return latestFloor;
    }

    public void setLatestFloor(Integer latestFloor) {
        this.latestFloor = latestFloor;
    }
}
