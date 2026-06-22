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
    private final String regionCode;
    private final String yearMonth;
    private Iterator<MolitRawHouseDeal> items = List.<MolitRawHouseDeal>of().iterator();
    private int pageNumber = 1;
    private int emittedCount;
    private Integer totalCount;

    public MolitHouseDealReader(
            MolitHouseDealClient client,
            String regionCode,
            String yearMonth
    ) {
        this.client = client;
        this.regionCode = regionCode;
        this.yearMonth = yearMonth;
    }

    @Override
    public MolitRawHouseDeal read() {
        while (!items.hasNext()) {
            if (totalCount != null && emittedCount >= totalCount) {
                return null;
            }
            MolitHouseDealPage page = client.fetch(regionCode, yearMonth, pageNumber);
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
                pageNumber++;
                return null;
            }
            pageNumber++;
        }
        emittedCount++;
        return items.next();
    }
}
