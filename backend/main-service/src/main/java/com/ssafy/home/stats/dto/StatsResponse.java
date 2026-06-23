package com.ssafy.home.stats.dto;

public record StatsResponse(
        long thisMonthDealCount,
        double thisMonthDealCountChange,
        long avgSalePrice,
        double avgSalePriceChange,
        long avgLeasePrice,
        double avgLeasePriceChange
) {

    public static StatsResponse of(
            long thisMonthCount,
            double thisMonthChange,
            long avgSalePrice,
            double saleChange,
            long avgLeasePrice,
            double leaseChange
    ) {
        return new StatsResponse(thisMonthCount, thisMonthChange, avgSalePrice, saleChange, avgLeasePrice, leaseChange);
    }
}
