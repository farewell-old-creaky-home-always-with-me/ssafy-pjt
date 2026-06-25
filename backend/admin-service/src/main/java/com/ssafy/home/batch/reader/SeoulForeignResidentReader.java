package com.ssafy.home.batch.reader;

import com.ssafy.home.external.seoul.demographics.SeoulDemographicsApiException;
import com.ssafy.home.external.seoul.demographics.SeoulDemographicsClient;
import com.ssafy.home.external.seoul.demographics.SeoulDemographicsPage;
import com.ssafy.home.external.seoul.demographics.SeoulRawForeignResident;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

public class SeoulForeignResidentReader implements ItemStreamReader<SeoulRawForeignResident> {

    private final SeoulDemographicsClient client;
    private final int pageSize;
    private final int retryCount;

    private int pageNumber = 1;
    private Iterator<SeoulRawForeignResident> buffer = List.<SeoulRawForeignResident>of().iterator();
    private boolean finished;

    public SeoulForeignResidentReader(SeoulDemographicsClient client, int pageSize, int retryCount) {
        this.client = client;
        this.pageSize = pageSize;
        this.retryCount = retryCount;
    }

    @Override
    public SeoulRawForeignResident read() {
        if (finished && !buffer.hasNext()) return null;
        while (!buffer.hasNext()) {
            loadNextPage();
            if (finished && !buffer.hasNext()) return null;
        }
        return buffer.next();
    }

    private void loadNextPage() {
        SeoulDemographicsPage<SeoulRawForeignResident> page = fetchWithRetry(pageNumber);
        buffer = page.rows().iterator();
        if (page.rows().isEmpty() || pageNumber * pageSize >= page.totalCount()) {
            finished = true;
        } else {
            pageNumber++;
        }
    }

    private SeoulDemographicsPage<SeoulRawForeignResident> fetchWithRetry(int page) {
        SeoulDemographicsApiException last = null;
        int attempts = retryCount + 1;
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return client.fetchForeignResident(page);
            } catch (SeoulDemographicsApiException e) {
                last = e;
                if (!e.retryable() || attempt == attempts - 1) {
                    throw new ItemStreamException("Failed to fetch foreign resident page " + page, e);
                }
            }
        }
        throw new ItemStreamException("Failed to fetch foreign resident page " + page, last);
    }
}
