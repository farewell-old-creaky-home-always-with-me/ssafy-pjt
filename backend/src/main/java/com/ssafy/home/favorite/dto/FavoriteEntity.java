package com.ssafy.home.favorite.dto;

import java.time.LocalDateTime;

public class FavoriteEntity {

    private Long favoriteId;
    private Long memberId;
    private String regionCode;
    private String sidoName;
    private String sigunguName;
    private String dongName;
    private LocalDateTime createdAt;

    public Long getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Long favoriteId) {
        this.favoriteId = favoriteId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getSidoName() {
        return sidoName;
    }

    public void setSidoName(String sidoName) {
        this.sidoName = sidoName;
    }

    public String getSigunguName() {
        return sigunguName;
    }

    public void setSigunguName(String sigunguName) {
        this.sigunguName = sigunguName;
    }

    public String getDongName() {
        return dongName;
    }

    public void setDongName(String dongName) {
        this.dongName = dongName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
