package com.surest.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {

    @Value("${server.port}")
    private String port;

    @Bean
    OpenAPI apiInfo() {
        final String securitySchemeName = "bearerAuth";


        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url("http://localhost:" + port).description("Localhost server url"));



        return new OpenAPI()
                .servers(servers)
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Bearer Authentication")))
                .info(new Info().title("surest-app-restAPI")
                        .description("A Spring Boot-based REST API for managing  App and user accounts.")
                        .version("v0.0.1")
                        .license(new License().name("MIT").url("https://opensource.org/license/mit/")))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repository")
                        .url("https://github.com/hemasundharp/surest_member_management.git"));
    }
}
