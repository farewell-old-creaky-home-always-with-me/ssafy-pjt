package com.ssafy.home.stats.mapper.dto;

import lombok.Data;

@Data
public class StatsResult {
    private long todayDealCount;
    private long yesterdayDealCount;
    private Long avgSalePriceThisMonthManwon;
    private Long avgSalePriceLastMonthManwon;
    private Long avgLeasePriceThisMonthManwon;
    private Long avgLeasePriceLastMonthManwon;
}
