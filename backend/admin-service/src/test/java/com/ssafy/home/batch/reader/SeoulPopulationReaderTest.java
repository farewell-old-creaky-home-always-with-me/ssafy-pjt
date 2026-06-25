package com.ssafy.home.batch.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.external.seoul.demographics.SeoulDemographicsClient;
import com.ssafy.home.external.seoul.demographics.SeoulDemographicsPage;
import com.ssafy.home.external.seoul.demographics.SeoulRawPopulation;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeoulPopulationReaderTest {

    @Mock
    private SeoulDemographicsClient client;

    @Test
    @DisplayName("첫 번째 페이지를 읽고 null을 반환한다")
    void readReturnsSinglePageThenNull() {
        given(client.fetchPopulation(1))
                .willReturn(new SeoulDemographicsPage<>(List.of(rawRow()), 1));

        SeoulPopulationReader reader = new SeoulPopulationReader(client, 10, 0);

        assertThat(reader.read()).isNotNull();
        assertThat(reader.read()).isNull();
    }

    @Test
    @DisplayName("여러 페이지를 순서대로 읽는다")
    void readFetchesMultiplePages() {
        SeoulRawPopulation row1 = rawRow();
        SeoulRawPopulation row2 = rawRow();
        given(client.fetchPopulation(1)).willReturn(new SeoulDemographicsPage<>(List.of(row1), 2));
        given(client.fetchPopulation(2)).willReturn(new SeoulDemographicsPage<>(List.of(row2), 2));

        SeoulPopulationReader reader = new SeoulPopulationReader(client, 1, 0);

        assertThat(reader.read()).isSameAs(row1);
        assertThat(reader.read()).isSameAs(row2);
        assertThat(reader.read()).isNull();
    }

    private SeoulRawPopulation rawRow() {
        return new SeoulRawPopulation("서울특별시", "강남구", "역삼1동", "12345", "5678", "1234", "202505");
    }
}
