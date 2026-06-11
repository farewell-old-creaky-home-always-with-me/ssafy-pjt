package com.ssafy.home.stats.dto;

public record StatsResponse(
        long todayDealCount,
        double todayDealCountChange,
        long avgSalePrice,
        double avgSalePriceChange,
        long avgLeasePrice,
        double avgLeasePriceChange
) {
}
