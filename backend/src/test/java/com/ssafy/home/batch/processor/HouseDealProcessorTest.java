package com.ssafy.home.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.ssafy.home.batch.domain.HouseType;
import com.ssafy.home.batch.domain.NormalizedHouseDeal;
import com.ssafy.home.batch.mapper.HouseDealBatchMapper;
import com.ssafy.home.external.molit.MolitRawHouseDeal;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HouseDealProcessorTest {

    @Mock
    private HouseDealBatchMapper mapper;

    @Test
    void normalizesApartmentSaleData() {
        when(mapper.existsRegionCode("1111010100")).thenReturn(true);
        HouseDealProcessor processor = new HouseDealProcessor(mapper, HouseType.APARTMENT);

        NormalizedHouseDeal result = processor.process(new MolitRawHouseDeal(
                "1111010100", " 테스트 아파트 ", " 12-3 ", "123,456",
                "2026", "5", "7", "84.95", "10", "2001"
        ));

        assertThat(result).isEqualTo(new NormalizedHouseDeal(
                "1111010100", "테스트 아파트", "12-3", 2001, "아파트",
                "매매", 123456, null, 0, LocalDate.of(2026, 5, 7),
                new BigDecimal("84.95"), 10
        ));
    }

    @Test
    void rejectsUnknownRegionCode() {
        when(mapper.existsRegionCode("1111010100")).thenReturn(false);
        HouseDealProcessor processor = new HouseDealProcessor(mapper, HouseType.APARTMENT);

        assertThatThrownBy(() -> processor.process(new MolitRawHouseDeal(
                "1111010100", "테스트 아파트", "12-3", "123,456",
                "2026", "5", "7", "84.95", "10", "2001"
        ))).isInstanceOf(InvalidHouseDealException.class)
                .hasMessage("Unknown region code");
    }
}
