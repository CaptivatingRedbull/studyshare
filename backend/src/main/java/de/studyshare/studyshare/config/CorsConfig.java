package de.studyshare.studyshare.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Cross-Origin Resource Sharing (CORS) configuration for the application.
 * This class defines CORS policies to allow the frontend application to
 * communicate
 * with the backend API securely across different origins.
 */
@Configuration
public class CorsConfig {
 /**
     * Configures CORS settings for the application.
     * 
     * @return CorsConfigurationSource that defines allowed origins, methods,
     *         headers,
     *         and whether credentials are supported for cross-origin requests.
     */
    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // allow orgins, HTTP-methods, headers and credentials
        config.setAllowedOrigins(List.of("http://192.168.67.63:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        // Apply these CORS settings to all API endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}