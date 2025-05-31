package de.studyshare.studyshare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Security configuration class for the StudyShare application.
 * This class configures security settings, including JWT authentication,
 * CORS, CSRF protection, and session management.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Constructor for SecurityConfig that initializes the JwtRequestFilter.
     *
     * @param jwtRequestFilter The JWT request filter to be used in the security
     *                         configuration.
     */
    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http       The HttpSecurity object to configure security settings.
     * @param corsSource The CorsConfigurationSource for handling CORS requests.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsSource)
            throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsSource))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            String expired = (String) request.getAttribute("expired");
                            String blocklisted = (String) request.getAttribute("blocklisted");

                            if (expired != null) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                        "JWT Token has expired: " + expired);
                            } else if (blocklisted != null) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                        "JWT Token is blocklisted.");
                            } else {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized: " + authException.getMessage());
                            }
                        }));
        return http.build();
    }

    /**
     * Provides a PasswordEncoder bean for encoding passwords.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides an AuthenticationManager bean for managing authentication.
     *
     * @param authenticationConfiguration The AuthenticationConfiguration to use.
     * @return An AuthenticationManager instance.
     * @throws Exception If an error occurs while creating the
     *                   AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
