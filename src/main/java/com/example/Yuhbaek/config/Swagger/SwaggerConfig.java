package com.example.Yuhbaek.config.Swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("로컬 서버");

        Info info = new Info()
                .title("Yuhbaek API")
                .version("1.0")
                .description("Yuhbaek 프로젝트")
                .contact(new Contact()
                        .name("Yuhbaek Team")
                        .email("support@yuhbaek.com"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}

