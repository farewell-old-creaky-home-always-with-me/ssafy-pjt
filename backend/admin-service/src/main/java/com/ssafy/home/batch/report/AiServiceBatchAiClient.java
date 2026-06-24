package com.ssafy.home.batch.report;

import com.ssafy.home.batch.report.dto.BatchAiReportRequest;
import com.ssafy.home.batch.report.dto.BatchAiReportResult;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AiServiceBatchAiClient implements BatchAiClient {

    private final RestClient restClient;

    public AiServiceBatchAiClient(BatchAiServiceProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.timeout());
        requestFactory.setReadTimeout(properties.timeout());
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl().toString())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public BatchAiReportResult createReport(BatchAiReportRequest request) {
        try {
            BatchAiReportResult response = restClient.post()
                    .uri("/api/ai/batch/reports/summary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(BatchAiReportResult.class);
            if (response == null) {
                throw new IllegalStateException("AI service returned empty report response");
            }
            return response;
        } catch (RestClientException exception) {
            throw new IllegalStateException("Failed to call AI service for batch report", exception);
        }
    }
}
