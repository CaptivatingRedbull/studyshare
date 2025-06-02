package de.studyshare.studyshare;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.ContentDTO;
import de.studyshare.studyshare.dto.entity.CourseDTO;
import de.studyshare.studyshare.dto.entity.FacultyDTO;
import de.studyshare.studyshare.dto.entity.LecturerDTO;
import de.studyshare.studyshare.dto.request.*;
import de.studyshare.studyshare.dto.response.LoginResponse;
import de.studyshare.studyshare.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Configuration
@Profile("dev") // Use a specific profile to control execution
public class TestDataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(TestDataGenerator.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private String baseUrl;

    private final Random random = new Random();

    public TestDataGenerator(
            ObjectMapper objectMapper, // Auto-configured by Spring Boot Web
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = new RestTemplate(); // Instantiate directly
    }

    @Bean
    public CommandLineRunner generateDataViaApi() {
        return args -> {
            this.baseUrl = "http://localhost:8080";
            logger.info("Starting Test Data Generation via API calls to {}", baseUrl);

            // 1. Create and login Admin User (directly in DB for bootstrapping)
            User adminUserEntity = userRepository.findByUsername("adminuser").orElseGet(() -> {
                User admin = new User("Admin", "Main", "admin@studyshare.com", "adminuser", passwordEncoder.encode("AdminPass123!"), Role.ADMIN);
                return userRepository.save(admin);
            });
            String adminToken = loginUserApi(adminUserEntity.getUsername(), "AdminPass123!");
            if (adminToken == null) {
                logger.error("Failed to log in admin user. Aborting data generation.");
                return;
            }
            logger.info("Admin user '{}' logged in.", adminUserEntity.getUsername());

            // 2. Register other users via API
            List<Map<String, String>> registeredUsersCredentials = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                String username = "student" + i;
                String email = "student" + i + "@studyshare.com";
                String password = "StudentPassword" + i + "!";
                RegisterRequest registerRequest = new RegisterRequest("Student" + i, "User" + i, email, username, password);
                try {
                    if (!userRepository.existsByUsername(username)) {
                        LoginResponse registeredUser = registerUserApi(registerRequest);
                        if (registeredUser != null) {
                            // Store credentials including the token obtained upon registration
                            registeredUsersCredentials.add(Map.of("username", username, "password", password, "token", registeredUser.token()));
                            logger.info("Registered and logged in user: {}", username);
                        }
                    } else {
                         logger.info("User {} already exists. Logging in.", username);
                         String token = loginUserApi(username, password);
                         if (token != null) {
                            registeredUsersCredentials.add(Map.of("username", username, "password", password, "token", token));
                         } else {
                            logger.warn("Could not log in existing user {}", username);
                         }
                    }
                } catch (HttpClientErrorException e) {
                    logger.error("Failed to register/login user {}: {} - {}", username, e.getStatusCode(), e.getResponseBodyAsString());
                }
            }
            if (registeredUsersCredentials.isEmpty()) {
                logger.error("No student users were available. Content creation might fail or be limited.");
                // Decide if to proceed or not. For now, we'll let it try.
            }

            // 3. Create Faculties
            List<FacultyDTO> faculties = new ArrayList<>();
            String[] facultyNames = {"Computer Science", "Mechanical Engineering", "Business Administration", "Arts & Humanities", "Medicine"};
            for (String name : facultyNames) {
                try {
                    FacultyCreateRequest req = new FacultyCreateRequest(name);
                    FacultyDTO faculty = createEntityApi(baseUrl + "/api/faculties", req, FacultyDTO.class, adminToken);
                    if (faculty != null) faculties.add(faculty);
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode() == HttpStatus.CONFLICT) {
                        logger.warn("Faculty '{}' already exists. Skipping creation.", name);
                         // Try to fetch existing if needed, or just log and continue
                    } else {
                        logger.error("Failed to create faculty {}: {} - {}", name, e.getStatusCode(), e.getResponseBodyAsString());
                    }
                }
            }
            if (faculties.size() < 3) logger.warn("Less than 3 faculties created. Expected at least 3.");


            // 4. Create Lecturers
            List<LecturerDTO> lecturers = new ArrayList<>();
            String[] lecturerNames = {"Dr. Ada Lovelace", "Prof. Alan Turing", "Dr. Grace Hopper", "Prof. Charles Babbage", "Dr. Tim Berners-Lee", "Prof. Edsger Dijkstra", "Dr. Marie Curie", "Prof. Richard Feynman"};
            for (int i = 0; i < 8; i++) { // Create 8 lecturers
                String name = lecturerNames[i % lecturerNames.length];
                String email = name.toLowerCase().replaceAll("[^a-z0-9]", "") + "@uni.example.com";
                 try {
                    LecturerCreateRequest req = new LecturerCreateRequest(name, email, Collections.emptySet());
                    LecturerDTO lecturer = createEntityApi(baseUrl + "/api/lecturers", req, LecturerDTO.class, adminToken);
                    if (lecturer != null) lecturers.add(lecturer);
                } catch (HttpClientErrorException e) {
                     if (e.getStatusCode() == HttpStatus.CONFLICT) {
                        logger.warn("Lecturer with email '{}' already exists. Skipping creation.", email);
                    } else {
                        logger.error("Failed to create lecturer {}: {} - {}", name, e.getStatusCode(), e.getResponseBodyAsString());
                    }
                }
            }
            if (lecturers.size() < 6) logger.warn("Less than 6 lecturers created. Expected at least 6.");


            // 5. Create Courses
            List<CourseDTO> courses = new ArrayList<>();
            String[] coursePrefixes = {"Intro to ", "Advanced ", "Principles of ", "Applied ", "Modern ", "Theoretical ", "Computational "};
            String[] courseThemes = {"Programming", "Algorithms", "Data Structures", "Web Development", "Machine Learning", "Calculus", "Linear Algebra", "Statistics", "Economics", "Marketing", "Literature", "History", "Physics", "Biology", "Chemistry"};

            for (int i = 0; i < 15; i++) { // Create 15 courses
                if (faculties.isEmpty()) {logger.error("No faculties to assign courses to."); break;}
                FacultyDTO faculty = faculties.get(random.nextInt(faculties.size()));
                
                Set<Long> lecturerIdsForCourse = new HashSet<>();
                if (!lecturers.isEmpty()) {
                    int numLecturers = 1 + random.nextInt(Math.min(2, lecturers.size())); // 1 to 2 lecturers
                    List<LecturerDTO> shuffledLecturers = new ArrayList<>(lecturers);
                    Collections.shuffle(shuffledLecturers);
                    for(int j=0; j < numLecturers && j < shuffledLecturers.size(); j++) {
                        lecturerIdsForCourse.add(shuffledLecturers.get(j).id());
                    }
                }

                String courseName = coursePrefixes[random.nextInt(coursePrefixes.length)] + courseThemes[random.nextInt(courseThemes.length)] + " " + (i + 101);
                try {
                    CourseCreateRequest req = new CourseCreateRequest(courseName, faculty.id(), lecturerIdsForCourse);
                    CourseDTO course = createEntityApi(baseUrl + "/api/courses", req, CourseDTO.class, adminToken);
                    if (course != null) courses.add(course);
                } catch (HttpClientErrorException e) {
                     if (e.getStatusCode() == HttpStatus.CONFLICT) {
                        logger.warn("Course '{}' in faculty '{}' already exists. Skipping creation.", courseName, faculty.name());
                    } else {
                        logger.error("Failed to create course {}: {} - {}", courseName, e.getStatusCode(), e.getResponseBodyAsString());
                    }
                }
            }
            if (courses.size() < 10) logger.warn("Less than 10 courses created. Expected at least 10.");


            // 6. Create Content
            List<ContentDTO> createdContents = new ArrayList<>();
            ContentCategory[] categories = ContentCategory.values();
            for (int i = 0; i < 50; i++) { // Create 50 content items
                if (registeredUsersCredentials.isEmpty() || courses.isEmpty() || faculties.isEmpty()) {
                    logger.warn("Skipping content creation due to missing prerequisities (users, courses, or faculties).");
                    break;
                }
                Map<String, String> uploader = registeredUsersCredentials.get(random.nextInt(registeredUsersCredentials.size()));
                String uploaderToken = uploader.get("token");
                
                CourseDTO course = courses.get(random.nextInt(courses.size()));
                FacultyDTO faculty = faculties.stream().filter(f -> f.id().equals(course.faculty().id())).findFirst().orElse(faculties.get(0));
                
                LecturerDTO lecturer = null;
                if (!course.lecturerIds().isEmpty()) {
                    Long randomLecturerId = new ArrayList<>(course.lecturerIds()).get(random.nextInt(course.lecturerIds().size()));
                    lecturer = lecturers.stream().filter(l -> l.id().equals(randomLecturerId)).findFirst().orElse(null);
                }
                 Long lecturerIdToUse = (lecturer != null) ? lecturer.id() : (lecturers.isEmpty() ? null : lecturers.get(random.nextInt(lecturers.size())).id());


                ContentCreateRequest contentRequest = new ContentCreateRequest(
                        categories[random.nextInt(categories.length)],
                        course.id(),
                        lecturerIdToUse,
                        faculty.id(),
                        "Material for " + course.name() + " - Item " + (i + 1)
                );

                try {
                    ContentDTO content = createContentApi(contentRequest, uploaderToken);
                    if (content != null) {
                        createdContents.add(content);
                        logger.info("Created content: {} (ID: {})", content.title(), content.id());
                    }
                } catch (Exception e) { // Catch broader exception for createContentApi due to multipart complexity
                    logger.error("Error creating content '{}': {}", contentRequest.title(), e.getMessage(), e);
                }
            }
             if (createdContents.size() < 40) {
                logger.warn("Less than 40 content items created. Created: {}", createdContents.size());
            }


            // 7. Create Reviews
            int reviewsToCreate = 25; // Increased for more coverage
            for (int i = 0; i < reviewsToCreate && !createdContents.isEmpty() && registeredUsersCredentials.size() > 0; i++) {
                ContentDTO contentToReview = createdContents.get(random.nextInt(createdContents.size()));
                
                List<Map<String, String>> potentialReviewers = registeredUsersCredentials.stream()
                    .filter(creds -> contentToReview.uploadedBy() == null || !creds.get("username").equals(contentToReview.uploadedBy().username()))
                    .collect(Collectors.toList());

                if (potentialReviewers.isEmpty()) {
                    logger.warn("No suitable reviewers for content ID {} (uploader: {}). Skipping review.", 
                                contentToReview.id(), contentToReview.uploadedBy() != null ? contentToReview.uploadedBy().username() : "N/A");
                    continue;
                }
                Map<String, String> reviewerCreds = potentialReviewers.get(random.nextInt(potentialReviewers.size()));
                String reviewerToken = reviewerCreds.get("token");

                ReviewCreateRequest reviewRequest = new ReviewCreateRequest(
                        ThreadLocalRandom.current().nextInt(1, 6), // Stars 1-5
                        "Review " + (i + 1) + " for " + contentToReview.title(),
                        "This is a sample review. Content seems " + (random.nextBoolean() ? "helpful." : "okay.")
                );
                try {
                    createReviewApi(contentToReview.id(), reviewRequest, reviewerToken);
                    logger.info("Created review for content ID: {} by {}", contentToReview.id(), reviewerCreds.get("username"));
                } catch (HttpClientErrorException e) {
                    logger.warn("Failed to create review for content ID {} by {}: {} - {}", contentToReview.id(), reviewerCreds.get("username"), e.getStatusCode(), e.getResponseBodyAsString());
                }
            }

            // 8. Mark some content as outdated
            int outdatedToMark = 7;
            Collections.shuffle(createdContents);
            for (int i = 0; i < outdatedToMark && i < createdContents.size() && !registeredUsersCredentials.isEmpty(); i++) {
                ContentDTO contentToMark = createdContents.get(i);
                Map<String, String> markerCreds = registeredUsersCredentials.get(random.nextInt(registeredUsersCredentials.size()));
                String markerToken = markerCreds.get("token");
                try {
                    markContentAsOutdatedApi(contentToMark.id(), markerToken);
                    logger.info("Marked content ID {} as outdated by {}", contentToMark.id(), markerCreds.get("username"));
                } catch (HttpClientErrorException e) {
                    logger.warn("Failed to mark content ID {} as outdated: {} - {}", contentToMark.id(), e.getStatusCode(), e.getResponseBodyAsString());
                }
            }

            logger.info("Test Data Generation via API calls finished.");
        };
    }

    private <T, R> R createEntityApi(String url, T requestPayload, Class<R> responseType, String token) throws HttpClientErrorException {
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);
        HttpEntity<T> requestEntity = new HttpEntity<>(requestPayload, headers);
        ResponseEntity<R> response = restTemplate.postForEntity(url, requestEntity, responseType);
        if (response.getStatusCode().is2xxSuccessful()) {
             logger.info("Successfully created entity at {}: {}", url, response.getBody());
        }
        return response.getBody();
    }
    
    @SuppressWarnings("null")
    private String loginUserApi(String username, String password) {
        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(loginRequest, jsonHeaders());
            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                    baseUrl + "/api/auth/login", requestEntity, LoginResponse.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().token();
            }
             logger.warn("Login attempt for {} returned status {} with body {}", username, response.getStatusCode(), response.getBody());
        } catch (HttpClientErrorException e) {
            logger.error("Login HTTP error for {}: {} - {}", username, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Login general error for {}: {}", username, e.getMessage(), e);
        }
        return null;
    }

    private LoginResponse registerUserApi(RegisterRequest registerRequest) throws HttpClientErrorException {
        HttpEntity<RegisterRequest> requestEntity = new HttpEntity<>(registerRequest, jsonHeaders());
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/auth/register", requestEntity, LoginResponse.class);
        return response.getBody();
    }


    private ContentDTO createContentApi(ContentCreateRequest contentMetadata, String token) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // Add metadata as JSON part - ensure correct Content-Type for this part
        HttpHeaders jsonPartHeaders = new HttpHeaders();
        jsonPartHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonPart = new HttpEntity<>(objectMapper.writeValueAsString(contentMetadata), jsonPartHeaders);
        body.add("contentData", jsonPart);

        // Add file part (simulated)
        String filename = (contentMetadata.title() != null ? contentMetadata.title().replaceAll("[^a-zA-Z0-9.-]", "_") : "file") + ".txt";
        ByteArrayResource fileResource = new ByteArrayResource(("This is dummy content for " + filename).getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<ContentDTO> response = restTemplate.exchange(
            baseUrl + "/api/contents",
            HttpMethod.POST,
            requestEntity,
            ContentDTO.class
        );
        return response.getBody();
    }
    
    private void createReviewApi(Long contentId, ReviewCreateRequest reviewRequest, String token) throws HttpClientErrorException {
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);
        HttpEntity<ReviewCreateRequest> requestEntity = new HttpEntity<>(reviewRequest, headers);
        restTemplate.postForEntity(
                baseUrl + "/api/contents/" + contentId + "/reviews", requestEntity, Void.class);
    }

    private void markContentAsOutdatedApi(Long contentId, String token) throws HttpClientErrorException {
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        restTemplate.postForEntity(
                baseUrl + "/api/contents/" + contentId + "/mark-outdated", requestEntity, Void.class);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}