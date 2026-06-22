package com.ssafy.home.stats.dto;

public record StatsResponse(
        long todayDealCount,
        double todayDealCountChange,
        long avgSalePrice,
        double avgSalePriceChange,
        long avgLeasePrice,
        double avgLeasePriceChange
) {

    public static StatsResponse of(
            long todayCount,
            double todayChange,
            long avgSalePrice,
            double saleChange,
            long avgLeasePrice,
            double leaseChange
    ) {
        return new StatsResponse(todayCount, todayChange, avgSalePrice, saleChange, avgLeasePrice, leaseChange);
    }
}
