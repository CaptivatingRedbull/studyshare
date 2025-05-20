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
import de.studyshare.studyshare.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            final String token = jwtUtil.generateToken(authentication.getName());

            return ResponseEntity.ok(new LoginResponse(token, authentication.getName()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid username or password!");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Authentication failed! " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        if (userService.existsByUsername(registerRequest.username())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Username is already taken!");
        }
        if (userService.existsByEmail(registerRequest.email())) {
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

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userCreateRequest.username(), userCreateRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = jwtUtil.generateToken(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse(token, registeredUser.username()));

    }
    // TODO add serverside invalidation
    // TODO add token refreshing
}
