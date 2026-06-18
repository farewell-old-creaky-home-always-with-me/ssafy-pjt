package com.ssafy.home.external.vworld;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;

class VworldLegalRegionClientTest {

    private MockRestServiceServer server;
    private VworldLegalRegionClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        VworldProperties properties = new VworldProperties(
                URI.create("http://localhost/vworld"),
                "test-key",
                "localhost",
                "LT_C_ADEMD_INFO",
                10,
                Duration.ofSeconds(5),
                0,
                1,
                1000
        );
        client = new VworldLegalRegionClient(
                builder.build(),
                new ObjectMapper(),
                properties
        );
    }

    @Test
    @DisplayName("읍면동 응답을 10자리 법정동코드로 파싱한다")
    void fetchParsesEmdFeatures() {
        // given
        String body = """
                {
                  "type": "FeatureCollection",
                  "totalFeatures": 1,
                  "features": [
                    {
                      "properties": {
                        "emd_cd": "11110101",
                        "emd_kor_nm": "청운동",
                        "full_nm": "서울특별시 종로구 청운동"
                      }
                    }
                  ]
                }
                """;
        server.expect(requestTo(allOf(
                        containsString("/req/wfs"),
                        containsString("TYPENAME=lt_c_ademd_info"),
                        containsString("11110")
                )))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // when
        VworldRegionPage page = client.fetch("11110", 1);

        // then
        assertThat(page.regions()).hasSize(1);
        assertThat(page.regions().get(0).regionCode()).isEqualTo("1111010100");
        assertThat(page.regions().get(0).sidoName()).isEqualTo("서울특별시");
        assertThat(page.regions().get(0).sigunguName()).isEqualTo("종로구");
        assertThat(page.regions().get(0).dongName()).isEqualTo("청운동");
        assertThat(page.totalCount()).isEqualTo(1);
        server.verify();
    }

    @Test
    @DisplayName("시군구 코드 목록을 조회한다")
    void fetchSigunguCodesParsesSigCd() {
        // given
        String body = """
                {
                  "response": {
                    "status": "OK",
                    "record": { "total": "1", "current": "1" },
                    "result": {
                      "featureCollection": {
                        "features": [
                          {
                            "properties": {
                              "sig_cd": "11110",
                              "sig_kor_nm": "종로구"
                            }
                          },
                          {
                            "properties": {
                              "sig_cd": "26110",
                              "sig_kor_nm": "중구"
                            }
                          }
                        ]
                      }
                    }
                  }
                }
                """;
        server.expect(requestTo(allOf(
                        containsString("data=LT_C_ADSIGG_INFO"),
                        containsString("geomFilter")
                )))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // when
        var sigunguCodes = client.fetchSigunguCodes("11");

        // then
        assertThat(sigunguCodes).containsExactly("11110");
        server.verify();
    }

    @Test
    @DisplayName("Data API 오류 응답은 예외로 변환한다")
    void fetchSigunguCodesThrowsOnErrorStatus() {
        // given
        String body = """
                {
                  "response": {
                    "status": "ERROR",
                    "error": { "text": "invalid key" }
                  }
                }
                """;
        server.expect(requestTo(org.hamcrest.Matchers.containsString("data=LT_C_ADSIGG_INFO")))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // when / then
        assertThatThrownBy(() -> client.fetchSigunguCodes("11"))
                .isInstanceOf(VworldApiException.class);
    }

    @Test
    @DisplayName("WFS features가 없으면 빈 페이지를 반환한다")
    void fetchReturnsEmptyPageWhenNoFeatures() {
        // given
        String body = """
                {
                  "type": "FeatureCollection",
                  "totalFeatures": 0,
                  "features": []
                }
                """;
        server.expect(requestTo(org.hamcrest.Matchers.containsString("/req/wfs")))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // when
        VworldRegionPage page = client.fetch("11110", 1);

        // then
        assertThat(page.regions()).isEmpty();
        assertThat(page.totalCount()).isZero();
    }

    @Test
    @DisplayName("Data API record.total이 없으면 예외를 던진다")
    void fetchSigunguCodesThrowsWhenTotalIsMissing() {
        // given
        String body = """
                {
                  "response": {
                    "status": "OK",
                    "result": {
                      "featureCollection": {
                        "features": []
                      }
                    }
                  }
                }
                """;
        server.expect(requestTo(org.hamcrest.Matchers.containsString("data=LT_C_ADSIGG_INFO")))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // when / then
        assertThatThrownBy(() -> client.fetchSigunguCodes("11"))
                .isInstanceOf(VworldApiException.class)
                .satisfies(throwable -> assertThat(((VworldApiException) throwable).retryable()).isFalse());
    }

    @Test
    @DisplayName("Data API record.total 형식이 잘못되면 재시도하지 않는 예외를 던진다")
    void fetchSigunguCodesThrowsWhenTotalFormatIsInvalid() {
        // given
        String body = """
                {
                  "response": {
                    "status": "OK",
                    "record": { "total": "not-a-number" },
                    "result": {
                      "featureCollection": {
                        "features": []
                      }
                    }
                  }
                }
                """;
        server.expect(requestTo(org.hamcrest.Matchers.containsString("data=LT_C_ADSIGG_INFO")))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // when / then
        assertThatThrownBy(() -> client.fetchSigunguCodes("11"))
                .isInstanceOf(VworldApiException.class)
                .satisfies(throwable -> assertThat(((VworldApiException) throwable).retryable()).isFalse());
    }
}
