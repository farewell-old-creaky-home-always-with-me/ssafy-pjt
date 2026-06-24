package com.ssafy.home.house.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.house.mapper.dto.HouseDealResult;
import com.ssafy.home.house.mapper.dto.HouseDetailResult;
import com.ssafy.home.house.mapper.dto.HouseSearchParam;
import com.ssafy.home.house.mapper.dto.HouseSummaryResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Sql("/sql/house-data.sql")
class HouseMapperTest {

    @Autowired
    private HouseMapper houseMapper;

    @Test
    @DisplayName("ID로 주택 상세를 조회한다")
    void findById() {
        // when
        HouseDetailResult found = houseMapper.findById(1L);

        // then
        assertThat(found.getAptName()).isEqualTo("역삼래미안");
        assertThat(found.getRegionCode()).isEqualTo("1168010100");
    }

    @Test
    @DisplayName("지역 코드로 주택을 검색한다")
    void search() {
        // given
        HouseSearchParam condition = new HouseSearchParam();
        condition.setRegionCode("1168010100");
        condition.setSize(20);
        condition.setOffset(0);

        // when
        long total = houseMapper.countBySearch(condition);
        List<HouseSummaryResult> houses = houseMapper.search(condition);

        // then
        assertThat(total).isEqualTo(2L);
        assertThat(houses).hasSize(2);
        assertThat(houses.get(0).getLatestDealType()).isEqualTo("매매");
    }

    @Test
    @DisplayName("아파트명으로 주택을 검색한다")
    void searchByHouseName() {
        // given
        HouseSearchParam condition = new HouseSearchParam();
        condition.setRegionCode("1168010100");
        condition.setHouseName("래미안");
        condition.setSize(20);
        condition.setOffset(0);

        // when
        long total = houseMapper.countBySearch(condition);
        List<HouseSummaryResult> houses = houseMapper.search(condition);

        // then
        assertThat(total).isEqualTo(1L);
        assertThat(houses).hasSize(1);
        assertThat(houses.get(0).getAptName()).isEqualTo("역삼래미안");
    }

    @Test
    @DisplayName("주택 거래 이력을 조회한다")
    void findAllByHouseId() {
        // when
        List<HouseDealResult> deals = houseMapper.findAllByHouseId(1L);

        // then
        assertThat(deals).hasSize(2);
        assertThat(deals.get(0).getDealDate()).isAfter(deals.get(1).getDealDate());
    }
}
