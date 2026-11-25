package es.examrush.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI examRushApi() {
        return new OpenAPI().info(new Info().title("Exam Rush API")
                .description("API de usuarios, tienda, inventario, partidas y ranking.")
                .version("v1"));
    }
}
