package com.ssafy.home.batch.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.external.molit.MolitHouseDealClient;
import com.ssafy.home.external.molit.MolitHouseDealPage;
import com.ssafy.home.external.molit.MolitRawHouseDeal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MolitHouseDealReaderTest {

    @Mock
    private MolitHouseDealClient client;

    @Test
    @DisplayName("여러 지역코드를 순서대로 조회한다")
    void readFetchesMultipleRegionCodesInOrder() {
        // Given
        given(client.fetch("11110", "202605", 1))
                .willReturn(new MolitHouseDealPage(List.of(rawDeal("1111010100")), 1));
        given(client.fetch("11140", "202605", 1))
                .willReturn(new MolitHouseDealPage(List.of(rawDeal("1114010100")), 1));
        MolitHouseDealReader reader = new MolitHouseDealReader(
                client,
                List.of("11110", "11140"),
                "202605"
        );

        // When / Then
        assertThat(reader.read().legalDongCode()).isEqualTo("1111010100");
        assertThat(reader.read().legalDongCode()).isEqualTo("1114010100");
        assertThat(reader.read()).isNull();
    }

    private MolitRawHouseDeal rawDeal(String legalDongCode) {
        return new MolitRawHouseDeal(
                legalDongCode,
                "테스트아파트",
                "1-1",
                "10,000",
                "2026",
                "5",
                "1",
                "84.0",
                "10",
                "2020"
        );
    }
}
