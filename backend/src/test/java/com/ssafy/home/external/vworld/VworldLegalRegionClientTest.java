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
import org.springframework.test.web.client.match.MockRestRequestMatchers;
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
                "LT_C_ADLEGAL_EMD",
                10,
                Duration.ofSeconds(5),
                0,
                1
        );
        client = new VworldLegalRegionClient(
                builder.build(),
                new ObjectMapper(),
                properties
        );
    }

    @Test
    @DisplayName("VWorld 응답을 파싱한다")
    void fetchParsesFeatures() {
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
                              "bjcd": "1111010100",
                              "sido_nm": "서울특별시",
                              "sgg_nm": "종로구",
                              "emd_nm": "청운동",
                              "abol_en": "N"
                            }
                          }
                        ]
                      }
                    }
                  }
                }
                """;
        server.expect(request -> request.getURI().getQuery().contains("page=1"))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // when
        VworldRegionPage page = client.fetch("11", 1);

        // then
        assertThat(page.regions()).hasSize(1);
        assertThat(page.regions().get(0).regionCode()).isEqualTo("1111010100");
        assertThat(page.totalCount()).isEqualTo(1);
        server.verify();
    }

    @Test
    @DisplayName("API 오류 응답은 예외로 변환한다")
    void fetchThrowsOnErrorStatus() {
        // given
        String body = """
                {
                  "response": {
                    "status": "ERROR",
                    "error": { "text": "invalid key" }
                  }
                }
                """;
        server.expect(MockRestRequestMatchers.requestTo(org.hamcrest.Matchers.containsString("/vworld")))
                .andRespond(MockRestResponseCreators.withSuccess(body, MediaType.APPLICATION_JSON));

        // when / then
        assertThatThrownBy(() -> client.fetch("11", 1))
                .isInstanceOf(VworldApiException.class);
    }
}
