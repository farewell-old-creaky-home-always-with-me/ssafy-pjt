package com.ssafy.home.batch.reader;

import com.ssafy.home.external.seoul.demographics.SeoulDemographicsApiException;
import com.ssafy.home.external.seoul.demographics.SeoulDemographicsClient;
import com.ssafy.home.external.seoul.demographics.SeoulDemographicsPage;
import com.ssafy.home.external.seoul.demographics.SeoulRawPopulation;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

public class SeoulPopulationReader implements ItemStreamReader<SeoulRawPopulation> {

    private final SeoulDemographicsClient client;
    private final int pageSize;
    private final int retryCount;

    private int pageNumber = 1;
    private Iterator<SeoulRawPopulation> buffer = List.<SeoulRawPopulation>of().iterator();
    private boolean finished;

    public SeoulPopulationReader(SeoulDemographicsClient client, int pageSize, int retryCount) {
        this.client = client;
        this.pageSize = pageSize;
        this.retryCount = retryCount;
    }

    @Override
    public SeoulRawPopulation read() {
        if (finished && !buffer.hasNext()) return null;
        while (!buffer.hasNext()) {
            loadNextPage();
            if (finished && !buffer.hasNext()) return null;
        }
        return buffer.next();
    }

    private void loadNextPage() {
        SeoulDemographicsPage<SeoulRawPopulation> page = fetchWithRetry(pageNumber);
        buffer = page.rows().iterator();
        if (page.rows().isEmpty() || pageNumber * pageSize >= page.totalCount()) {
            finished = true;
        } else {
            pageNumber++;
        }
    }

    private SeoulDemographicsPage<SeoulRawPopulation> fetchWithRetry(int page) {
        SeoulDemographicsApiException last = null;
        int attempts = retryCount + 1;
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return client.fetchPopulation(page);
            } catch (SeoulDemographicsApiException e) {
                last = e;
                if (!e.retryable() || attempt == attempts - 1) {
                    throw new ItemStreamException("Failed to fetch population page " + page, e);
                }
            }
        }
        throw new ItemStreamException("Failed to fetch population page " + page, last);
    }
}
