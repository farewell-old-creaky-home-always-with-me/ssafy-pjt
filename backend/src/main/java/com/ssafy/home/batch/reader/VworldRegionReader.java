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
    private List<String> sigunguCodes = List.of();
    private int sigunguIndex;
    private int pageNumber = 1;
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
        if (sigunguIndex >= sigunguCodes.size()) {
            loadSigunguCodes();
            if (sigunguCodes.isEmpty()) {
                advanceSido();
                return;
            }
        }
        String sigunguCode = sigunguCodes.get(sigunguIndex);
        VworldRegionPage page = fetchWithRetry(sigunguCode, pageNumber);
        buffer = page.regions().iterator();
        if (page.regions().isEmpty() || isLastPage(page)) {
            sigunguIndex++;
            pageNumber = 1;
            if (sigunguIndex >= sigunguCodes.size()) {
                advanceSido();
            }
            return;
        }
        pageNumber++;
    }

    private void loadSigunguCodes() {
        String sidoCode = VworldSidoCodes.ALL.get(sidoIndex);
        sigunguCodes = fetchSigunguCodesWithRetry(sidoCode);
        sigunguIndex = 0;
        pageNumber = 1;
    }

    private void advanceSido() {
        sidoIndex++;
        sigunguCodes = List.of();
        sigunguIndex = 0;
        pageNumber = 1;
    }

    private boolean isLastPage(VworldRegionPage page) {
        return page.regions().size() < pageSize
                || pageNumber * pageSize >= page.totalCount();
    }

    private List<String> fetchSigunguCodesWithRetry(String sidoCode) {
        VworldApiException last = null;
        int attempts = sidoRetryCount + 1;
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return client.fetchSigunguCodes(sidoCode);
            } catch (VworldApiException exception) {
                last = exception;
                if (!exception.retryable() || attempt == attempts - 1) {
                    throw new ItemStreamException(
                            "Failed to fetch sigungu codes for sido " + sidoCode,
                            exception
                    );
                }
            }
        }
        throw new ItemStreamException(
                "Failed to fetch sigungu codes for sido " + sidoCode,
                last
        );
    }

    private VworldRegionPage fetchWithRetry(String sigunguCode, int page) {
        VworldApiException last = null;
        int attempts = sidoRetryCount + 1;
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return client.fetch(sigunguCode, page);
            } catch (VworldApiException exception) {
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
