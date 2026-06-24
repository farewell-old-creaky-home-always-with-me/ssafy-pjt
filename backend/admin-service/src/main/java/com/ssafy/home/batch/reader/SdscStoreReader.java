package com.ssafy.home.batch.reader;

import com.ssafy.home.external.sdsc.SdscApiException;
import com.ssafy.home.external.sdsc.SdscRawStore;
import com.ssafy.home.external.sdsc.SdscStorePage;
import com.ssafy.home.external.sdsc.SdscStoreClient;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

public class SdscStoreReader implements ItemStreamReader<SdscRawStore> {

    private final SdscStoreClient client;
    private final List<String> sigunguCodes;
    private final int pageSize;
    private final int retryCount;

    private int sigunguIndex;
    private int pageNumber = 1;
    private Iterator<SdscRawStore> buffer = List.<SdscRawStore>of().iterator();
    private boolean finished;

    public SdscStoreReader(
            SdscStoreClient client,
            List<String> sigunguCodes,
            int pageSize,
            int retryCount
    ) {
        this.client = client;
        this.sigunguCodes = sigunguCodes;
        this.pageSize = pageSize;
        this.retryCount = retryCount;
    }

    @Override
    public SdscRawStore read() {
        if (finished) {
            return null;
        }
        while (!buffer.hasNext()) {
            if (sigunguIndex >= sigunguCodes.size()) {
                finished = true;
                return null;
            }
            loadNextPage();
        }
        return buffer.next();
    }

    private void loadNextPage() {
        String sigunguCode = sigunguCodes.get(sigunguIndex);
        SdscStorePage page = fetchWithRetry(sigunguCode, pageNumber);
        buffer = page.stores().iterator();
        if (page.stores().isEmpty() || isLastPage(page)) {
            sigunguIndex++;
            pageNumber = 1;
        } else {
            pageNumber++;
        }
    }

    private boolean isLastPage(SdscStorePage page) {
        return page.stores().size() < pageSize
                || pageNumber * pageSize >= page.totalCount();
    }

    private SdscStorePage fetchWithRetry(String sigunguCode, int page) {
        SdscApiException last = null;
        int attempts = retryCount + 1;
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return client.fetch(sigunguCode, page);
            } catch (SdscApiException exception) {
                last = exception;
                if (!exception.retryable() || attempt == attempts - 1) {
                    throw new ItemStreamException(
                            "Failed to fetch sigungu " + sigunguCode + " page " + page,
                            exception
                    );
                }
            }
        }
        throw new ItemStreamException(
                "Failed to fetch sigungu " + sigunguCode + " page " + page,
                last
        );
    }
}
