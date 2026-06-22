package com.ssafy.home.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.global.interceptor.LoginRequired;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;

class OpenApiConfigTest {

    @Test
    @DisplayName("공통 응답 커스터마이저가 에러 응답을 추가한다")
    void commonResponseCustomizerAddsCommonErrorResponses() {
        // given
        OpenApiConfig config = new OpenApiConfig();
        OperationCustomizer customizer = config.commonResponseCustomizer();

        // when
        Operation operation = customizer.customize(new Operation(), null);

        // then
        assertThat(operation.getResponses().keySet())
                .contains("400", "401", "403", "500");
        assertErrorResponse(operation.getResponses().get("400"), "Invalid request");
        assertErrorResponse(operation.getResponses().get("401"), "Authentication failed");
        assertErrorResponse(operation.getResponses().get("403"), "Access denied");
        assertErrorResponse(operation.getResponses().get("500"), "Internal server error");
    }

    @Test
    @DisplayName("OpenAPI에 공통 에러 스키마와 JWT 보안 스키마를 등록한다")
    void openApiRegistersCommonErrorSchemasAndJwtSecurityScheme() {
        // given
        OpenApiConfig config = new OpenApiConfig();

        // when
        OpenAPI openAPI = config.openAPI();

        // then
        assertThat(openAPI.getComponents().getSchemas())
                .containsKeys("ErrorDetail", "FieldErrorDetail");
        assertThat(openAPI.getComponents().getSecuritySchemes())
                .containsKey(OpenApiConfig.JWT_SCHEME_NAME);
    }

    @Test
    @DisplayName("로그인 필수 API에는 JWT 보안 요구사항을 추가한다")
    void commonResponseCustomizerAddsJwtSecurityToLoginRequiredOperation() throws Exception {
        // given
        OpenApiConfig config = new OpenApiConfig();
        OperationCustomizer customizer = config.commonResponseCustomizer();
        HandlerMethod handlerMethod = new HandlerMethod(
                new TestController(),
                TestController.class.getDeclaredMethod("loginRequired")
        );

        // when
        Operation operation = customizer.customize(new Operation(), handlerMethod);

        // then
        assertThat(operation.getSecurity())
                .anySatisfy(requirement -> assertThat(requirement)
                        .containsKey(OpenApiConfig.JWT_SCHEME_NAME));
    }

    private void assertErrorResponse(ApiResponse response, String description) {
        MediaType mediaType = response.getContent().get("application/json");

        assertThat(response.getDescription()).isEqualTo(description);
        assertThat(mediaType.getSchema().get$ref())
                .isEqualTo("#/components/schemas/ErrorDetail");
    }

    static class TestController {

        @LoginRequired
        @GetMapping("/login-required")
        public void loginRequired() {
        }
    }
}
