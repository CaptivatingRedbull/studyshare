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

import de.studyshare.studyshare.service.JpaUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Security configuration class for the application.
 * 
 * This class configures Spring Security with JWT authentication, defines access
 * rules,
 * CORS settings, and sets up the authentication flow. It implements a stateless
 * authentication mechanism suitable for REST APIs.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Constructs a new SecurityConfig with required dependencies.
     * 
     * @param jpaUserDetailsService Service for loading user details
     * @param jwtRequestFilter      Filter that processes JWT authentication tokens
     */
    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * 
     * This method:
     * - Applies CORS configuration
     * - Disables CSRF protection
     * - Configures frame options to allow H2 console access (will be removed in
     * production)
     * - Sets up authorization rules for different endpoints
     * - Configures stateless session management
     * - Adds the JWT request filter
     * - Handles authentication exceptions
     *
     * @param http       The HttpSecurity object to configure
     * @param corsSource The CORS configuration source
     * @return The built SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsSource)
            throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsSource))
                // Disable CSRF protection as we use JWT tokens
                .csrf(csrf -> csrf.disable())
                // Allow H2 console access (to be removed in production)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth
                        // Allow access to all public entpoints
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // to be removed in production

                        .anyRequest().authenticated())
                // Configure stateless session management (no server-side sessions)
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            String expired = (String) request.getAttribute("expired");
                            if (expired != null) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                        "JWT Token has expired: " + expired);
                            } else {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized: Access is denied");
                            }
                        }));
        return http.build();
    }

    /**
     * Creates a password encoder bean for secure password hashing.
     *
     * @return BCryptPasswordEncoder for encoding passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates an authentication manager bean that uses the default authentication
     * configuration.
     *
     * @param authenticationConfiguration The Spring Security authentication
     *                                    configuration
     * @return The authentication manager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
