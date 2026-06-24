package com.ssafy.home.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ssafy.home.batch.domain.HouseType;
import com.ssafy.home.batch.domain.NormalizedHouseDeal;
import com.ssafy.home.batch.mapper.HouseDealBatchMapper;
import com.ssafy.home.external.molit.MolitRawHouseDeal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HouseDealProcessorTest {

    @Mock
    private HouseDealBatchMapper mapper;

    private HouseDealProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new HouseDealProcessor(mapper, HouseType.APARTMENT);
    }

    @Test
    @DisplayName("거래 응답에 법정동 코드가 없으면 요청 지역코드와 법정동명으로 조회한다")
    void processFindsRegionCodeByLawdCodeAndDongName() {
        // Given
        MolitRawHouseDeal rawDeal = new MolitRawHouseDeal(
                null,
                "11110",
                "청운동",
                "청운아파트",
                "1-1",
                "10,000",
                "2026",
                "1",
                "15",
                "84.0",
                "10",
                "2020"
        );
        given(mapper.findRegionCodeByLawdCodeAndDongName("11110", "청운동"))
                .willReturn("1111010100");
        given(mapper.existsRegionCode("1111010100")).willReturn(true);

        // When
        NormalizedHouseDeal normalized = processor.process(rawDeal);

        // Then
        assertThat(normalized.regionCode()).isEqualTo("1111010100");
    }
}
