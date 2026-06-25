package com.ssafy.home.batch.reader;

import com.ssafy.home.external.seoul.cctv.SeoulCctvApiException;
import com.ssafy.home.external.seoul.cctv.SeoulCctvClient;
import com.ssafy.home.external.seoul.cctv.SeoulCctvPage;
import com.ssafy.home.external.seoul.cctv.SeoulRawCctv;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

public class SeoulCctvReader implements ItemStreamReader<SeoulRawCctv> {

    private final SeoulCctvClient client;
    private final int pageSize;
    private final int retryCount;

    private int pageNumber = 1;
    private Iterator<SeoulRawCctv> buffer = List.<SeoulRawCctv>of().iterator();
    private boolean finished;

    public SeoulCctvReader(SeoulCctvClient client, int pageSize, int retryCount) {
        this.client = client;
        this.pageSize = pageSize;
        this.retryCount = retryCount;
    }

    @Override
    public SeoulRawCctv read() {
        while (!buffer.hasNext()) {
            if (finished) {
                return null;
            }
            loadNextPage();
        }
        return buffer.next();
    }

    private void loadNextPage() {
        SeoulCctvPage page = fetchWithRetry(pageNumber);
        buffer = page.rows().iterator();
        if (page.rows().isEmpty() || isLastPage(page)) {
            finished = true;
        } else {
            pageNumber++;
        }
    }

    private boolean isLastPage(SeoulCctvPage page) {
        return page.rows().size() < pageSize
                || pageNumber * pageSize >= page.totalCount();
    }

    private SeoulCctvPage fetchWithRetry(int page) {
        SeoulCctvApiException last = null;
        int attempts = retryCount + 1;
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return client.fetch(page);
            } catch (SeoulCctvApiException exception) {
                last = exception;
                if (!exception.retryable() || attempt == attempts - 1) {
                    throw new ItemStreamException(
                            "Failed to fetch Seoul CCTV dataset page " + page,
                            exception
                    );
                }
            }
        }
        throw new ItemStreamException("Failed to fetch Seoul CCTV dataset page " + page, last);
    }
}
