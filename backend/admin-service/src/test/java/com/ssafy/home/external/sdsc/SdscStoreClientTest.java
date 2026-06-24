package com.ssafy.home.external.sdsc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class SdscStoreClientTest {

    private MockRestServiceServer server;
    private SdscStoreClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        SdscProperties properties = new SdscProperties(
                URI.create("https://apis.data.go.kr/B553077/api/open/sdsc2/storeListInUpjong"),
                "TEST_KEY",
                10,
                Duration.ofSeconds(5),
                1,
                100
        );
        client = new SdscStoreClient(builder.build(), new ObjectMapper(), properties);
    }

    @Test
    @DisplayName("시군구 코드로 상가 목록을 조회한다")
    void fetchReturnsParsedStores() {
        server.expect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"currentCount":1,"matchCount":1,"page":1,"perPage":10,
                         "totalCount":1,
                         "data":[{"bizesId":"J1100000001","bizesNm":"테스트식당",
                                  "indsLclsNm":"음식","indsMclsNm":"한식","indsSclsNm":"한식음식점",
                                  "lat":"37.575","lon":"126.977",
                                  "rdnmAdr":"서울특별시 종로구 청운동 1-1","lnoAdr":null}]}
                        """, MediaType.APPLICATION_JSON));

        SdscStorePage page = client.fetch("11110", 1);

        assertThat(page.totalCount()).isEqualTo(1);
        assertThat(page.stores()).hasSize(1);
        SdscRawStore store = page.stores().get(0);
        assertThat(store.bizesId()).isEqualTo("J1100000001");
        assertThat(store.bizesNm()).isEqualTo("테스트식당");
        assertThat(store.lat()).isEqualTo("37.575");
    }

    @Test
    @DisplayName("5xx 응답은 retryable SdscApiException을 던진다")
    void fetchThrowsRetryableExceptionOnServerError() {
        server.expect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.fetch("11110", 1))
                .isInstanceOf(SdscApiException.class)
                .satisfies(e -> assertThat(((SdscApiException) e).retryable()).isTrue());
    }

    @Test
    @DisplayName("totalCount가 없으면 SdscApiException을 던진다")
    void fetchThrowsExceptionWhenTotalCountIsMissing() {
        server.expect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"currentCount":1,"matchCount":1,"page":1,"perPage":10,
                         "data":[]}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetch("11110", 1))
                .isInstanceOf(SdscApiException.class)
                .hasMessageContaining("totalCount");
    }

    @Test
    @DisplayName("totalCount 형식이 올바르지 않으면 SdscApiException을 던진다")
    void fetchThrowsExceptionWhenTotalCountIsInvalid() {
        server.expect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"currentCount":1,"matchCount":1,"page":1,"perPage":10,
                         "totalCount": "invalid_number",
                         "data":[]}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetch("11110", 1))
                .isInstanceOf(SdscApiException.class)
                .hasMessageContaining("totalCount");
    }

    @Test
    @DisplayName("data가 없으면 SdscApiException을 던진다")
    void fetchThrowsExceptionWhenDataIsMissing() {
        server.expect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"currentCount":1,"matchCount":1,"page":1,"perPage":10,
                         "totalCount":1}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetch("11110", 1))
                .isInstanceOf(SdscApiException.class)
                .hasMessageContaining("data");
    }

    @Test
    @DisplayName("data가 배열이 아니면 SdscApiException을 던진다")
    void fetchThrowsExceptionWhenDataIsNotArray() {
        server.expect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"currentCount":1,"matchCount":1,"page":1,"perPage":10,
                         "totalCount":1,
                         "data": "not_an_array"}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetch("11110", 1))
                .isInstanceOf(SdscApiException.class)
                .hasMessageContaining("data");
    }
}
