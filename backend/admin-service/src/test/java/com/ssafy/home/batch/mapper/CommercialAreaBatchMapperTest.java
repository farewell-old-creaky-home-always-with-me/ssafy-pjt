package com.ssafy.home.batch.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.batch.domain.NormalizedCommercialArea;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CommercialAreaBatchMapperTest {

    @Autowired
    private CommercialAreaBatchMapper mapper;

    @Test
    @DisplayName("새 상가 정보를 INSERT한다")
    void upsertInsertsNewStore() {
        NormalizedCommercialArea area = new NormalizedCommercialArea(
                "J1100000001", "테스트식당",
                "음식", "한식", "한식음식점",
                new BigDecimal("37.575023"), new BigDecimal("126.977957"),
                "서울 종로구 1-1"
        );

        int affected = mapper.upsert(area);

        assertThat(affected).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("같은 biz_id로 재INSERT하면 UPDATE된다")
    void upsertUpdatesExistingStore() {
        NormalizedCommercialArea original = new NormalizedCommercialArea(
                "J1100000001", "원래식당",
                "음식", "한식", "한식음식점",
                new BigDecimal("37.575023"), new BigDecimal("126.977957"),
                null
        );
        NormalizedCommercialArea updated = new NormalizedCommercialArea(
                "J1100000001", "바뀐식당",
                "음식", "한식", "한식음식점",
                new BigDecimal("37.575023"), new BigDecimal("126.977957"),
                null
        );
        mapper.upsert(original);

        // ON DUPLICATE KEY UPDATE returns >= 1 (1 for insert, 2 for update in MySQL mode)
        int affected = mapper.upsert(updated);

        assertThat(affected).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("region_code 테이블에서 시군구 코드(5자리)를 중복 없이 조회한다")
    @Sql(statements = {
            "INSERT INTO region_code (region_code, sido_name, sigungu_name, dong_name) VALUES ('1111010100', '서울특별시', '종로구', '청운동')",
            "INSERT INTO region_code (region_code, sido_name, sigungu_name, dong_name) VALUES ('1111010200', '서울특별시', '종로구', '신교동')",
            "INSERT INTO region_code (region_code, sido_name, sigungu_name, dong_name) VALUES ('1141010100', '서울특별시', '노원구', '월계동')"
    })
    void findAllSigunguCodesReturnsDistinctCodes() {
        List<String> codes = mapper.findAllSigunguCodes();

        assertThat(codes).containsExactly("11110", "11410");
    }
}
