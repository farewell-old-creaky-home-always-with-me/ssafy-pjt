package com.ssafy.home.favorite.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.favorite.mapper.dto.FavoriteCreateParam;
import com.ssafy.home.favorite.mapper.dto.FavoriteDetailResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@Sql("/sql/favorite-data.sql")
class FavoriteMapperTest {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Test
    @DisplayName("회원의 관심 지역 목록을 조회한다")
    void findByMemberId() {
        // when
        List<FavoriteDetailResult> favorites = favoriteMapper.findByMemberId(1L);

        // then
        assertThat(favorites).hasSize(1);
        assertThat(favorites.get(0).getRegionCode()).isEqualTo("1168010100");
        assertThat(favorites.get(0).getSigunguName()).isEqualTo("강남구");
    }

    @Test
    @DisplayName("관심 지역을 등록하고 삭제한다")
    void insertAndDelete() {
        // given
        FavoriteCreateParam param = new FavoriteCreateParam();
        param.setMemberId(1L);
        param.setRegionCode("1168010100");

        // when
        assertThat(favoriteMapper.existsByMemberIdAndRegionCode(1L, "1168010100")).isTrue();

        param.setRegionCode("1171010100");
        favoriteMapper.insert(param);

        // then
        assertThat(param.getId()).isNotNull();
        assertThat(favoriteMapper.findMemberIdById(param.getId())).isEqualTo(1L);

        // when
        int deleted = favoriteMapper.deleteByIdAndMemberId(param.getId(), 1L);

        // then
        assertThat(deleted).isEqualTo(1);
    }
}
