package com.ssafy.home.global.config;

import com.ssafy.home.global.interceptor.LoginRequired;
import com.ssafy.home.global.response.ErrorDetail;
import com.ssafy.home.global.response.FieldErrorDetail;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Map;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SSAFY HOME API",
                version = "v1",
                description = "SSAFY HOME backend API documentation"
        )
)
public class OpenApiConfig {

    static final String JWT_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .schemas(registerSchemas(ErrorDetail.class, FieldErrorDetail.class))
                        .addSecuritySchemes(JWT_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public OperationCustomizer commonResponseCustomizer() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }

            responses.addApiResponse("400", errorResponse("Invalid request"));
            responses.addApiResponse("401", errorResponse("Authentication failed"));
            responses.addApiResponse("403", errorResponse("Access denied"));
            responses.addApiResponse("500", errorResponse("Internal server error"));

            if (requiresAuthentication(handlerMethod)) {
                operation.addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME_NAME));
            }

            return operation;
        };
    }

    private boolean requiresAuthentication(HandlerMethod handlerMethod) {
        if (handlerMethod == null) {
            return false;
        }

        return handlerMethod.hasMethodAnnotation(LoginRequired.class)
                || handlerMethod.getBeanType().isAnnotationPresent(LoginRequired.class);
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
