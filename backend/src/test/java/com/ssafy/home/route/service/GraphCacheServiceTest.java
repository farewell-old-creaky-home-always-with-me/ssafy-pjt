package com.ssafy.home.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ssafy.home.route.domain.FacilityGraph;
import com.ssafy.home.route.mapper.dto.FacilityResult;
import com.ssafy.home.route.mapper.FacilityMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraphCacheServiceTest {

    @Mock
    private FacilityMapper facilityMapper;

    private GraphCacheService graphCacheService;

    @BeforeEach
    void setUp() {
        graphCacheService = new GraphCacheService(facilityMapper);
    }

    @Test
    void facilitiesWithin1kmAreConnected() {
        // A(37.000, 127.000) — B(37.008, 127.000): Haversine ≈ 890m (1km 이내)
        when(facilityMapper.findAll()).thenReturn(List.of(
                facility(1L, 37.000, 127.000),
                facility(2L, 37.008, 127.000)
        ));

        graphCacheService.rebuild();
        FacilityGraph graph = graphCacheService.getGraph();

        assertThat(graph.getAdjacentEdges(1L)).anyMatch(e -> e.getToId() == 2L);
        assertThat(graph.getAdjacentEdges(2L)).anyMatch(e -> e.getToId() == 1L);
    }

    @Test
    void facilitiesOver1kmAreNotConnected() {
        // A(37.000, 127.000) — B(37.010, 127.000): Haversine ≈ 1112m (1km 초과)
        when(facilityMapper.findAll()).thenReturn(List.of(
                facility(1L, 37.000, 127.000),
                facility(2L, 37.010, 127.000)
        ));

        graphCacheService.rebuild();
        FacilityGraph graph = graphCacheService.getGraph();

        assertThat(graph.getAdjacentEdges(1L)).noneMatch(e -> e.getToId() == 2L);
        assertThat(graph.getAdjacentEdges(2L)).noneMatch(e -> e.getToId() == 1L);
    }

    private FacilityResult facility(Long id, double lat, double lng) {
        FacilityResult f = new FacilityResult();
        f.setFacilityId(id);
        f.setName("시설물-" + id);
        f.setFacilityType("SUBWAY");
        f.setLatitude(BigDecimal.valueOf(lat));
        f.setLongitude(BigDecimal.valueOf(lng));
        return f;
    }
}
