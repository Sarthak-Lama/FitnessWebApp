package com.project.fitness.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fitness Tracking API")
                        .version("1.0")
                .description("This is the API for Fitness Tracking API.")
                                .contact(new Contact()
                                        .name("Sarthak Lama")
                                        .url("https://www.linkedin.com/in/sarthak-l-429200286/")
                                        .email("sarthaklama068@gmail.com"))
                );

    }
}
