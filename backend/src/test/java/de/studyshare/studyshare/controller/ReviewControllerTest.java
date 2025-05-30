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
import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.ReviewDTO;
import de.studyshare.studyshare.dto.request.ReviewCreateRequest;
import de.studyshare.studyshare.dto.request.ReviewUpdateRequest;
import de.studyshare.studyshare.repository.ContentRepository;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import de.studyshare.studyshare.repository.UserRepository;
import de.studyshare.studyshare.service.JpaUserDetailsService;
import de.studyshare.studyshare.service.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewControllerTest extends AbstractDatabaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentRepository contentRepository;

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
    private User anotherUser;
    private Faculty faculty;
    private Course course;
    private Lecturer lecturer;
    private Content content;

    private String adminUserJwt;
    private String testUserJwt;
    private String anotherUserJwt;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/contents";

        adminUser = new User("Admin", "User", "admin@example.com", "admin", passwordEncoder.encode("adminpass"),
                Role.ADMIN);
        userRepository.save(adminUser);
        adminUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(adminUser.getUsername()));

        testUser = new User("Test", "User", "testuser@example.com", "testuser", passwordEncoder.encode("password"),
                Role.STUDENT);
        userRepository.save(testUser);
        testUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(testUser.getUsername()));

        anotherUser = new User("Another", "User", "another@example.com", "another", passwordEncoder.encode("password2"),
                Role.STUDENT);
        userRepository.save(anotherUser);
        anotherUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(anotherUser.getUsername()));

        faculty = new Faculty("Engineering");
        facultyRepository.save(faculty);

        course = new Course("Algorithms", faculty);
        courseRepository.save(course);

        lecturer = new Lecturer("Dr. Smith", "smith@uni.edu");
        lecturerRepository.save(lecturer);

        content = new Content();
        content.setTitle("Lecture 1");
        content.setFilePath("/path/to/file.pdf");
        content.setContentCategory(ContentCategory.PDF);
        content.setUploadedBy(anotherUser); // testUser should not be uploader for review tests
        content.setCourse(course);
        content.setLecturer(lecturer);
        content.setFaculty(faculty);
        contentRepository.save(content);
    }

    private HttpHeaders jwtHeaders(String userJwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userJwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should create review as authenticated user")
    void createReview_authenticated() {
        ReviewCreateRequest req = new ReviewCreateRequest(5, "Great content", "Very helpful!");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> resp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().stars()).isEqualTo(5);
        assertThat(resp.getBody().subject()).isEqualTo("Great content");
        assertThat(resp.getBody().comment()).isEqualTo("Very helpful!");
    }

    @Test
    @DisplayName("Should not allow user to review own content")
    void createReview_ownContent_forbidden() {
        // testUser uploads content
        Content ownContent = new Content();
        ownContent.setTitle("My Own Content");
        ownContent.setFilePath("/path/to/own.pdf");
        ownContent.setContentCategory(ContentCategory.PDF);
        ownContent.setUploadedBy(testUser);
        ownContent.setCourse(course);
        ownContent.setLecturer(lecturer);
        ownContent.setFaculty(faculty);
        contentRepository.save(ownContent);

        ReviewCreateRequest req = new ReviewCreateRequest(4, "Self review", "Should not be allowed");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<String> resp = restTemplate.exchange(
                baseUrl + "/" + ownContent.getId() + "/reviews", HttpMethod.POST, entity, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should not allow duplicate review by same user")
    void createReview_duplicate_forbidden() {
        ReviewCreateRequest req = new ReviewCreateRequest(5, "First", "First review");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> resp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Try to create another review for same content by same user
        ReviewCreateRequest req2 = new ReviewCreateRequest(4, "Second", "Second review");
        HttpEntity<ReviewCreateRequest> entity2 = new HttpEntity<>(req2, jwtHeaders(testUserJwt));
        ResponseEntity<String> resp2 = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity2, String.class);
        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get all reviews for content as authenticated user")
    void getAllReviewsForContent_authenticated() {
        // Add a review first
        ReviewCreateRequest req = new ReviewCreateRequest(5, "Great content", "Very helpful!");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);

        HttpEntity<Void> getEntity = new HttpEntity<>(jwtHeaders(anotherUserJwt));
        ResponseEntity<ReviewDTO[]> resp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.GET, getEntity, ReviewDTO[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().length).isGreaterThanOrEqualTo(1);
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should get review by ID as authenticated user")
    void getReviewById_authenticated() {
        // Add a review first
        ReviewCreateRequest req = new ReviewCreateRequest(5, "Great content", "Very helpful!");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> createResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);

        @SuppressWarnings("null")
        Long reviewId = createResp.getBody().id();

        HttpEntity<Void> getEntity = new HttpEntity<>(jwtHeaders(anotherUserJwt));
        ResponseEntity<ReviewDTO> resp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews/" + reviewId, HttpMethod.GET, getEntity, ReviewDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().subject()).isEqualTo("Great content");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Should update review as owner")
    void updateReview_asOwner() {
        // Add a review first
        ReviewCreateRequest req = new ReviewCreateRequest(5, "Great content", "Very helpful!");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> createResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);

        @SuppressWarnings("null")
        Long reviewId = createResp.getBody().id();

        ReviewUpdateRequest updateReq = new ReviewUpdateRequest(3, "Updated subject", "Updated comment");
        HttpEntity<ReviewUpdateRequest> updateEntity = new HttpEntity<>(updateReq, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> updateResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews/" + reviewId, HttpMethod.PUT, updateEntity,
                ReviewDTO.class);

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResp.getBody()).isNotNull();
        assertThat(updateResp.getBody().stars()).isEqualTo(3);
        assertThat(updateResp.getBody().subject()).isEqualTo("Updated subject");
        assertThat(updateResp.getBody().comment()).isEqualTo("Updated comment");
    }

    @Test
    @DisplayName("Should not update review as non-owner")
    void updateReview_asNonOwner_forbidden() {
        // Add a review as testUser
        ReviewCreateRequest req = new ReviewCreateRequest(5, "Great content", "Very helpful!");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> createResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);

        @SuppressWarnings("null")
        Long reviewId = createResp.getBody().id();

        ReviewUpdateRequest updateReq = new ReviewUpdateRequest(2, "Hacked", "Should not work");
        HttpEntity<ReviewUpdateRequest> updateEntity = new HttpEntity<>(updateReq, jwtHeaders(anotherUserJwt));
        ResponseEntity<String> updateResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews/" + reviewId, HttpMethod.PUT, updateEntity, String.class);

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should delete review as owner")
    void deleteReview_asOwner() {
        // Add a review as testUser
        ReviewCreateRequest req = new ReviewCreateRequest(5, "Great content", "Very helpful!");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> createResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);

        @SuppressWarnings("null")
        Long reviewId = createResp.getBody().id();

        HttpEntity<Void> deleteEntity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<Void> deleteResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews/" + reviewId, HttpMethod.DELETE, deleteEntity, Void.class);

        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Confirm deletion
        HttpEntity<Void> getEntity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> getResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews/" + reviewId, HttpMethod.GET, getEntity, String.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should delete review as admin")
    void deleteReview_asAdmin() {
        ReviewCreateRequest req = new ReviewCreateRequest(5, "Great content", "Very helpful!");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> createResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);

        @SuppressWarnings("null")
        Long reviewId = createResp.getBody().id();

        HttpEntity<Void> deleteEntity = new HttpEntity<>(jwtHeaders(adminUserJwt));
        ResponseEntity<Void> deleteResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews/" + reviewId, HttpMethod.DELETE, deleteEntity, Void.class);

        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        HttpEntity<Void> getEntity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<String> getResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews/" + reviewId, HttpMethod.GET, getEntity, String.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should not delete review as non-owner")
    void deleteReview_asNonOwner_forbidden() {
        // Add a review as testUser
        ReviewCreateRequest req = new ReviewCreateRequest(5, "Great content", "Very helpful!");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> createResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);

        @SuppressWarnings("null")
        Long reviewId = createResp.getBody().id();

        HttpEntity<Void> deleteEntity = new HttpEntity<>(jwtHeaders(anotherUserJwt));
        ResponseEntity<String> deleteResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews/" + reviewId, HttpMethod.DELETE, deleteEntity,
                String.class);

        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should not create review with invalid data")
    void createReview_invalidData() {
        ReviewCreateRequest req = new ReviewCreateRequest(null, "", ""); // invalid stars and subject
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> resp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should not update review with invalid data")
    void updateReview_invalidData() {
        // Add a review first
        ReviewCreateRequest req = new ReviewCreateRequest(5, "Great content", "Very helpful!");
        HttpEntity<ReviewCreateRequest> entity = new HttpEntity<>(req, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> createResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews", HttpMethod.POST, entity, ReviewDTO.class);

        @SuppressWarnings("null")
        Long reviewId = createResp.getBody().id();

        ReviewUpdateRequest updateReq = new ReviewUpdateRequest(null, "", ""); // invalid stars and subject
        HttpEntity<ReviewUpdateRequest> updateEntity = new HttpEntity<>(updateReq, jwtHeaders(testUserJwt));
        ResponseEntity<ReviewDTO> updateResp = restTemplate.exchange(
                baseUrl + "/" + content.getId() + "/reviews/" + reviewId, HttpMethod.PUT, updateEntity,
                ReviewDTO.class);

        assertThat(updateResp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}