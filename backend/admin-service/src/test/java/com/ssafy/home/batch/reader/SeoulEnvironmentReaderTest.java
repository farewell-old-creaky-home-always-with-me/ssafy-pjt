package com.ssafy.home.batch.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.external.seoul.SeoulEnvironmentClient;
import com.ssafy.home.external.seoul.SeoulEnvironmentPage;
import com.ssafy.home.external.seoul.SeoulRawEnvironment;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeoulEnvironmentReaderTest {

    @Mock
    private SeoulEnvironmentClient client;

    @Test
    @DisplayName("설정된 데이터셋을 순서대로 읽는다")
    void readFetchesDatasetsInOrder() {
        SeoulRawEnvironment row1 = row("inspection");
        SeoulRawEnvironment row2 = row("green");
        given(client.fetch("inspection", 1))
                .willReturn(new SeoulEnvironmentPage(List.of(row1), 1));
        given(client.fetch("green", 1))
                .willReturn(new SeoulEnvironmentPage(List.of(row2), 1));
        SeoulEnvironmentReader reader = new SeoulEnvironmentReader(
                client,
                List.of("inspection", "green"),
                10,
                0
        );

        assertThat(reader.read().datasetKey()).isEqualTo("inspection");
        assertThat(reader.read().datasetKey()).isEqualTo("green");
        assertThat(reader.read()).isNull();
    }

    @Test
    @DisplayName("한 데이터셋에 여러 페이지가 있으면 순서대로 모두 읽는다")
    void readFetchesMultiplePagesForOneDataset() {
        SeoulRawEnvironment row1 = row("air");
        SeoulRawEnvironment row2 = row("air");
        given(client.fetch("air", 1))
                .willReturn(new SeoulEnvironmentPage(List.of(row1), 2));
        given(client.fetch("air", 2))
                .willReturn(new SeoulEnvironmentPage(List.of(row2), 2));
        SeoulEnvironmentReader reader = new SeoulEnvironmentReader(client, List.of("air"), 1, 0);

        assertThat(reader.read()).isSameAs(row1);
        assertThat(reader.read()).isSameAs(row2);
        assertThat(reader.read()).isNull();
    }

    private SeoulRawEnvironment row(String datasetKey) {
        return new SeoulRawEnvironment(
                datasetKey,
                "대기 배출",
                "테스트 항목",
                "1.0",
                "kg",
                "2026-05-30",
                "37.5665",
                "126.9780"
        );
    }
}
