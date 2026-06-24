package com.ssafy.home.external.sdsc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;

/**
 * Stub implementation — replaced by full implementation in Task 3.
 */
public class SdscStoreClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final SdscProperties properties;

    public SdscStoreClient(RestClient restClient, ObjectMapper objectMapper, SdscProperties properties) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    /**
     * Stub fetch method — full implementation in Task 3.
     */
    public Object fetch(int page) {
        return null;
    }
}
