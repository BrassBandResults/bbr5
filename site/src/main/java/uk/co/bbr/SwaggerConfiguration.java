package uk.co.bbr;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfiguration {

    public static final String API_KEY_HEADER = "X-API-Key";

    @Bean
    public OpenAPI springOpenApiConfig() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("api_key", new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .description("Api Key Access")
                    .in(SecurityScheme.In.HEADER)
                    .name(API_KEY_HEADER)
                )
            )
            .security(Arrays.asList(
                new SecurityRequirement().addList("api_key")))
            // whatever else you need
            .info(new Info().title("Brass Band Results")
                .description("API for https://brassbandresults.co.uk")
                .version("v1")
            );
    }
}
