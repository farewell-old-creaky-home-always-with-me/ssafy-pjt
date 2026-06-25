package com.ssafy.home.external.seoul.demographics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class SeoulDemographicsClientTest {

    private SeoulDemographicsClient client;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        SeoulDemographicsProperties properties = new SeoulDemographicsProperties(
                URI.create("https://openapi.seoul.go.kr:8088"),
                "test-key",
                2,
                Duration.ofSeconds(15),
                1,
                10,
                new SeoulDemographicsProperties.Population(
                        "SPOP_PEOPLE_INGU_JACHI",
                        "SIDO_NM", "SIGNGU_NM", "ADSTRD_NM",
                        "PPLTN_CNT", "HOUSE_CNT", "AGRDE_65_ABOVE_PPLTN_CNT", "STDR_DE_ID"),
                new SeoulDemographicsProperties.ForeignResident(
                        "SPOP_FRNR_RNTS",
                        "SIDO_NM", "SIGNGU_NM", "ADSTRD_NM",
                        "FRNR_CNT", "STDR_DE_ID")
        );
        client = new SeoulDemographicsClient(builder.build(), new ObjectMapper(), properties);
    }

    @Test
    @DisplayName("인구통계 데이터를 정상 조회한다")
    void fetchPopulationReturnsRows() {
        server.expect(requestTo(
                "https://openapi.seoul.go.kr:8088/test-key/json/SPOP_PEOPLE_INGU_JACHI/1/2"))
                .andRespond(withSuccess(populationJson(), MediaType.APPLICATION_JSON));

        SeoulDemographicsPage<SeoulRawPopulation> page = client.fetchPopulation(1);

        assertThat(page.totalCount()).isEqualTo(1);
        assertThat(page.rows()).hasSize(1);
        SeoulRawPopulation row = page.rows().get(0);
        assertThat(row.sidoName()).isEqualTo("서울특별시");
        assertThat(row.sigunguName()).isEqualTo("강남구");
        assertThat(row.dongName()).isEqualTo("역삼1동");
        assertThat(row.totalPopulation()).isEqualTo("12345");
        assertThat(row.referenceDate()).isEqualTo("202505");
    }

    @Test
    @DisplayName("외국인통계 데이터를 정상 조회한다")
    void fetchForeignResidentReturnsRows() {
        server.expect(requestTo(
                "https://openapi.seoul.go.kr:8088/test-key/json/SPOP_FRNR_RNTS/1/2"))
                .andRespond(withSuccess(foreignResidentJson(), MediaType.APPLICATION_JSON));

        SeoulDemographicsPage<SeoulRawForeignResident> page = client.fetchForeignResident(1);

        assertThat(page.totalCount()).isEqualTo(1);
        assertThat(page.rows()).hasSize(1);
        assertThat(page.rows().get(0).foreignCount()).isEqualTo("345");
    }

    @Test
    @DisplayName("5xx 응답이면 retryable SeoulDemographicsApiException을 던진다")
    void fetchPopulationThrowsRetryableOn5xx() {
        server.expect(requestTo(
                "https://openapi.seoul.go.kr:8088/test-key/json/SPOP_PEOPLE_INGU_JACHI/1/2"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> client.fetchPopulation(1))
                .isInstanceOf(SeoulDemographicsApiException.class)
                .satisfies(e -> assertThat(((SeoulDemographicsApiException) e).retryable()).isTrue());
    }

    private String populationJson() {
        return """
                {
                  "SPOP_PEOPLE_INGU_JACHI": {
                    "list_total_count": 1,
                    "RESULT": {"CODE": "INFO-000", "MESSAGE": "정상"},
                    "row": [{
                      "SIDO_NM": "서울특별시",
                      "SIGNGU_NM": "강남구",
                      "ADSTRD_NM": "역삼1동",
                      "PPLTN_CNT": "12345",
                      "HOUSE_CNT": "5678",
                      "AGRDE_65_ABOVE_PPLTN_CNT": "1234",
                      "STDR_DE_ID": "202505"
                    }]
                  }
                }
                """;
    }

    private String foreignResidentJson() {
        return """
                {
                  "SPOP_FRNR_RNTS": {
                    "list_total_count": 1,
                    "RESULT": {"CODE": "INFO-000", "MESSAGE": "정상"},
                    "row": [{
                      "SIDO_NM": "서울특별시",
                      "SIGNGU_NM": "강남구",
                      "ADSTRD_NM": "역삼1동",
                      "FRNR_CNT": "345",
                      "STDR_DE_ID": "202505"
                    }]
                  }
                }
                """;
    }
}
