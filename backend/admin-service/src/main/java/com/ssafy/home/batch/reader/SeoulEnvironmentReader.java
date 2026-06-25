package com.ssafy.home.batch.reader;

import com.ssafy.home.external.seoul.SeoulEnvironmentApiException;
import com.ssafy.home.external.seoul.SeoulEnvironmentClient;
import com.ssafy.home.external.seoul.SeoulEnvironmentPage;
import com.ssafy.home.external.seoul.SeoulRawEnvironment;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

public class SeoulEnvironmentReader implements ItemStreamReader<SeoulRawEnvironment> {

    private final SeoulEnvironmentClient client;
    private final List<String> datasetKeys;
    private final int pageSize;
    private final int retryCount;

    private int datasetIndex;
    private int pageNumber = 1;
    private Iterator<SeoulRawEnvironment> buffer = List.<SeoulRawEnvironment>of().iterator();
    private boolean finished;

    public SeoulEnvironmentReader(
            SeoulEnvironmentClient client,
            List<String> datasetKeys,
            int pageSize,
            int retryCount
    ) {
        this.client = client;
        this.datasetKeys = datasetKeys;
        this.pageSize = pageSize;
        this.retryCount = retryCount;
    }

    @Override
    public SeoulRawEnvironment read() {
        if (finished) {
            return null;
        }
        while (!buffer.hasNext()) {
            if (datasetIndex >= datasetKeys.size()) {
                finished = true;
                return null;
            }
            loadNextPage();
        }
        return buffer.next();
    }

    private void loadNextPage() {
        String datasetKey = datasetKeys.get(datasetIndex);
        SeoulEnvironmentPage page = fetchWithRetry(datasetKey, pageNumber);
        buffer = page.rows().iterator();
        if (page.rows().isEmpty() || isLastPage(page)) {
            datasetIndex++;
            pageNumber = 1;
        } else {
            pageNumber++;
        }
    }

    private boolean isLastPage(SeoulEnvironmentPage page) {
        return page.rows().size() < pageSize
                || pageNumber * pageSize >= page.totalCount();
    }

    private SeoulEnvironmentPage fetchWithRetry(String datasetKey, int page) {
        SeoulEnvironmentApiException last = null;
        int attempts = retryCount + 1;
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return client.fetch(datasetKey, page);
            } catch (SeoulEnvironmentApiException exception) {
                last = exception;
                if (!exception.retryable() || attempt == attempts - 1) {
                    throw new ItemStreamException(
                            "Failed to fetch Seoul environment dataset " + datasetKey + " page " + page,
                            exception
                    );
                }
            }
        }
        throw new ItemStreamException(
                "Failed to fetch Seoul environment dataset " + datasetKey + " page " + page,
                last
        );
    }
}
