package de.studyshare.studyshare.controller;

import static org.assertj.core.api.Assertions.assertThat;



import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.UserDTO;
import de.studyshare.studyshare.dto.request.UserUpdateRequest;
import de.studyshare.studyshare.repository.UserRepository;
import de.studyshare.studyshare.service.JpaUserDetailsService;
import de.studyshare.studyshare.service.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JpaUserDetailsService jpaUserDetailsService;

    private String baseUrl;

    private User adminUser;
    private User testUser;
    private User anotherUser;

    private String adminUserJwt;
    private String testUserJwt;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";

        adminUser = new User("Admin", "User", "admin@example.com", "admin", passwordEncoder.encode("adminpass"), Role.ADMIN);
        userRepository.save(adminUser);
        adminUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(adminUser.getUsername()));

        testUser = new User("Test", "User", "testuser@example.com", "testuser", passwordEncoder.encode("password"), Role.STUDENT);
        userRepository.save(testUser);
        testUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(testUser.getUsername()));

        anotherUser = new User("Another", "User", "another@example.com", "another", passwordEncoder.encode("password2"), Role.STUDENT);
        userRepository.save(anotherUser);
    }

    private HttpHeaders jwtHeaders(String userJwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userJwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get all users as admin")
    void getAllUsers_asAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<UserDTO[]> resp = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, UserDTO[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().length).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should not get all users as non-admin")
    void getAllUsers_asNonAdmin_forbidden() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get user by ID as admin")
    void getUserById_asAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<UserDTO> resp = restTemplate.exchange(baseUrl + "/" + testUser.getId(), HttpMethod.GET, entity, UserDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().username()).isEqualTo("testuser");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get own user by ID as user")
    void getUserById_asSelf() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<UserDTO> resp = restTemplate.exchange(baseUrl + "/" + testUser.getId(), HttpMethod.GET, entity, UserDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().username()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should not get other user by ID as non-admin")
    void getUserById_asOtherUser_forbidden() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + anotherUser.getId(), HttpMethod.GET, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get user by username as admin")
    void getUserByUsername_asAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<UserDTO> resp = restTemplate.exchange(baseUrl + "/byUsername?username=testuser", HttpMethod.GET, entity, UserDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().username()).isEqualTo("testuser");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get own user by username as user")
    void getUserByUsername_asSelf() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<UserDTO> resp = restTemplate.exchange(baseUrl + "/byUsername?username=testuser", HttpMethod.GET, entity, UserDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().username()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should not get other user by username as non-admin")
    void getUserByUsername_asOtherUser_forbidden() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/byUsername?username=another", HttpMethod.GET, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should update user as admin")
    void updateUser_asAdmin() {
        UserUpdateRequest req = new UserUpdateRequest(
            "Test",
            "User",
            "testuser@example.com"
        );
        HttpEntity<UserUpdateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<UserDTO> resp = restTemplate.exchange(baseUrl + "/" + testUser.getId(), HttpMethod.PUT, entity, UserDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().username()).isEqualTo("testuser");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should update own user as user")
    void updateUser_asSelf() {
        UserUpdateRequest req = new UserUpdateRequest(
            "Test",
            "User",
            "testuser@example.com"
        );
        HttpEntity<UserUpdateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<UserDTO> resp = restTemplate.exchange(baseUrl + "/" + testUser.getId(), HttpMethod.PUT, entity, UserDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().username()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should not update other user as non-admin")
    void updateUser_asOtherUser_forbidden() {
        UserUpdateRequest req = new UserUpdateRequest(
            "Another",
            "User",
            "another@example.com"
        );
        HttpEntity<UserUpdateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + anotherUser.getId(), HttpMethod.PUT, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should delete user as admin")
    void deleteUser_asAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<Void> resp = restTemplate.exchange(baseUrl + "/" + testUser.getId(), HttpMethod.DELETE, entity, Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResp = restTemplate.exchange(baseUrl + "/" + testUser.getId(), HttpMethod.GET, entity, String.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should delete own user as user")
    void deleteUser_asSelf() {
        long id = testUser.getId();
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        HttpEntity<Void> entityAdmin = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<Void> resp = restTemplate.exchange(baseUrl + "/" + testUser.getId(), HttpMethod.DELETE, entity, Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResp = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.GET, entityAdmin, String.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should not delete other user as non-admin")
    void deleteUser_asOtherUser_forbidden() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + anotherUser.getId(), HttpMethod.DELETE, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}