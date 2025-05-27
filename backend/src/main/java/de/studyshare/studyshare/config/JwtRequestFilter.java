package de.studyshare.studyshare.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import de.studyshare.studyshare.service.JpaUserDetailsService;
import de.studyshare.studyshare.service.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT Request Filter that intercepts all incoming HTTP requests and validates
 * JWT tokens.
 * 
 * This filter extracts the JWT from the Authorization header, validates it, and
 * sets up
 * Spring Security authentication if the token is valid.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JpaUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Constructs a new JwtRequestFilter with the necessary dependencies.
     * 
     * @param userDetailsService Service to load user details by username
     * @param jwtUtil            Utility for JWT operations like token validation
     *                           and extraction
     */
    public JwtRequestFilter(JpaUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Core filter method that intercepts every HTTP request to validate JWT tokens.
     * 
     * This method:
     * 1. Extracts the JWT from the Authorization header
     * 2. Validates the token and extracts the username
     * 3. Loads user details if username is found
     * 4. Sets the authentication in Spring Security's context if token is valid
     * 
     * @param request     The HTTP request being processed
     * @param response    The HTTP response
     * @param filterChain The filter chain for further processing
     * @throws ServletException If a servlet error occurs
     * @throws IOException      If an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the Authorization header from the request
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Check if the Authorization header is present and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                // Attempt to extract username from token
                username = jwtUtil.extractUsername(jwt);
            } catch (IllegalArgumentException e) {
                logger.warn("Unable to get JWT Token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token has expired", e);
                request.setAttribute("expired", e.getMessage());
            } catch (UnsupportedJwtException e) {
                logger.warn("JWT token is unsupported", e);
            } catch (MalformedJwtException e) {
                logger.warn("JWT token is malformed", e);
            } catch (SignatureException e) {
                logger.warn("JWT signature does not match locally computed signature", e);
            } catch (Exception e) {
                logger.error("JWT token validation error", e);
            }
        } else if (authorizationHeader != null) {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // Set up authentication if token is valid and user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details by username from the token
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validate token against user details
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Create authentication token with user's authorities
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // Set request details in authentication token
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Set authentication in Spring Security context
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.warn("JWT Token is not valid for user: " + username);
            }
        }
        // Continue with filter chain
        filterChain.doFilter(request, response);
    }
}
