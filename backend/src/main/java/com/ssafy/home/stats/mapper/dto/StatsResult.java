package com.ssafy.home.stats.mapper.dto;

import lombok.Data;

@Data
public class StatsResult {
    private long thisMonthDealCount;
    private long lastMonthDealCount;
    private Long avgSalePriceThisMonthManwon;
    private Long avgSalePriceLastMonthManwon;
    private Long avgLeasePriceThisMonthManwon;
    private Long avgLeasePriceLastMonthManwon;
}
