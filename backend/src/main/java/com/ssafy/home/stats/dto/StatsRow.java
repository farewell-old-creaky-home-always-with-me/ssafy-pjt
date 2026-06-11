package com.ssafy.home.stats.dto;

import lombok.Data;

@Data
public class StatsRow {
    private long todayDealCount;
    private long yesterdayDealCount;
    private Long avgSalePriceThisMonthManwon;
    private Long avgSalePriceLastMonthManwon;
    private Long avgLeasePriceThisMonthManwon;
    private Long avgLeasePriceLastMonthManwon;
}
