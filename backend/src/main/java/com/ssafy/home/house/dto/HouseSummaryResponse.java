package com.ssafy.home.house.dto;

import com.ssafy.home.house.mapper.dto.HouseSummaryResult;
import java.math.BigDecimal;
import java.time.LocalDate;

public record HouseSummaryResponse(
        Long houseId,
        String aptName,
        String jibun,
        Integer buildYear,
        String houseType,
        LatestDealResponse latestDeal
) {
    public static HouseSummaryResponse from(HouseSummaryResult row) {
        return new HouseSummaryResponse(
                row.getHouseId(),
                row.getAptName(),
                row.getJibun(),
                row.getBuildYear(),
                row.getHouseType(),
                LatestDealResponse.from(row)
        );
    }

    public record LatestDealResponse(
            String dealType,
            Integer dealAmount,
            Integer depositAmount,
            Integer monthlyRent,
            LocalDate dealDate,
            BigDecimal area,
            Integer floor
    ) {
        public static LatestDealResponse from(HouseSummaryResult row) {
            return new LatestDealResponse(
                    row.getLatestDealType(),
                    row.getLatestDealAmount(),
                    row.getLatestDepositAmount(),
                    row.getLatestMonthlyRent(),
                    row.getLatestDealDate(),
                    row.getLatestArea(),
                    row.getLatestFloor()
            );
        }
    }
}
