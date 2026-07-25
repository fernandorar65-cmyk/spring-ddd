package kahoot.clabs.kahoot_clabs.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI kahootOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("kahoot-clabs API")
                        .description("API multi-tenant estilo Kahoot (DDD). Auth por JWT pendiente.")
                        .version("v1"));
    }
}
