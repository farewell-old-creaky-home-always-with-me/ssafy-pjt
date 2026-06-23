package com.ssafy.home.stats.service;

import com.ssafy.home.stats.dto.StatsResponse;
import com.ssafy.home.stats.mapper.dto.StatsResult;
import com.ssafy.home.stats.mapper.StatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatsService {

    private static final long MANWON_TO_WON = 10_000L;

    private final StatsMapper statsMapper;

    @Transactional(readOnly = true)
    public StatsResponse getStats() {
        StatsResult row = statsMapper.findSummary();

        long thisMonthCount = row.getThisMonthDealCount();
        long lastMonthCount = row.getLastMonthDealCount();
        long avgSalePrice   = toWon(row.getAvgSalePriceThisMonthManwon());
        long avgLeasePrice  = toWon(row.getAvgLeasePriceThisMonthManwon());

        double thisMonthChange = calculateChange(thisMonthCount, lastMonthCount);
        double saleChange      = calculateChange(avgSalePrice, toWon(row.getAvgSalePriceLastMonthManwon()));
        double leaseChange     = calculateChange(avgLeasePrice, toWon(row.getAvgLeasePriceLastMonthManwon()));

        return StatsResponse.of(thisMonthCount, thisMonthChange, avgSalePrice, saleChange, avgLeasePrice, leaseChange);
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
