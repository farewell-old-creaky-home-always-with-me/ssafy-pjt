package com.ssafy.home.global.config;

import com.ssafy.home.global.response.ErrorDetail;
import com.ssafy.home.global.response.FieldErrorDetail;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.core.converter.ModelConverters;
import java.util.Map;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SSAFY HOME API",
                version = "v1",
                description = "SSAFY HOME 백엔드 API 문서"
        )
)
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .schemas(registerSchemas(ErrorDetail.class, FieldErrorDetail.class)));
    }

    @Bean
    public OperationCustomizer commonResponseCustomizer() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }

            responses.addApiResponse("400", errorResponse("잘못된 요청"));
            responses.addApiResponse("401", errorResponse("인증 실패"));
            responses.addApiResponse("403", errorResponse("접근 권한 없음"));
            responses.addApiResponse("500", errorResponse("서버 오류"));

            return operation;
        };
    }

    private ApiResponse errorResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<ErrorDetail>()
                                        .$ref("#/components/schemas/ErrorDetail"))));
    }

    private Map<String, Schema> registerSchemas(Class<?>... classes) {
        Map<String, Schema> schemas = new java.util.LinkedHashMap<>();
        for (Class<?> clazz : classes) {
            schemas.putAll(ModelConverters.getInstance().read(clazz));
        }
        return schemas;
    }
}
