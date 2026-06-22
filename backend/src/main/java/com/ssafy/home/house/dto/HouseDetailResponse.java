package com.ssafy.home.house.dto;

import com.ssafy.home.house.mapper.dto.HouseDealResult;
import com.ssafy.home.house.mapper.dto.HouseDetailResult;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record HouseDetailResponse(
        Long houseId,
        String aptName,
        String regionCode,
        String jibun,
        Integer buildYear,
        String houseType,
        BigDecimal latitude,
        BigDecimal longitude,
        List<HouseDealResponse> deals
) {
    public static HouseDetailResponse from(HouseDetailResult house, List<HouseDealResponse> deals) {
        return new HouseDetailResponse(
                house.getHouseId(),
                house.getAptName(),
                house.getRegionCode(),
                house.getJibun(),
                house.getBuildYear(),
                house.getHouseType(),
                house.getLatitude(),
                house.getLongitude(),
                deals
        );
    }

    public record HouseDealResponse(
            Long dealId,
            String dealType,
            Integer dealAmount,
            Integer depositAmount,
            Integer monthlyRent,
            LocalDate dealDate,
            BigDecimal area,
            Integer floor
    ) {
        public static HouseDealResponse from(HouseDealResult row) {
            return new HouseDealResponse(
                    row.getDealId(),
                    row.getDealType(),
                    row.getDealAmount(),
                    row.getDepositAmount(),
                    row.getMonthlyRent(),
                    row.getDealDate(),
                    row.getArea(),
                    row.getFloor()
            );
        }
    }
}
