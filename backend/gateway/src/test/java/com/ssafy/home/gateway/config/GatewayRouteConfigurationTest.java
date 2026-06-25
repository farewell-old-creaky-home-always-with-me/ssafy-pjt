package com.ssafy.home.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;

@SpringBootTest(properties = "jwt.secret=test-jwt-secret-key-for-ssafy-home-project-2026")
class GatewayRouteConfigurationTest {

    @Autowired
    private GatewayProperties gatewayProperties;

    @Test
    @DisplayName("AI 서비스 라우트는 /api/chat과 하위 경로를 모두 매칭한다")
    void aiRouteMatchesChatRootAndNestedPaths() {
        RouteDefinition aiRoute = gatewayProperties.getRoutes().stream()
                .filter(route -> route.getId().equals("ai-service"))
                .findFirst()
                .orElseThrow();

        List<String> pathPatterns = aiRoute.getPredicates().stream()
                .filter(predicate -> predicate.getName().equals("Path"))
                .map(PredicateDefinition::getArgs)
                .flatMap(args -> args.values().stream())
                .toList();

        assertThat(pathPatterns).contains("/api/chat", "/api/chat/**");
    }
}
