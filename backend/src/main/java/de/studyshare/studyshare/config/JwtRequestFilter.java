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
import de.studyshare.studyshare.service.TokenBlocklistService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter for processing JWT authentication in incoming HTTP requests.
 * This filter extracts the JWT from the Authorization header, validates it,
 * and sets the authentication in the security context if valid.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JpaUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenBlocklistService tokenBlocklistService;

    /**
     * Constructor for JwtRequestFilter.
     * Initializes the user details service, JWT utility, and token blocklist
     * service.
     *
     * @param userDetailsService    The service to load user details.
     * @param jwtUtil               The utility for handling JWT operations.
     * @param tokenBlocklistService The service for managing blocklisted tokens.
     */
    public JwtRequestFilter(JpaUserDetailsService userDetailsService, JwtUtil jwtUtil,
            TokenBlocklistService tokenBlocklistService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.tokenBlocklistService = tokenBlocklistService;
    }

    /**
     * Filters incoming requests to check for JWT authentication.
     * Extracts the JWT from the Authorization header, validates it, and sets the
     * authentication in the security context if valid.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain to continue processing the request.
     * @throws ServletException If an error occurs during filtering.
     * @throws IOException      If an I/O error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        String jti = null; // JWT ID

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                jti = jwtUtil.extractJti(jwt); // Extract JTI
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

        if (username != null && jwt != null && jti != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Check if the token's JTI is blocklisted
            if (tokenBlocklistService.isBlocklisted(jwt)) { // Pass the full token to service, it will extract JTI
                logger.warn("JWT Token's JTI is blocklisted: " + jti);
                // Setting an attribute to indicate the reason for unauthorized access
                request.setAttribute("blocklisted", "Token is blocklisted");
            } else if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.warn("JWT Token is not valid for user: " + username);
            }
        }
        filterChain.doFilter(request, response);
    }
}
