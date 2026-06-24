package com.ssafy.home.batch.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.external.sdsc.SdscRawStore;
import com.ssafy.home.external.sdsc.SdscStorePage;
import com.ssafy.home.external.sdsc.SdscStoreClient;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SdscStoreReaderTest {

    @Mock
    private SdscStoreClient client;

    @Test
    @DisplayName("두 시군구 코드를 순서대로 읽는다")
    void readFetchesTwoSigunguCodesInOrder() {
        SdscRawStore store1 = store("J001");
        SdscRawStore store2 = store("J002");
        given(client.fetch("11110", 1))
                .willReturn(new SdscStorePage(List.of(store1), 1));
        given(client.fetch("11140", 1))
                .willReturn(new SdscStorePage(List.of(store2), 1));
        SdscStoreReader reader = new SdscStoreReader(client, List.of("11110", "11140"), 10, 0);

        assertThat(reader.read().bizesId()).isEqualTo("J001");
        assertThat(reader.read().bizesId()).isEqualTo("J002");
        assertThat(reader.read()).isNull();
    }

    @Test
    @DisplayName("한 시군구에 여러 페이지가 있으면 순서대로 모두 읽는다")
    void readFetchesMultiplePagesForOneSigungu() {
        SdscRawStore store1 = store("J001");
        SdscRawStore store2 = store("J002");
        given(client.fetch("11110", 1))
                .willReturn(new SdscStorePage(List.of(store1), 2));
        given(client.fetch("11110", 2))
                .willReturn(new SdscStorePage(List.of(store2), 2));
        SdscStoreReader reader = new SdscStoreReader(client, List.of("11110"), 1, 0);

        assertThat(reader.read().bizesId()).isEqualTo("J001");
        assertThat(reader.read().bizesId()).isEqualTo("J002");
        assertThat(reader.read()).isNull();
    }

    @Test
    @DisplayName("시군구 목록이 비어 있으면 즉시 null을 반환한다")
    void readReturnsNullImmediatelyWhenNoSigunguCodes() {
        SdscStoreReader reader = new SdscStoreReader(client, List.of(), 10, 0);

        assertThat(reader.read()).isNull();
    }

    private SdscRawStore store(String bizesId) {
        return new SdscRawStore(bizesId, "식당", null, null, null, "37.5", "126.9", null, null);
    }
}
