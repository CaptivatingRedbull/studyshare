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

import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.FacultyDTO;
import de.studyshare.studyshare.dto.request.FacultyCreateRequest;
import de.studyshare.studyshare.dto.request.FacultyUpdateRequest;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.UserRepository;
import de.studyshare.studyshare.service.JpaUserDetailsService;
import de.studyshare.studyshare.service.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JpaUserDetailsService jpaUserDetailsService;

    private String baseUrl;

    private User adminUser;
    private User testUser;
    private Faculty faculty;

    private String adminUserJwt;
    private String testUserJwt;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/faculties";

        adminUser = new User("Admin", "User", "admin@example.com", "admin", passwordEncoder.encode("adminpass"), Role.ADMIN);
        userRepository.save(adminUser);
        adminUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(adminUser.getUsername()));

        testUser = new User("Test", "User", "testuser@example.com", "testuser", passwordEncoder.encode("password"), Role.STUDENT);
        userRepository.save(testUser);
        testUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(testUser.getUsername()));

        faculty = new Faculty("Engineering");
        facultyRepository.save(faculty);
    }

    private HttpHeaders jwtHeaders(String userJwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userJwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @SuppressWarnings("null")
	@Test
    @DisplayName("Should get all faculties as authenticated user")
    void getAllFaculties_authenticated() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<FacultyDTO[]> resp = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, FacultyDTO[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().length).isGreaterThanOrEqualTo(1);
    }

    @SuppressWarnings("null")
	@Test
    @DisplayName("Should get faculty by ID as authenticated user")
    void getFacultyById_authenticated() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<FacultyDTO> resp = restTemplate.exchange(baseUrl + "/" + faculty.getId(), HttpMethod.GET, entity, FacultyDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo("Engineering");
    }

    @SuppressWarnings("null")
	@Test
    @DisplayName("Should create faculty as admin")
    void createFaculty_asAdmin() {
        FacultyCreateRequest req = new FacultyCreateRequest("Mathematics");
        HttpEntity<FacultyCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<FacultyDTO> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, FacultyDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo("Mathematics");
    }

    @Test
    @DisplayName("Should not create faculty as non-admin")
    void createFaculty_asNonAdmin_forbidden() {
        FacultyCreateRequest req = new FacultyCreateRequest("Physics");
        HttpEntity<FacultyCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("null")
	@Test
    @DisplayName("Should update faculty as admin")
    void updateFaculty_asAdmin() {
        FacultyUpdateRequest req = new FacultyUpdateRequest("Engineering Updated");
        HttpEntity<FacultyUpdateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<FacultyDTO> resp = restTemplate.exchange(baseUrl + "/" + faculty.getId(), HttpMethod.PUT, entity, FacultyDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo("Engineering Updated");
    }

    @Test
    @DisplayName("Should not update faculty as non-admin")
    void updateFaculty_asNonAdmin_forbidden() {
        FacultyUpdateRequest req = new FacultyUpdateRequest("Engineering Updated");
        HttpEntity<FacultyUpdateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + faculty.getId(), HttpMethod.PUT, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should delete faculty as admin")
    void deleteFaculty_asAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<Void> resp = restTemplate.exchange(baseUrl + "/" + faculty.getId(), HttpMethod.DELETE, entity, Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResp = restTemplate.exchange(baseUrl + "/" + faculty.getId(), HttpMethod.GET, entity, String.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should not delete faculty as non-admin")
    void deleteFaculty_asNonAdmin_forbidden() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + faculty.getId(), HttpMethod.DELETE, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should not create faculty with invalid data")
    void createFaculty_invalidData() {
        FacultyCreateRequest req = new FacultyCreateRequest("");
        HttpEntity<FacultyCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<FacultyDTO> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, FacultyDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}