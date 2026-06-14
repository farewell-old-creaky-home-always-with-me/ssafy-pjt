package com.ssafy.home;

import com.ssafy.home.batch.mapper.BatchCollectionLogMapper;
import com.ssafy.home.batch.mapper.HouseDealBatchMapper;
import com.ssafy.home.commercial.mapper.CommercialMapper;
import com.ssafy.home.environment.mapper.EnvironmentMapper;
import com.ssafy.home.favorite.mapper.FavoriteMapper;
import com.ssafy.home.house.mapper.HouseMapper;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.notice.mapper.NoticeMapper;
import com.ssafy.home.place.mapper.PlaceMapper;
import com.ssafy.home.route.mapper.FacilityMapper;
import com.ssafy.home.route.mapper.RouteMapper;
import com.ssafy.home.stats.mapper.StatsMapper;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration"
})
class SsafyHomeApplicationTests {

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
    JobRepository jobRepository;

    @MockitoBean
    PlatformTransactionManager transactionManager;

    @MockitoBean
    BatchCollectionLogMapper batchCollectionLogMapper;

    @MockitoBean
    HouseDealBatchMapper houseDealBatchMapper;

    @Test
    void contextLoads() {
    }
}
