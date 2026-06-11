package com.ssafy.home.stats.service;

import com.ssafy.home.stats.dto.StatsResponse;
import com.ssafy.home.stats.dto.StatsRow;
import com.ssafy.home.stats.mapper.StatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private static final long MANWON_TO_WON = 10_000L;

    private final StatsMapper statsMapper;

    public StatsResponse getStats() {
        StatsRow row = statsMapper.findStats();

        long todayCount    = row.getTodayDealCount();
        long yesterdayCount = row.getYesterdayDealCount();
        long avgSalePrice  = toWon(row.getAvgSalePriceThisMonthManwon());
        long avgLeasePrice = toWon(row.getAvgLeasePriceThisMonthManwon());

        double todayChange  = calculateChange(todayCount,  yesterdayCount);
        double saleChange   = calculateChange(avgSalePrice, toWon(row.getAvgSalePriceLastMonthManwon()));
        double leaseChange  = calculateChange(avgLeasePrice, toWon(row.getAvgLeasePriceLastMonthManwon()));

        return new StatsResponse(todayCount, todayChange, avgSalePrice, saleChange, avgLeasePrice, leaseChange);
    }

    private long toWon(Long manwon) {
        return manwon == null ? 0L : manwon * MANWON_TO_WON;
    }

    private double calculateChange(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return Math.round((double) (current - previous) / previous * 1000.0) / 10.0;
    }
}
