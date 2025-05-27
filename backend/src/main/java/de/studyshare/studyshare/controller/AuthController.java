package de.studyshare.studyshare.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.dto.entity.UserDTO;
import de.studyshare.studyshare.dto.request.LoginRequest;
import de.studyshare.studyshare.dto.request.RegisterRequest;
import de.studyshare.studyshare.dto.request.UserCreateRequest;
import de.studyshare.studyshare.dto.response.LoginResponse;
import de.studyshare.studyshare.service.JwtUtil;
import de.studyshare.studyshare.service.TokenBlocklistService;
import de.studyshare.studyshare.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling authentication-related requests such as login,
 * registration, and logout.
 * Provides endpoints for user authentication and management.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlocklistService tokenBlocklistService;

    /**
     * Constructor for AuthController.
     * Initializes the authentication manager, user service, JWT utility, and token
     * blocklist service.
     * 
     * @param authenticationManager The authentication manager for handling user
     *                              authentication.
     * @param userService           The service for managing user accounts.
     * @param jwtUtil               The utility for generating and validating JWT
     *                              tokens.
     * @param tokenBlocklistService The service for managing blocklisted tokens.
     */
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil,
            TokenBlocklistService tokenBlocklistService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenBlocklistService = tokenBlocklistService;
    }

    /**
     * Handles user login by authenticating the user credentials.
     * If successful, generates a JWT token and returns it along with the username.
     * 
     * @param loginRequest The request containing user login details.
     * @return ResponseEntity with the generated JWT token and username.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            final String token = jwtUtil.generateToken(authentication.getName());
            logger.info("User {} logged in successfully.", loginRequest.username());
            return ResponseEntity.ok(new LoginResponse(token, authentication.getName()));

        } catch (BadCredentialsException e) {
            logger.warn("Login attempt failed for user {}: Invalid credentials", loginRequest.username());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid username or password!");
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user {}: {}", loginRequest.username(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Authentication failed! " + e.getMessage());
        }
    }

    /**
     * Handles user registration by creating a new user account.
     * Validates the uniqueness of username and email before creating the user.
     * Automatically logs in the user after successful registration.
     * 
     * @param registerRequest The request containing user registration details.
     * @return ResponseEntity with the created user's login token and username.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        if (userService.existsByUsername(registerRequest.username())) {
            logger.warn("Registration attempt failed: Username {} is already taken.", registerRequest.username());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Username is already taken!");
        }
        if (userService.existsByEmail(registerRequest.email())) {
            logger.warn("Registration attempt failed: Email {} is already in use.", registerRequest.email());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email is already in use!");
        }

        UserCreateRequest userCreateRequest = new UserCreateRequest(
                registerRequest.firstName(),
                registerRequest.lastName(),
                registerRequest.email(),
                registerRequest.username(),
                registerRequest.password(),
                Role.STUDENT // default role
        );

        UserDTO registeredUser = userService.createUser(userCreateRequest);
        logger.info("User {} registered successfully.", registeredUser.username());

        // Automatically log in the user after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userCreateRequest.username(), userCreateRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = jwtUtil.generateToken(authentication.getName());
        logger.info("User {} automatically logged in after registration.", registeredUser.username());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse(token, registeredUser.username()));
    }

    /**
     * Handles user logout by blocklisting the JWT token.
     * The token's JTI is extracted and added to the blocklist.
     * 
     * @param request The HTTP request containing the JWT token in the Authorization
     *                header.
     * @return ResponseEntity indicating success or failure of the logout operation.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        if (jwt != null) {
            try {
                if (jwtUtil.validateToken(jwt)) { // Validate before blocklisting
                    String username = jwtUtil.extractUsername(jwt);
                    tokenBlocklistService.addToBlocklist(jwt); // Service now handles JTI extraction
                    SecurityContextHolder.clearContext(); // Clear security context
                    logger.info("User {} logged out successfully. Token JTI blocklisted.", username);
                    return ResponseEntity.ok("Successfully logged out.");
                } else {
                    logger.warn("Logout attempt with an invalid or expired token.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid or expired token provided for logout.");
                }
            } catch (Exception e) {
                logger.error("Error during logout process: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during logout.");
            }
        }
        logger.warn("Logout attempt failed: No token provided or invalid format.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Logout failed. No token provided or invalid format.");
    }
}
