package com.ssafy.home.external.seoul;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class SeoulEnvironmentClientTest {

    private MockRestServiceServer server;
    private SeoulEnvironmentClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        SeoulEnvironmentProperties properties = new SeoulEnvironmentProperties(
                URI.create("http://openapi.seoul.go.kr:8088"),
                "TEST_KEY",
                10,
                Duration.ofSeconds(5),
                1,
                100,
                List.of(new SeoulEnvironmentProperties.Dataset(
                        "air",
                        "대기 배출",
                        "airEmission",
                        "사업장명",
                        "배출량",
                        "톤",
                        "점검일",
                        "위도",
                        "경도"
                ))
        );
        client = new SeoulEnvironmentClient(builder.build(), new ObjectMapper(), properties);
    }

    @Test
    @DisplayName("서울 열린데이터 환경 데이터를 조회하고 필드 매핑대로 파싱한다")
    void fetchReturnsParsedEnvironmentRows() {
        server.expect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "airEmission": {
                            "list_total_count": 1,
                            "RESULT": {"CODE": "INFO-000", "MESSAGE": "정상 처리되었습니다"},
                            "row": [
                              {
                                "사업장명": "테스트 배출시설",
                                "배출량": "12.3400",
                                "톤": "kg",
                                "점검일": "2026-05-30",
                                "위도": "37.5665000",
                                "경도": "126.9780000"
                              }
                            ]
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        SeoulEnvironmentPage page = client.fetch("air", 1);

        assertThat(page.totalCount()).isEqualTo(1);
        assertThat(page.rows()).hasSize(1);
        SeoulRawEnvironment row = page.rows().get(0);
        assertThat(row.datasetKey()).isEqualTo("air");
        assertThat(row.category()).isEqualTo("대기 배출");
        assertThat(row.itemName()).isEqualTo("테스트 배출시설");
        assertThat(row.value()).isEqualTo("12.3400");
        assertThat(row.unit()).isEqualTo("kg");
        assertThat(row.measuredDate()).isEqualTo("2026-05-30");
        assertThat(row.latitude()).isEqualTo("37.5665000");
        assertThat(row.longitude()).isEqualTo("126.9780000");
    }

    @Test
    @DisplayName("5xx 응답은 retryable SeoulEnvironmentApiException을 던진다")
    void fetchThrowsRetryableExceptionOnServerError() {
        server.expect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.fetch("air", 1))
                .isInstanceOf(SeoulEnvironmentApiException.class)
                .satisfies(e -> assertThat(((SeoulEnvironmentApiException) e).retryable()).isTrue());
    }
}
