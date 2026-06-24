package com.ssafy.home.batch.reader;

import com.ssafy.home.external.molit.MolitApiException;
import com.ssafy.home.external.molit.MolitHouseDealClient;
import com.ssafy.home.external.molit.MolitHouseDealPage;
import com.ssafy.home.external.molit.MolitRawHouseDeal;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemReader;

public class MolitHouseDealReader implements ItemReader<MolitRawHouseDeal> {

    private final MolitHouseDealClient client;
    private final List<String> regionCodes;
    private final String yearMonth;
    private Iterator<MolitRawHouseDeal> items = List.<MolitRawHouseDeal>of().iterator();
    private int regionIndex;
    private int pageNumber = 1;
    private int emittedCount;
    private Integer totalCount;

    public MolitHouseDealReader(
            MolitHouseDealClient client,
            List<String> regionCodes,
            String yearMonth
    ) {
        this.client = client;
        this.regionCodes = List.copyOf(regionCodes);
        this.yearMonth = yearMonth;
    }

    @Override
    public MolitRawHouseDeal read() {
        while (!items.hasNext()) {
            if (regionIndex >= regionCodes.size()) {
                return null;
            }
            if (totalCount != null && emittedCount >= totalCount) {
                moveToNextRegion();
                continue;
            }
            MolitHouseDealPage page = client.fetch(currentRegionCode(), yearMonth, pageNumber);
            if (totalCount == null) {
                totalCount = page.totalCount();
            }
            items = page.items().iterator();
            if (!items.hasNext()) {
                if (emittedCount < totalCount) {
                    throw new MolitApiException(
                            "MOLIT returned an empty page before totalCount was reached",
                            null,
                            true
                    );
                }
                moveToNextRegion();
                continue;
            }
            pageNumber++;
        }
        emittedCount++;
        return items.next();
    }

    private String currentRegionCode() {
        return regionCodes.get(regionIndex);
    }

    private void moveToNextRegion() {
        regionIndex++;
        pageNumber = 1;
        emittedCount = 0;
        totalCount = null;
    }
}
