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

import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.ContentDTO;
import de.studyshare.studyshare.dto.request.ContentCreateRequest;
import de.studyshare.studyshare.dto.request.ContentUpdateRequest;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import de.studyshare.studyshare.repository.UserRepository;
import de.studyshare.studyshare.service.JpaUserDetailsService;
import de.studyshare.studyshare.service.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ContentControllerTest {
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

    private User testUser;
    private User adminUser;
    private Faculty faculty;
    private Course course;
    private Lecturer lecturer;

    private String testUserJwt;
    private String adminUserJwt;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/content";

        // Setup test data
        testUser = new User("Test", "User", "testuser@example.com", "testuser",
                passwordEncoder.encode("password"), Role.STUDENT);
        userRepository.save(testUser);
        String testUserJwt = jwtUtil
                .generateToken(jpaUserDetailsService.loadUserByUsername(testUser.getUsername()));
        this.testUserJwt = testUserJwt; // dont know why this is needed but when assigning it directly it is
                                        // null
        adminUser = new User("Admin", "User", "admin@example.com", "admin", passwordEncoder.encode("adminpass"),
                Role.ADMIN);
        userRepository.save(adminUser);
        String adminUserJwt = jwtUtil
                .generateToken(jpaUserDetailsService.loadUserByUsername(adminUser.getUsername()));
        this.adminUserJwt = adminUserJwt;

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
    @DisplayName("Should create and fetch content by ID")
    void createAndGetContentById() {
        ContentCreateRequest req = new ContentCreateRequest(
                ContentCategory.PDF, // contentCategory
                course.getId(), // courseId
                lecturer.getId(), // lecturerId
                faculty.getId(), // facultyId
                "/path/to/file.pdf", // filePath
                "Lecture 1" // title
        );

        HttpHeaders headers = jwtHeaders(testUserJwt);
        HttpEntity<ContentCreateRequest> createRequest = new HttpEntity<>(req, headers);

        ResponseEntity<ContentDTO> createResp = restTemplate
                .exchange(baseUrl, HttpMethod.POST, createRequest, ContentDTO.class);

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ContentDTO created = createResp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.title()).isEqualTo("Lecture 1");

        HttpEntity<Void> userAuthEntity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO> getResp = restTemplate
                .exchange(baseUrl + "/" + created.id(), HttpMethod.GET, userAuthEntity,
                        ContentDTO.class);

        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody()).isNotNull();
        assertThat(getResp.getBody().title()).isEqualTo("Lecture 1");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should update content as admin")
    void updateContent_asAdmin() {
        ContentCreateRequest req = new ContentCreateRequest(
                ContentCategory.PDF,
                course.getId(),
                lecturer.getId(),
                faculty.getId(),
                "/path/to/file2.pdf",
                "Lecture 2");

        HttpEntity<ContentCreateRequest> createReqAuth = new HttpEntity<>(req, jwtHeaders(adminUserJwt));
        ResponseEntity<ContentDTO> createResp = restTemplate
                .exchange(baseUrl, HttpMethod.POST, createReqAuth, ContentDTO.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResp.getBody()).isNotNull();
        assertThat(createResp.getBody().id()).isNotNull();

        ContentUpdateRequest updateReq = new ContentUpdateRequest(
                ContentCategory.PDF,
                course.getId(),
                lecturer.getId(),
                faculty.getId(),
                "/path/to/file2_updated.pdf",
                "Lecture 2 Updated");
        HttpEntity<ContentUpdateRequest> updateReqAuth = new HttpEntity<>(updateReq, jwtHeaders(adminUserJwt));

        @SuppressWarnings("null")
        ResponseEntity<ContentDTO> updateResp = restTemplate
                .exchange(baseUrl + "/" + createResp.getBody().id(), HttpMethod.PUT, updateReqAuth,
                        ContentDTO.class);

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResp.getBody().title()).isEqualTo("Lecture 2 Updated");
    }

    @Test
    @DisplayName("Should delete content as admin")
    void deleteContent_asAdmin() {
        ContentCreateRequest req = new ContentCreateRequest(
                ContentCategory.PDF,
                course.getId(),
                lecturer.getId(),
                faculty.getId(),
                "/path/to/file3.pdf",
                "Lecture 3");
        HttpHeaders userHeader = jwtHeaders(testUserJwt);
        HttpEntity<ContentCreateRequest> createRequestEntity = new HttpEntity<>(req, userHeader);
        ResponseEntity<ContentDTO> createResp = restTemplate
                .exchange(baseUrl, HttpMethod.POST, createRequestEntity, ContentDTO.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResp.getBody()).isNotNull();

        HttpEntity<Void> adminAuthEntity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        @SuppressWarnings("null")
        ResponseEntity<Void> delResp = restTemplate
                .exchange(baseUrl + "/" + createResp.getBody().id(), HttpMethod.DELETE, adminAuthEntity,
                        Void.class);

        assertThat(delResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        @SuppressWarnings("null")
        ResponseEntity<String> getResp = restTemplate
                .exchange(baseUrl + "/" + createResp.getBody().id(), HttpMethod.GET, adminAuthEntity,
                        String.class);

        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should increment report count")
    void reportContent() {
        ContentCreateRequest req = new ContentCreateRequest(
                ContentCategory.PDF,
                course.getId(),
                lecturer.getId(),
                faculty.getId(),
                "/path/to/file4.pdf",
                "Lecture 4");
        HttpHeaders userHeader = jwtHeaders(testUserJwt);
        HttpEntity<ContentCreateRequest> createRequestEntity = new HttpEntity<>(req, userHeader);
        ResponseEntity<ContentDTO> createResp = restTemplate
                .exchange(baseUrl, HttpMethod.POST, createRequestEntity, ContentDTO.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResp.getBody()).isNotNull();

        HttpEntity<Void> userAuthEntity = new HttpEntity<>(jwtHeaders(testUserJwt));
        @SuppressWarnings("null")
        ResponseEntity<ContentDTO> reportResp = restTemplate
                .exchange(baseUrl + "/" + createResp.getBody().id() + "/report", HttpMethod.POST,
                        userAuthEntity, ContentDTO.class);

        assertThat(reportResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reportResp.getBody()).isNotNull();
        assertThat(reportResp.getBody().reportedCount()).isEqualTo(1);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should increment outdated count")
    void markContentAsOutdated() {
        ContentCreateRequest req = new ContentCreateRequest(
                ContentCategory.PDF,
                course.getId(),
                lecturer.getId(),
                faculty.getId(),
                "/path/to/file5.pdf",
                "Lecture 5");
        HttpHeaders userHeader = jwtHeaders(testUserJwt);
        HttpEntity<ContentCreateRequest> createRequestEntity = new HttpEntity<>(req, userHeader);
        ResponseEntity<ContentDTO> createResp = restTemplate
                .exchange(baseUrl, HttpMethod.POST, createRequestEntity, ContentDTO.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResp.getBody()).isNotNull();
        assertThat(createResp.getBody().id()).isNotNull();

        HttpEntity<Void> userAuthEntity = new HttpEntity<>(jwtHeaders(testUserJwt));
        @SuppressWarnings("null")
        ResponseEntity<ContentDTO> outdatedResp = restTemplate
                .exchange(baseUrl + "/" + createResp.getBody().id() + "/mark-outdated", HttpMethod.POST,
                        userAuthEntity, ContentDTO.class);

        assertThat(outdatedResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(outdatedResp.getBody()).isNotNull();
        assertThat(outdatedResp.getBody().outdatedCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should not allow update by non-owner non-admin")
    void updateContent_notOwnerForbidden() {
        ContentCreateRequest req = new ContentCreateRequest(
                ContentCategory.PDF,
                course.getId(),
                lecturer.getId(),
                faculty.getId(),
                "/path/to/file6.pdf",
                "Lecture 6");
        HttpHeaders userHeader = jwtHeaders(testUserJwt);
        HttpEntity<ContentCreateRequest> createRequestEntity = new HttpEntity<>(req, userHeader);
        ResponseEntity<ContentDTO> createResp = restTemplate
                .exchange(baseUrl, HttpMethod.POST, createRequestEntity, ContentDTO.class);

        User otherUser = new User("Other", "User", "other@example.com", "other",
                passwordEncoder.encode("otherpass"), Role.STUDENT);
        userRepository.save(otherUser);
        String otherUserJwt = jwtUtil
                .generateToken(jpaUserDetailsService.loadUserByUsername(otherUser.getUsername()));

        ContentUpdateRequest updateReq = new ContentUpdateRequest(
                ContentCategory.PDF,
                course.getId(),
                lecturer.getId(),
                faculty.getId(),
                "/path/to/file6_updated.pdf",
                "Lecture 6 Updated");
        HttpEntity<ContentUpdateRequest> entity = new HttpEntity<>(updateReq, jwtHeaders(otherUserJwt));

        @SuppressWarnings("null")
        ResponseEntity<String> updateResp = restTemplate
                .exchange(baseUrl + "/" + createResp.getBody().id(), HttpMethod.PUT, entity,
                        String.class);

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should not allow delete by non-owner non-admin")
    void deleteContent_notOwnerForbidden() {
        ContentCreateRequest req = new ContentCreateRequest(
                ContentCategory.PDF,
                course.getId(),
                lecturer.getId(),
                faculty.getId(),
                "/path/to/file7.pdf",
                "Lecture 7");
        HttpHeaders userHeader = jwtHeaders(testUserJwt);
        HttpEntity<ContentCreateRequest> createRequestEntity = new HttpEntity<>(req, userHeader);
        ResponseEntity<ContentDTO> createResp = restTemplate
                .exchange(baseUrl, HttpMethod.POST, createRequestEntity, ContentDTO.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResp.getBody()).isNotNull();
        assertThat(createResp.getBody().id()).isNotNull();

        User otherUser = new User("Other2", "User", "other2@example.com", "other2",
                passwordEncoder.encode("other2pass"), Role.STUDENT);
        userRepository.save(otherUser);
        String otherUserJwt = jwtUtil
                .generateToken(jpaUserDetailsService.loadUserByUsername(otherUser.getUsername()));

        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(otherUserJwt));
        @SuppressWarnings("null")
        ResponseEntity<String> delResp = restTemplate
                .exchange(baseUrl + "/" + createResp.getBody().id(), HttpMethod.DELETE, entity,
                        String.class);

        assertThat(delResp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should not create content with invalid data")
    void createContent_invalidData() {
        // Missing required fields
        ContentCreateRequest req = new ContentCreateRequest(
                null, // ContentCategory
                null, // courseId
                null, // lecturerId
                null, // facultyId
                "", // filePath
                "" // title
        );
        HttpHeaders userHeader = jwtHeaders(testUserJwt);
        HttpEntity<ContentCreateRequest> createRequestEntity = new HttpEntity<>(req, userHeader);
        ResponseEntity<ContentDTO> createResp = restTemplate
                .exchange(baseUrl, HttpMethod.POST, createRequestEntity, ContentDTO.class);

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}