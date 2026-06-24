package com.ssafy.home;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.commercial.mapper.CommercialMapper;
import com.ssafy.home.environment.mapper.EnvironmentMapper;
import com.ssafy.home.favorite.mapper.FavoriteMapper;
import com.ssafy.home.global.config.database.InitialDataLoader;
import com.ssafy.home.house.mapper.HouseMapper;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.notice.mapper.NoticeMapper;
import com.ssafy.home.place.mapper.PlaceMapper;
import com.ssafy.home.qna.mapper.QnaMapper;
import com.ssafy.home.region.mapper.RegionMapper;
import com.ssafy.home.report.mapper.BatchReportMapper;
import com.ssafy.home.route.mapper.FacilityMapper;
import com.ssafy.home.route.mapper.RouteMapper;
import com.ssafy.home.stats.mapper.StatsMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration"
})
class SsafyHomeApplicationTests {

    @Autowired
    ApplicationContext applicationContext;

    @MockitoBean
    MemberMapper memberMapper;

    @MockitoBean
    FavoriteMapper favoriteMapper;

    @MockitoBean
    HouseMapper houseMapper;

    @MockitoBean
    NoticeMapper noticeMapper;

    @MockitoBean
    CommercialMapper commercialMapper;

    @MockitoBean
    EnvironmentMapper environmentMapper;

    @MockitoBean
    PlaceMapper placeMapper;

    @MockitoBean
    FacilityMapper facilityMapper;

    @MockitoBean
    RouteMapper routeMapper;

    @MockitoBean
    StatsMapper statsMapper;

    @MockitoBean
    RegionMapper regionMapper;

    @MockitoBean
    QnaMapper qnaMapper;

    @MockitoBean
    BatchReportMapper batchReportMapper;

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("공유 테스트 설정은 초기 데이터 로더를 비활성화한다")
    void sharedTestConfigurationDisablesInitialDataLoader() {
        // Given
        // The application context uses the shared test configuration.

        // When
        var initialDataLoaders = applicationContext.getBeansOfType(InitialDataLoader.class);

        // Then
        assertThat(initialDataLoaders).isEmpty();
    }
}
