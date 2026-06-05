package com.ssafy.home.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;

class OpenApiConfigTest {

    @Test
    void commonResponseCustomizerAddsCommonErrorResponses() {
        OpenApiConfig config = new OpenApiConfig();
        OperationCustomizer customizer = config.commonResponseCustomizer();

        Operation operation = customizer.customize(new Operation(), null);

        assertThat(operation.getResponses().keySet())
                .contains("400", "401", "403", "500");
        assertErrorResponse(operation.getResponses().get("400"), "잘못된 요청");
        assertErrorResponse(operation.getResponses().get("401"), "인증 실패");
        assertErrorResponse(operation.getResponses().get("403"), "접근 권한 없음");
        assertErrorResponse(operation.getResponses().get("500"), "서버 오류");
    }

    @Test
    void openApiRegistersCommonErrorSchemas() {
        OpenApiConfig config = new OpenApiConfig();

        OpenAPI openAPI = config.openAPI();

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
