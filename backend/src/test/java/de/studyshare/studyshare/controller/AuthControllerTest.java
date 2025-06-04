package de.studyshare.studyshare.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import de.studyshare.studyshare.AbstractDatabaseIntegrationTest;
import de.studyshare.studyshare.dto.entity.LecturerDTO;
import de.studyshare.studyshare.dto.request.LoginRequest;
import de.studyshare.studyshare.dto.request.RegisterRequest;
import de.studyshare.studyshare.dto.response.LoginResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerTest extends AbstractDatabaseIntegrationTest{

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/auth";
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should register a new user")
    void registerUser_success() {
        RegisterRequest req = new RegisterRequest(
            "Test",
            "User",
            "testuser@example.com",
            "testuser",
            "password"
        );
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(req, jsonHeaders());
        ResponseEntity<LoginResponse> resp = restTemplate.exchange(
            baseUrl() + "/register", HttpMethod.POST, entity, LoginResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().username()).isEqualTo("testuser");
        assertThat(resp.getBody().token()).isNotBlank();
    }

    @Test
    @DisplayName("Should not register with duplicate username")
    void registerUser_duplicateUsername() {
        // First registration
        RegisterRequest req = new RegisterRequest(
            "Test",
            "User",
            "testuser@example.com",
            "testuser",
            "password"
        );
        restTemplate.exchange(baseUrl() + "/register", HttpMethod.POST, new HttpEntity<>(req, jsonHeaders()), LoginResponse.class);

        // Second registration with same username
        RegisterRequest req2 = new RegisterRequest(
            "Test2",
            "User2",
            "testuser2@example.com",
            "testuser",
            "password2"
        );
        HttpEntity<RegisterRequest> entity2 = new HttpEntity<>(req2, jsonHeaders());
        ResponseEntity<String> resp2 = restTemplate.exchange(
            baseUrl() + "/register", HttpMethod.POST, entity2, String.class);

        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp2.getBody()).contains("Username is already taken");
    }

    @Test
    @DisplayName("Should not register with duplicate email")
    void registerUser_duplicateEmail() {
        // First registration
        RegisterRequest req = new RegisterRequest(
            "Test",
            "User",
            "testuser@example.com",
            "testuser",
            "password"
        );
        restTemplate.exchange(baseUrl() + "/register", HttpMethod.POST, new HttpEntity<>(req, jsonHeaders()), LoginResponse.class);

        // Second registration with same email
        RegisterRequest req2 = new RegisterRequest(
            "Test2",
            "User2",
            "testuser@example.com",
            "testuser2",
            "password2"
        );
        HttpEntity<RegisterRequest> entity2 = new HttpEntity<>(req2, jsonHeaders());
        ResponseEntity<String> resp2 = restTemplate.exchange(
            baseUrl() + "/register", HttpMethod.POST, entity2, String.class);

        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp2.getBody()).contains("Email is already in use");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should login with valid credentials")
    void login_success() {
        // Register first
        RegisterRequest registerReq = new RegisterRequest(
            "Test",
            "User",
            "testuser@example.com",
            "testuser",
            "password"
        );
        restTemplate.exchange(baseUrl() + "/register", HttpMethod.POST, new HttpEntity<>(registerReq, jsonHeaders()), LoginResponse.class);

        // Login
        LoginRequest loginReq = new LoginRequest("testuser", "password");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginReq, jsonHeaders());
        ResponseEntity<LoginResponse> resp = restTemplate.exchange(
            baseUrl() + "/login", HttpMethod.POST, entity, LoginResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().username()).isEqualTo("testuser");
        assertThat(resp.getBody().token()).isNotBlank();
    }

    @Test
    @DisplayName("Should not login with invalid credentials")
    void login_invalidCredentials() {
        LoginRequest loginReq = new LoginRequest("notexist", "wrongpass");
        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginReq, jsonHeaders());
        ResponseEntity<String> resp = restTemplate.exchange(
            baseUrl() + "/login", HttpMethod.POST, entity, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resp.getBody()).contains("Invalid username or password");
    }

    @Test
    @DisplayName("Should not register with invalid data")
    void registerUser_invalidData() {
        RegisterRequest req = new RegisterRequest(
            "", // invalid first name
            "", // invalid last name
            "not-an-email", // invalid email
            "", // invalid username
            "" // invalid password
        );
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(req, jsonHeaders());
        ResponseEntity<String> resp = restTemplate.exchange(
            baseUrl() + "/register", HttpMethod.POST, entity, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should not authenticate with invalid token")
    void request_invalidToken(){
        RegisterRequest registerReq = new RegisterRequest(
            "Test",
            "User",
            "testuser@example.com",
            "testuser",
            "password"
        );
        restTemplate.exchange(baseUrl() + "/register", HttpMethod.POST, new HttpEntity<>(registerReq, jsonHeaders()), LoginResponse.class);

        LoginRequest loginReq = new LoginRequest(registerReq.username(), registerReq.password());
        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginReq, jsonHeaders());

        ResponseEntity<LoginResponse> resp = restTemplate.exchange(
            baseUrl() + "/login", HttpMethod.POST, entity, LoginResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().token()).isNotBlank();

        String token = resp.getBody().token();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<?> logoutResp = restTemplate.exchange(
            baseUrl() + "/logout", HttpMethod.POST, new HttpEntity<>(null, headers), Void.class);
        assertThat(logoutResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<LecturerDTO[]> invalidTokenResp = restTemplate.exchange(
            "http://localhost:" + port + "/api/lecturers", HttpMethod.GET, new HttpEntity<>(null, headers), LecturerDTO[].class);
        assertThat(invalidTokenResp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }
}