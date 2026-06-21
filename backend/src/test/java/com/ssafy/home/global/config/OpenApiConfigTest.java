package com.ssafy.home.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.global.interceptor.LoginRequired;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;

class OpenApiConfigTest {

    @Test
    void commonResponseCustomizerAddsCommonErrorResponses() {
        OpenApiConfig config = new OpenApiConfig();
        OperationCustomizer customizer = config.commonResponseCustomizer();

        Operation operation = customizer.customize(new Operation(), null);

        assertThat(operation.getResponses().keySet())
                .contains("400", "401", "403", "500");
        assertErrorResponse(operation.getResponses().get("400"), "Invalid request");
        assertErrorResponse(operation.getResponses().get("401"), "Authentication failed");
        assertErrorResponse(operation.getResponses().get("403"), "Access denied");
        assertErrorResponse(operation.getResponses().get("500"), "Internal server error");
    }

    @Test
    void openApiRegistersCommonErrorSchemasAndJwtSecurityScheme() {
        OpenApiConfig config = new OpenApiConfig();

        OpenAPI openAPI = config.openAPI();

        assertThat(openAPI.getComponents().getSchemas())
                .containsKeys("ErrorDetail", "FieldErrorDetail");
        assertThat(openAPI.getComponents().getSecuritySchemes())
                .containsKey(OpenApiConfig.JWT_SCHEME_NAME);
    }

    @Test
    void commonResponseCustomizerAddsJwtSecurityToLoginRequiredOperation() throws Exception {
        OpenApiConfig config = new OpenApiConfig();
        OperationCustomizer customizer = config.commonResponseCustomizer();
        HandlerMethod handlerMethod = new HandlerMethod(
                new TestController(),
                TestController.class.getDeclaredMethod("loginRequired")
        );

        Operation operation = customizer.customize(new Operation(), handlerMethod);

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
