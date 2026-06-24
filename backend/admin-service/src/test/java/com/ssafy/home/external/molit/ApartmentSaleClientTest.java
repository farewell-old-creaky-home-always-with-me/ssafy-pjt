package com.ssafy.home.external.molit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class ApartmentSaleClientTest {

    @Test
    @DisplayName("국토부 한국어 필드명 응답에서 법정동 코드를 파싱한다")
    void fetchParsesKoreanFieldNames() {
        // Given
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();
        MolitProperties properties = new MolitProperties(
                URI.create("https://example.com/apartment"),
                URI.create("https://example.com/multi-family"),
                "service-key",
                100,
                Duration.ofSeconds(10),
                2,
                1000
        );
        ApartmentSaleClient client = new ApartmentSaleClient(
                restClient,
                new ObjectMapper(),
                properties
        );
        server.expect(requestTo("https://example.com/apartment"
                        + "?serviceKey=service-key&LAWD_CD=11110&DEAL_YMD=202601"
                        + "&pageNo=1&numOfRows=100&_type=json"))
                .andRespond(withSuccess(koreanFieldResponse(), MediaType.APPLICATION_JSON));

        // When
        MolitHouseDealPage page = client.fetch("11110", "202601", 1);

        // Then
        assertThat(page.totalCount()).isEqualTo(1);
        assertThat(page.items()).hasSize(1);
        MolitRawHouseDeal deal = page.items().get(0);
        assertThat(deal.legalDongCode()).isEqualTo("1111010100");
        assertThat(deal.lawdCode()).isEqualTo("11110");
        assertThat(deal.legalDongName()).isEqualTo("청운동");
        assertThat(deal.name()).isEqualTo("청운아파트");
        assertThat(deal.dealAmount()).isEqualTo("10,000");
        server.verify();
    }

    private String koreanFieldResponse() {
        return """
                {
                  "response": {
                    "header": {
                      "resultCode": "000",
                      "resultMsg": "OK"
                    },
                    "body": {
                      "items": {
                        "item": {
                          "법정동시군구코드": "11110",
                          "법정동읍면동코드": "10100",
                          "법정동": "청운동",
                          "아파트": "청운아파트",
                          "지번": "1-1",
                          "거래금액": "10,000",
                          "년": "2026",
                          "월": "1",
                          "일": "15",
                          "전용면적": "84.0",
                          "층": "10",
                          "건축년도": "2020"
                        }
                      },
                      "totalCount": 1
                    }
                  }
                }
                """;
    }
}
