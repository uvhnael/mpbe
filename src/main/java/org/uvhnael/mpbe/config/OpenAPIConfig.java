package org.uvhnael.mpbe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/api/v1}")
    private String serverPath;
    @Bean
    public OpenAPI mealPlannerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Meal Planner API")
                        .description("AI-Based Meal Planner Backend API using Google Gemini AI")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Meal Planner Support")
                                .email("support@mealplanner.com")
                                .url("https://mealplanner.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + serverPath)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.mealplanner.com")
                                .description("Production Server")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")));
    }
}
