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

import de.studyshare.studyshare.AbstractDatabaseIntegrationTest;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.LecturerDTO;
import de.studyshare.studyshare.dto.request.LecturerCreateRequest;
import de.studyshare.studyshare.dto.request.LecturerUpdateRequest;
import de.studyshare.studyshare.repository.LecturerRepository;
import de.studyshare.studyshare.repository.UserRepository;
import de.studyshare.studyshare.service.JpaUserDetailsService;
import de.studyshare.studyshare.service.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LecturerControllerTest extends AbstractDatabaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JpaUserDetailsService jpaUserDetailsService;

    private String baseUrl;

    private User adminUser;
    private User testUser;
    private Lecturer lecturer;

    private String adminUserJwt;
    private String testUserJwt;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/lecturers";

        adminUser = new User("Admin", "User", "admin@example.com", "admin", passwordEncoder.encode("adminpass"),
                Role.ADMIN);
        userRepository.save(adminUser);
        adminUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(adminUser.getUsername()));

        testUser = new User("Test", "User", "testuser@example.com", "testuser", passwordEncoder.encode("password"),
                Role.STUDENT);
        userRepository.save(testUser);
        testUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(testUser.getUsername()));

        lecturer = new Lecturer("Dr. Smith", "smith@uni.edu");
        lecturerRepository.save(lecturer);
    }

    private HttpHeaders jwtHeaders(String userJwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userJwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get all lecturers as authenticated user")
    void getAllLecturers_authenticated() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<LecturerDTO[]> resp = restTemplate.exchange(baseUrl, HttpMethod.GET, entity,
                LecturerDTO[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().length).isGreaterThanOrEqualTo(1);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get lecturer by ID as authenticated user")
    void getLecturerById_authenticated() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<LecturerDTO> resp = restTemplate.exchange(baseUrl + "/" + lecturer.getId(), HttpMethod.GET,
                entity, LecturerDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo("Dr. Smith");
        assertThat(resp.getBody().email()).isEqualTo("smith@uni.edu");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should create lecturer as admin")
    void createLecturer_asAdmin() {
        LecturerCreateRequest req = new LecturerCreateRequest(
                "Dr. Jane",
                "jane@uni.edu",
                null // or Set.of(courseId) if you want to assign courses
        );
        HttpEntity<LecturerCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<LecturerDTO> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, LecturerDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo("Dr. Jane");
        assertThat(resp.getBody().email()).isEqualTo("jane@uni.edu");
    }

    @Test
    @DisplayName("Should not create lecturer as non-admin")
    void createLecturer_asNonAdmin_forbidden() {
        LecturerCreateRequest req = new LecturerCreateRequest(
                "Dr. John",
                "john@uni.edu",
                null);
        HttpEntity<LecturerCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should update lecturer as admin")
    void updateLecturer_asAdmin() {
        LecturerUpdateRequest req = new LecturerUpdateRequest(
                "Dr. Smith Updated",
                "smith_updated@uni.edu",
                null // or Set.of(courseId) if you want to assign courses
        );
        HttpEntity<LecturerUpdateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<LecturerDTO> resp = restTemplate.exchange(baseUrl + "/" + lecturer.getId(), HttpMethod.PUT,
                entity, LecturerDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo("Dr. Smith Updated");
        assertThat(resp.getBody().email()).isEqualTo("smith_updated@uni.edu");
    }

    @Test
    @DisplayName("Should not update lecturer as non-admin")
    void updateLecturer_asNonAdmin_forbidden() {
        LecturerUpdateRequest req = new LecturerUpdateRequest(
                "Dr. Smith Updated",
                "smith_updated@uni.edu",
                null);
        HttpEntity<LecturerUpdateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + lecturer.getId(), HttpMethod.PUT, entity,
                String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should delete lecturer as admin")
    void deleteLecturer_asAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<Void> resp = restTemplate.exchange(baseUrl + "/" + lecturer.getId(), HttpMethod.DELETE, entity,
                Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResp = restTemplate.exchange(baseUrl + "/" + lecturer.getId(), HttpMethod.GET, entity,
                String.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should not delete lecturer as non-admin")
    void deleteLecturer_asNonAdmin_forbidden() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + lecturer.getId(), HttpMethod.DELETE, entity,
                String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should not create lecturer with invalid data")
    void createLecturer_invalidData() {
        LecturerCreateRequest req = new LecturerCreateRequest(
                "", // invalid name
                "not-an-email", // invalid email
                null);
        HttpEntity<LecturerCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<LecturerDTO> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, LecturerDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}