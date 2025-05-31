package de.studyshare.studyshare.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * Utility class for handling JWT (JSON Web Tokens) operations.
 * Provides methods to generate, validate, and extract information from JWT
 * tokens.
 */
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private Key key;

    /**
     * Initializes the JWT key using the secret from application properties.
     * This method is called after the bean is constructed to ensure the key is
     * ready for use.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token The JWT token.
     * @return The username (subject) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the JWT ID (jti) from the JWT token.
     *
     * @param token The JWT token.
     * @return The JTI from the token.
     */
    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver
     * function.
     *
     * @param token          The JWT token.
     * @param claimsResolver A function to extract the desired claim.
     * @param <T>            The type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT token and extracts all claims.
     *
     * @param token The JWT token.
     * @return The claims contained in the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token The JWT token.
     * @return True if the token has expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT token for the given UserDetails.
     *
     * @param userDetails The UserDetails object representing the authenticated
     *                    user.
     * @return A JWT token string.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // You can add more claims here if needed, e.g., roles, user ID
        // claims.put("roles",
        // userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Generates a JWT token for the given username.
     * Allows for more flexibility if UserDetails object is not readily available
     * or if you want to include custom claims directly.
     *
     * @param username The username for whom the token is generated.
     * @return A JWT token string.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        // Add custom claims if necessary
        return createToken(claims, username);
    }

    /**
     * Creates a JWT token with the given claims and subject (username).
     *
     * @param claims  The claims to include in the token.
     * @param subject The subject of the token (typically the username).
     * @return A JWT token string.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validates the JWT token against the given UserDetails.
     * Checks if the username in the token matches and if the token has not expired.
     *
     * @param token       The JWT token.
     * @param userDetails The UserDetails object to validate against.
     * @return True if the token is valid, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validates the JWT token.
     * Checks if the token has not expired and is well-formed.
     * This version is useful when you don't have UserDetails yet, e.g., in the
     * filter.
     *
     * @param token The JWT token.
     * @return True if the token is valid (not expired and parsable), false
     *         otherwise.
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            // Log the exception (e.g., MalformedJwtException, ExpiredJwtException,
            // SignatureException)
            // logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
}
