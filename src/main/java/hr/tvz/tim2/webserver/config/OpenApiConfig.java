package hr.tvz.tim2.webserver.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini videoteka API").version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP).scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

//    @Bean
//    public OpenApiCustomizer globalResponseCustomizer() {
//        return openApi -> openApi.getPaths()
//                .forEach((path, pathItem) -> pathItem.readOperations()
//                        .forEach(operation -> {
//            ApiResponses responses = operation.getResponses();
//            responses.addApiResponse("default", createApiErrorResponse());
//        }));
//    }
//
//    private ApiResponse createApiErrorResponse() {
//        return new ApiResponse()
//                .description("Error")
//                .content(new Content()
//                                 .addMediaType("application/json",
//                                               new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiError"))));
//    }
}