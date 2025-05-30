package de.studyshare.studyshare.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

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
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.CourseDTO;
import de.studyshare.studyshare.dto.request.CourseCreateRequest;
import de.studyshare.studyshare.dto.request.CourseUpdateRequest;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import de.studyshare.studyshare.repository.UserRepository;
import de.studyshare.studyshare.service.JpaUserDetailsService;
import de.studyshare.studyshare.service.JwtUtil;
import jakarta.transaction.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CourseControllerTest extends AbstractDatabaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private CourseRepository courseRepository;

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
    private Faculty faculty;
    private Course course;
    private Lecturer lecturer;

    private String adminUserJwt;
    private String testUserJwt;

    @BeforeEach
    @Transactional
    void setUp2() {
        baseUrl = "http://localhost:" + port + "/api/courses";

        adminUser = new User("Admin", "User", "admin@example.com", "admin", passwordEncoder.encode("adminpass"),
                Role.ADMIN);
        userRepository.save(adminUser);
        adminUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(adminUser.getUsername()));

        testUser = new User("Test", "User", "testuser@example.com", "testuser", passwordEncoder.encode("password"),
                Role.STUDENT);
        userRepository.save(testUser);
        testUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(testUser.getUsername()));

        faculty = new Faculty("Engineering");
        facultyRepository.save(faculty);

        course = new Course("Algorithms", faculty);
        courseRepository.save(course);

        lecturer = new Lecturer("Dr. Smith", "smith@uni.edu");
        lecturerRepository.save(lecturer);
        lecturer.addCourse(course);
        course.addLecturer(lecturer);
        courseRepository.save(course);
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
    @DisplayName("Should get all courses as authenticated user")
    void getAllCourses_authenticated() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<CourseDTO[]> resp = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, CourseDTO[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().length).isGreaterThanOrEqualTo(1);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get course by ID as authenticated user")
    void getCourseById_authenticated() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<CourseDTO> resp = restTemplate.exchange(baseUrl + "/" + course.getId(), HttpMethod.GET, entity,
                CourseDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo("Algorithms");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should create course as admin")
    void createCourse_asAdmin() {
        CourseCreateRequest req = new CourseCreateRequest(
                "Data Structures",
                faculty.getId(),
                Set.of(lecturer.getId()));
        HttpEntity<CourseCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<CourseDTO> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, CourseDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo("Data Structures");
    }

    @Test
    @DisplayName("Should not create course as non-admin")
    void createCourse_asNonAdmin_forbidden() {
        CourseCreateRequest req = new CourseCreateRequest(
                "Operating Systems",
                faculty.getId(),
                Set.of(lecturer.getId()));
        HttpEntity<CourseCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should update course as admin")
    void updateCourse_asAdmin() {
        CourseUpdateRequest req = new CourseUpdateRequest(
                "Algorithms Updated",
                faculty.getId(),
                Set.of(lecturer.getId()));
        HttpEntity<CourseUpdateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<CourseDTO> resp = restTemplate.exchange(baseUrl + "/" + course.getId(), HttpMethod.PUT, entity,
                CourseDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().name()).isEqualTo("Algorithms Updated");
    }

    @Test
    @DisplayName("Should not update course as non-admin")
    void updateCourse_asNonAdmin_forbidden() {
        CourseUpdateRequest req = new CourseUpdateRequest(
                "Algorithms Updated",
                faculty.getId(),
                Set.of(lecturer.getId()));
        HttpEntity<CourseUpdateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + course.getId(), HttpMethod.PUT, entity,
                String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should delete course as admin")
    void deleteCourse_asAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<Void> resp = restTemplate.exchange(baseUrl + "/" + course.getId(), HttpMethod.DELETE, entity,
                Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResp = restTemplate.exchange(baseUrl + "/" + course.getId(), HttpMethod.GET, entity,
                String.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should not delete course as non-admin")
    void deleteCourse_asNonAdmin_forbidden() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + course.getId(), HttpMethod.DELETE, entity,
                String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should add lecturer to course as admin")
    void addLecturerToCourse_asAdmin() {
        Lecturer newLecturer = new Lecturer("Dr. Jane", "jane@uni.edu");
        lecturerRepository.save(newLecturer);

        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<CourseDTO> resp = restTemplate.exchange(
                baseUrl + "/" + course.getId() + "/lecturers/" + newLecturer.getId(),
                HttpMethod.POST, entity, CourseDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().lecturerIds()).contains(newLecturer.getId());
    }

    @Test
    @DisplayName("Should not add lecturer to course as non-admin")
    void addLecturerToCourse_asNonAdmin_forbidden() {
        Lecturer newLecturer = new Lecturer("Dr. Jane", "jane@uni.edu");
        lecturerRepository.save(newLecturer);

        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(
                baseUrl + "/" + course.getId() + "/lecturers/" + newLecturer.getId(),
                HttpMethod.POST, entity, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should remove lecturer from course as admin")
    void removeLecturerFromCourse_asAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<CourseDTO> resp = restTemplate.exchange(
                baseUrl + "/" + course.getId() + "/lecturers/" + lecturer.getId(),
                HttpMethod.DELETE, entity, CourseDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().lecturerIds()).doesNotContain(lecturer.getId());
    }

    @Test
    @DisplayName("Should not remove lecturer from course as non-admin")
    void removeLecturerFromCourse_asNonAdmin_forbidden() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(
                baseUrl + "/" + course.getId() + "/lecturers/" + lecturer.getId(),
                HttpMethod.DELETE, entity, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should not create course with invalid data")
    void createCourse_invalidData() {
        // Missing required fields
        CourseCreateRequest req = new CourseCreateRequest(
                "", // name
                null, // facultyId
                null // lecturerIds
        );
        HttpEntity<CourseCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<CourseDTO> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, CourseDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}