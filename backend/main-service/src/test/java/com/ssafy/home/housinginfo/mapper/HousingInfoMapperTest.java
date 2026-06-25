package com.ssafy.home.housinginfo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.housinginfo.mapper.dto.HousingInfoResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Sql("/sql/housing-content-data.sql")
class HousingInfoMapperTest {

    @Autowired
    private HousingInfoMapper housingInfoMapper;

    @Test
    @DisplayName("최신 주거 정보 목록을 발행일과 ID 내림차순으로 조회한다")
    void findRecent() {
        // when
        List<HousingInfoResult> result = housingInfoMapper.findRecent(null, 2);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPublishedAt()).isAfterOrEqualTo(result.get(1).getPublishedAt());
        if (result.get(0).getPublishedAt().isEqual(result.get(1).getPublishedAt())) {
            assertThat(result.get(0).getId()).isGreaterThan(result.get(1).getId());
        }
    }

    @Test
    @DisplayName("주거 정보 유형으로 필터링한다")
    void findRecentByInfoType() {
        // when
        List<HousingInfoResult> result = housingInfoMapper.findRecent("POLICY", 10);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).allSatisfy(item -> assertThat(item.getInfoType()).isEqualTo("POLICY"));
    }
}
