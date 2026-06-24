package com.ssafy.home.external.seoul;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SeoulEnvironmentPropertiesTest {

    @Test
    @DisplayName("서울 열린데이터 환경 API 기본 URL은 HTTPS만 허용한다")
    void rejectsHttpBaseUrl() {
        assertThatThrownBy(() -> new SeoulEnvironmentProperties(
                URI.create("http://openapi.seoul.go.kr:8088"),
                "TEST_KEY",
                1000,
                Duration.ofSeconds(15),
                2,
                500,
                List.of(dataset())
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTPS");
    }

    private SeoulEnvironmentProperties.Dataset dataset() {
        return new SeoulEnvironmentProperties.Dataset(
                "air",
                "대기 배출",
                "AirEmission",
                "ITEM_NAME",
                "VALUE",
                "UNIT",
                "MEASURED_DATE",
                "LAT",
                "LNG"
        );
    }
}
