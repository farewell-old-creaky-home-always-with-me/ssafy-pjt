package com.ssafy.home.batch.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.ssafy.home.external.vworld.VworldLegalRegionClient;
import com.ssafy.home.external.vworld.VworldProperties;
import com.ssafy.home.external.vworld.VworldRawRegion;
import com.ssafy.home.external.vworld.VworldRegionPage;
import com.ssafy.home.external.vworld.VworldSidoCodes;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VworldRegionReaderTest {

    private VworldLegalRegionClient client;
    private VworldRegionReader reader;

    @BeforeEach
    void setUp() {
        client = mock(VworldLegalRegionClient.class);
        VworldProperties properties = new VworldProperties(
                URI.create("http://localhost/vworld"),
                "test-key",
                "localhost",
                "LT_C_ADLEGAL_EMD",
                100,
                Duration.ofSeconds(5),
                0,
                1,
                1000
        );
        reader = new VworldRegionReader(client, properties);
    }

    @Test
    @DisplayName("첫 페이지 요청은 page=1로 호출한다")
    void firstFetchUsesPageOne() {
        // given
        VworldRawRegion region = new VworldRawRegion(
                "1111010100", "서울특별시", "종로구", "청운동", false
        );
        given(client.fetch(eq("11"), eq(1)))
                .willReturn(new VworldRegionPage(List.of(region), 1));
        stubEmptySidoResponsesExcept("11");

        // when
        VworldRawRegion result = reader.read();

        // then
        assertThat(result).isEqualTo(region);
        verify(client).fetch(eq("11"), eq(1));
    }

    private void stubEmptySidoResponsesExcept(String excludedSidoCode) {
        for (String sidoCode : VworldSidoCodes.ALL) {
            if (sidoCode.equals(excludedSidoCode)) {
                continue;
            }
            given(client.fetch(eq(sidoCode), anyInt()))
                    .willReturn(new VworldRegionPage(List.of(), 0));
        }
    }
}
