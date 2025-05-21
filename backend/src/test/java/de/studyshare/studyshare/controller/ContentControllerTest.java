package de.studyshare.studyshare.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.web.util.UriComponentsBuilder;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.ContentDTO;
import de.studyshare.studyshare.dto.request.ContentCreateRequest;
import de.studyshare.studyshare.dto.request.ContentUpdateRequest;
import de.studyshare.studyshare.repository.ContentRepository;
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
    private ContentRepository contentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JpaUserDetailsService jpaUserDetailsService;

    private String baseUrl;
    private User testUser;
    private User adminUser;
    private String testUserJwt;
    private String adminUserJwt;

    // Test data entities
    private Faculty facultyEng;
    private Faculty facultyCS;
    private Course courseAlgo;
    private Course courseWebDev;
    private Course courseMath;
    private Lecturer lecturerSmith;
    private Lecturer lecturerDoe;
    private Content content1, content2, content3, content4, content5;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/contents"; // Corrected base URL

        // Setup Users
        testUser = new User("Test", "User", "testuser@example.com", "testuser",
                passwordEncoder.encode("password"), Role.STUDENT);
        userRepository.save(testUser);
        testUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(testUser.getUsername()));

        adminUser = new User("Admin", "User", "admin@example.com", "admin", passwordEncoder.encode("adminpass"),
                Role.ADMIN);
        userRepository.save(adminUser);
        adminUserJwt = jwtUtil.generateToken(jpaUserDetailsService.loadUserByUsername(adminUser.getUsername()));

        // Setup Faculties
        facultyEng = new Faculty("Engineering");
        facultyRepository.save(facultyEng);
        facultyCS = new Faculty("Computer Science");
        facultyRepository.save(facultyCS);

        // Setup Lecturers
        lecturerSmith = new Lecturer("Dr. Smith", "smith@uni.edu");
        lecturerRepository.save(lecturerSmith);
        lecturerDoe = new Lecturer("Prof. Doe", "doe@uni.edu");
        lecturerRepository.save(lecturerDoe);

        // Setup Courses
        courseAlgo = new Course("Algorithms", facultyCS);
        courseRepository.save(courseAlgo); // Save before adding lecturers
        courseWebDev = new Course("Web Development", facultyCS);
        courseRepository.save(courseWebDev);
        courseMath = new Course("Calculus", facultyEng);
        courseRepository.save(courseMath);

        // Associate lecturers with courses
        courseAlgo.addLecturer(lecturerDoe);
        courseWebDev.addLecturer(lecturerSmith);
        courseMath.addLecturer(lecturerSmith);
        courseRepository.saveAll(Arrays.asList(courseAlgo, courseWebDev, courseMath));
        lecturerRepository.saveAll(Arrays.asList(lecturerDoe, lecturerSmith)); // Save lecturers again after course
                                                                               // association

        // Setup Content items with diverse data
        content1 = new Content("Java Basics", " to Java programming.", ContentCategory.PDF, facultyCS, courseAlgo,
                lecturerDoe, testUser, LocalDate.now().minusDays(5), 0, 0);
        content2 = new Content("HTML & CSS Guide", "Guide for web frontend.", ContentCategory.PDF, facultyCS,
                courseWebDev, lecturerSmith, adminUser, LocalDate.now().minusDays(2), 1, 2);
        content3 = new Content("Calculus Cheat Sheet", "Important formulas for calculus.", ContentCategory.IMAGE,
                facultyEng, courseMath, lecturerSmith, testUser, LocalDate.now(), 1, 100);
        content4 = new Content("Advanced Algorithms", "Deep dive into complex algorithms.", ContentCategory.PDF,
                facultyCS, courseAlgo, lecturerDoe, adminUser, LocalDate.now().minusDays(10), 10, 0);
        content5 = new Content("Web App Security", "Security aspects of web applications.", ContentCategory.ZIP,
                facultyCS, courseWebDev, lecturerSmith, testUser, LocalDate.now().minusDays(1), 0, 0);

        contentRepository.saveAll(Arrays.asList(content1, content2, content3, content4, content5));

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
                courseAlgo.getId(), // courseId
                lecturerDoe.getId(), // lecturerId
                facultyCS.getId(), // facultyId
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
                courseAlgo.getId(), // courseId
                lecturerDoe.getId(), // lecturerId
                facultyCS.getId(), // facultyId
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
                courseAlgo.getId(), // courseId
                lecturerDoe.getId(), // lecturerId
                facultyCS.getId(), // facultyId
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
                courseAlgo.getId(), // courseId
                lecturerDoe.getId(), // lecturerId
                facultyCS.getId(), // facultyId
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
                courseAlgo.getId(), // courseId
                lecturerDoe.getId(), // lecturerId
                facultyCS.getId(), // facultyId
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
                courseAlgo.getId(), // courseId
                lecturerDoe.getId(), // lecturerId
                facultyCS.getId(), // facultyId
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
                courseAlgo.getId(), // courseId
                lecturerDoe.getId(), // lecturerId
                facultyCS.getId(), // facultyId
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
                courseAlgo.getId(), // courseId
                lecturerDoe.getId(), // lecturerId
                facultyCS.getId(), // facultyId
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
                courseAlgo.getId(), // courseId
                lecturerDoe.getId(), // lecturerId
                facultyCS.getId(), // facultyId
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

    @Test
    @DisplayName("[Browse] No filters - should return all sorted by date desc (default)")
    @SuppressWarnings("null")
    void browseContents_noFilters_shouldReturnAllSortedByDateDesc() {
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(baseUrl + "/browse", HttpMethod.GET, entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(5);
        List<ContentDTO> sortedContent = Arrays.stream(contents)
                .sorted(Comparator.comparing(ContentDTO::uploadDate).reversed())
                .collect(Collectors.toList());
        assertThat(Arrays.asList(contents)).containsExactlyElementsOf(sortedContent);
    }

    @Test
    @DisplayName("[Browse] Filter by facultyId")
    @SuppressWarnings("null")
    void browseContents_filterByFacultyId() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/browse")
                .queryParam("facultyId", facultyCS.getId());
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(4);
        assertTrue(Arrays.stream(contents).allMatch(c -> c.faculty().id().equals(facultyCS.getId())));
    }

    @Test
    @DisplayName("[Browse] Filter by courseId")
    @SuppressWarnings("null")
    void browseContents_filterByCourseId() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/browse")
                .queryParam("courseId", courseAlgo.getId());
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(2);
        assertTrue(Arrays.stream(contents).allMatch(c -> c.course().id().equals(courseAlgo.getId())));
    }

    @Test
    @DisplayName("[Browse] Filter by lecturerId")
    @SuppressWarnings("null")
    void browseContents_filterByLecturerId() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/browse")
                .queryParam("lecturerId", lecturerSmith.getId());
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(3);
        assertTrue(Arrays.stream(contents).allMatch(c -> Objects.equals(c.lecturer().id(), lecturerSmith.getId())));
    }

    @Test
    @DisplayName("[Browse] Filter by category")
    @SuppressWarnings("null")
    void browseContents_filterByCategory() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/browse")
                .queryParam("category", ContentCategory.PDF.toString());
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(3);
        assertTrue(Arrays.stream(contents).allMatch(c -> c.contentCategory() == ContentCategory.PDF));
    }

    @Test
    @DisplayName("[Browse] Filter by searchTerm in title")
    @SuppressWarnings("null")
    void browseContents_filterBySearchTerm_title() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/browse")
                .queryParam("searchTerm", "java");
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(1);
        assertThat(contents[0].title()).isEqualTo("Java Basics");
    }

    @Test
    @DisplayName("[Browse] Filter by multiple criteria (facultyCS and PDF)")
    @SuppressWarnings("null")
    void browseContents_filterByMultipleCriteria() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/browse")
                .queryParam("facultyId", facultyCS.getId())
                .queryParam("category", ContentCategory.PDF.toString());
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(3);
        assertTrue(Arrays.stream(contents).allMatch(
                c -> c.faculty().id().equals(facultyCS.getId()) && c.contentCategory() == ContentCategory.PDF));
    }

    @Test
    @DisplayName("[Browse] Sort by uploadDate ASC")
    @SuppressWarnings("null")
    void browseContents_sortByUploadDateAsc() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/browse")
                .queryParam("sortBy", "uploadDate")
                .queryParam("sortDirection", "asc");
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(5);
        assertThat(contents[0].uploadDate()).isEqualTo(content4.getUploadDate());
        assertThat(contents[4].uploadDate()).isEqualTo(content3.getUploadDate());
    }

    @Test
    @DisplayName("[Browse] Sort by title ASC")
    @SuppressWarnings("null")
    void browseContents_sortByTitleAsc() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/browse")
                .queryParam("sortBy", "title")
                .queryParam("sortDirection", "asc");
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(5);
        assertThat(contents[0].title()).isEqualTo("Advanced Algorithms");
        assertThat(contents[4].title()).isEqualTo("Web App Security");
    }

    @Test
    @DisplayName("[Browse] No results for filter combination")
    @SuppressWarnings("null")
    void browseContents_noResults() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/browse")
                .queryParam("facultyId", facultyEng.getId())
                .queryParam("category", ContentCategory.ZIP.toString());
        HttpEntity<Void> entity = new HttpEntity<>(jwtHeaders(testUserJwt));
        ResponseEntity<ContentDTO[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
                ContentDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContentDTO[] contents = response.getBody();
        assertThat(contents).isNotNull();
        assertThat(contents.length).isEqualTo(0);
    }

}