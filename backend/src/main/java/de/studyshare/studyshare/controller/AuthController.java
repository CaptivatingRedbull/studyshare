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
import de.studyshare.studyshare.exception.GlobalExceptionHandler.ErrorDetails; // Import ErrorDetails
import de.studyshare.studyshare.service.JwtUtil;
import de.studyshare.studyshare.service.TokenBlocklistService;
import de.studyshare.studyshare.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date; // Import Date

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
     * 
     * @param httpRequest  The HTTP request.
     * @return ResponseEntity with the generated JWT token and username or error
     *         details.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            final String token = jwtUtil.generateToken(authentication.getName());
            logger.info("User {} logged in successfully.", loginRequest.username());
            return ResponseEntity.ok(new LoginResponse(token, authentication.getName()));

        } catch (BadCredentialsException e) {
            logger.warn("Login attempt failed for user {}: Invalid credentials", loginRequest.username());
            ErrorDetails errorDetails = new ErrorDetails(new Date(), "Invalid username or password!",
                    httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user {}: {}", loginRequest.username(), e.getMessage());
            ErrorDetails errorDetails = new ErrorDetails(new Date(), "Authentication failed: " + e.getMessage(),
                    httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
        }
    }

    /**
     * Handles user registration by creating a new user account.
     * Validates the uniqueness of username and email before creating the user.
     * Automatically logs in the user after successful registration.
     * 
     * @param registerRequest The request containing user registration details.
     * 
     * @param httpRequest     The HTTP request.
     * @return ResponseEntity with the created user's login token and username or
     *         error details.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest,
            HttpServletRequest httpRequest) {

        if (userService.existsByUsername(registerRequest.username())) {
            logger.warn("Registration attempt failed: Username {} is already taken.", registerRequest.username());
            ErrorDetails errorDetails = new ErrorDetails(new Date(), "Username is already taken!",
                    httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
        }
        if (userService.existsByEmail(registerRequest.email())) {
            logger.warn("Registration attempt failed: Email {} is already in use.", registerRequest.email());
            ErrorDetails errorDetails = new ErrorDetails(new Date(), "Email is already in use!",
                    httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
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
     * @param request The HTTP request containing the JWT token in the
     *                Authorization
     *                header.
     * 
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
                if (jwtUtil.validateToken(jwt)) {
                    String username = jwtUtil.extractUsername(jwt);
                    tokenBlocklistService.addToBlocklist(jwt);
                    SecurityContextHolder.clearContext();
                    logger.info("User {} logged out successfully. Token JTI blocklisted.", username);
                    // Success can still return a JSON body if preferred, or a simple success
                    // message
                    ErrorDetails successDetails = new ErrorDetails(new Date(), "Successfully logged out.",
                            request.getRequestURI());
                    return ResponseEntity.ok(successDetails);
                } else {
                    logger.warn("Logout attempt with an invalid or expired token.");
                    ErrorDetails errorDetails = new ErrorDetails(new Date(),
                            "Invalid or expired token provided for logout.", request.getRequestURI());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(errorDetails);
                }
            } catch (Exception e) {
                logger.error("Error during logout process: {}", e.getMessage());
                ErrorDetails errorDetails = new ErrorDetails(new Date(), "Error during logout: " + e.getMessage(),
                        request.getRequestURI());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
            }
        }
        logger.warn("Logout attempt failed: No token provided or invalid format.");
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Logout failed. No token provided or invalid format.",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorDetails);
    }
}