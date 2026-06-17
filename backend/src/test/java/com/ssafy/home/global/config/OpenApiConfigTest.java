package com.ssafy.home.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;

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
        assertErrorResponse(operation.getResponses().get("400"), "잘못된 요청");
        assertErrorResponse(operation.getResponses().get("401"), "인증 실패");
        assertErrorResponse(operation.getResponses().get("403"), "접근 권한 없음");
        assertErrorResponse(operation.getResponses().get("500"), "서버 오류");
    }

    @Test
    @DisplayName("OpenAPI에 공통 에러 스키마를 등록한다")
    void openApiRegistersCommonErrorSchemas() {
        // given
        OpenApiConfig config = new OpenApiConfig();

        // when
        OpenAPI openAPI = config.openAPI();

        // then
        assertThat(openAPI.getComponents().getSchemas())
                .containsKeys("ErrorDetail", "FieldErrorDetail");
    }

    private void assertErrorResponse(ApiResponse response, String description) {
        MediaType mediaType = response.getContent().get("application/json");

        assertThat(response.getDescription()).isEqualTo(description);
        assertThat(mediaType.getSchema().get$ref())
                .isEqualTo("#/components/schemas/ErrorDetail");
    }
}
