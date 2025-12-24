package org.uvhnael.mpbe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "*",                          // Allow all origins
                    "http://localhost:*",         // Localhost any port
                    "http://127.0.0.1:*",         // 127.0.0.1 any port
                    "http://10.0.2.2:*",          // Android emulator localhost
                    "http://192.168.*.*:*"        // Local network IPs
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
