package com.ssafy.home;

import com.ssafy.home.commercial.mapper.CommercialMapper;
import com.ssafy.home.environment.mapper.EnvironmentMapper;
import com.ssafy.home.favorite.mapper.FavoriteMapper;
import com.ssafy.home.house.mapper.HouseMapper;
import com.ssafy.home.member.mapper.MemberMapper;
import com.ssafy.home.notice.mapper.NoticeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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

    @Test
    void contextLoads() {
    }
}
