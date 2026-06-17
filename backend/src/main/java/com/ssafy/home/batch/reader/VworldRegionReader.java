package com.ssafy.home.batch.reader;

import com.ssafy.home.external.vworld.VworldApiException;
import com.ssafy.home.external.vworld.VworldLegalRegionClient;
import com.ssafy.home.external.vworld.VworldProperties;
import com.ssafy.home.external.vworld.VworldRawRegion;
import com.ssafy.home.external.vworld.VworldRegionPage;
import com.ssafy.home.external.vworld.VworldSidoCodes;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

public class VworldRegionReader implements ItemStreamReader<VworldRawRegion> {

    private final VworldLegalRegionClient client;
    private final int sidoRetryCount;
    private final int pageSize;
    private int sidoIndex;
    private int pageNumber;
    private Iterator<VworldRawRegion> buffer = List.<VworldRawRegion>of().iterator();
    private boolean finished;

    public VworldRegionReader(VworldLegalRegionClient client, VworldProperties properties) {
        this.client = client;
        this.sidoRetryCount = properties.sidoRetryCount();
        this.pageSize = properties.pageSize();
    }

    @Override
    public VworldRawRegion read() {
        if (finished) {
            return null;
        }
        while (!buffer.hasNext()) {
            if (sidoIndex >= VworldSidoCodes.ALL.size()) {
                finished = true;
                return null;
            }
            loadNextPage();
        }
        return buffer.next();
    }

    private void loadNextPage() {
        String sidoCode = VworldSidoCodes.ALL.get(sidoIndex);
        VworldRegionPage page = fetchWithSidoRetry(sidoCode, pageNumber);
        buffer = page.regions().iterator();
        if (page.regions().isEmpty() || isLastPage(page)) {
            sidoIndex++;
            pageNumber = 1;
            return;
        }
        pageNumber++;
    }

    private boolean isLastPage(VworldRegionPage page) {
        return page.regions().size() < pageSize
                || pageNumber * pageSize >= page.totalCount();
    }

    private VworldRegionPage fetchWithSidoRetry(String sidoCode, int page) {
        VworldApiException last = null;
        int attempts = sidoRetryCount + 1;
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return client.fetch(sidoCode, page);
            } catch (VworldApiException exception) {
                last = exception;
                if (!exception.retryable() || attempt == attempts - 1) {
                    throw new ItemStreamException(
                            "Failed to fetch sido " + sidoCode + " page " + page,
                            exception
                    );
                }
            }
        }
        throw new ItemStreamException(
                "Failed to fetch sido " + sidoCode + " page " + page,
                last
        );
    }
}
