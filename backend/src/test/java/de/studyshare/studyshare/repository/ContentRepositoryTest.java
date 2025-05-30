package de.studyshare.studyshare.repository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.studyshare.studyshare.AbstractDatabaseIntegrationTest;
import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;

@DataJpaTest
public class ContentRepositoryTest extends AbstractDatabaseIntegrationTest{

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContentRepository contentRepository;

    private User user;
    private Course course;
    private Faculty faculty;
    private Lecturer lecturer;
    private Content content1;
    private Content content2;

    @TestConfiguration
    @SuppressWarnings("unused")
    static class TestConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @BeforeEach
    public void setup() {
        // Create a user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("password123hash");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.STUDENT);
        entityManager.persist(user);
        
        // Create faculty
        faculty = new Faculty("Computer Science");
        entityManager.persist(faculty);
        
        // Create course
        course = new Course("Programming 101", faculty);
        entityManager.persist(course);
        
        // Create lecturer
        lecturer = new Lecturer("Professor Smith", "prof.smith@university.edu");
        entityManager.persist(lecturer);
        
        // Create first content
        content1 = new Content();
        content1.setTitle("Test Content 1");
        content1.setFilePath("/path/to/file1.pdf");
        content1.setContentCategory(ContentCategory.PDF);
        content1.setUploadedBy(user);
        content1.setUploadDate(LocalDate.now());
        content1.setReportedCount(0);
        content1.setOutdatedCount(0);
        content1.setCourse(course);
        content1.setFaculty(faculty);
        content1.setLecturer(lecturer);
        entityManager.persist(content1);
        
        // Create second content
        content2 = new Content();
        content2.setTitle("Test Content 2");
        content2.setFilePath("/path/to/file2.ppt");
        content2.setContentCategory(ContentCategory.IMAGE);
        content2.setUploadedBy(user);
        content2.setUploadDate(LocalDate.now().minusDays(1));
        content2.setReportedCount(1);
        content2.setOutdatedCount(2);
        content2.setCourse(course);
        content2.setFaculty(faculty);
        content2.setLecturer(null);  // Testing null lecturer
        entityManager.persist(content2);
        
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find all contents")
    public void shouldFindAllContents() {
        // When
        List<Content> contents = contentRepository.findAll();
        
        // Then
        assertThat(contents).isNotNull();
        assertThat(contents).hasSize(2);
        assertThat(contents.stream().map(Content::getTitle))
            .contains("Test Content 1", "Test Content 2");
    }

    @Test
    @DisplayName("Should find content by ID")
    public void shouldFindContentById() {
        // When
        Content found = contentRepository.findById(content1.getId()).orElse(null);
        
        // Then
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Test Content 1");
        assertThat(found.getFilePath()).isEqualTo("/path/to/file1.pdf");
        assertThat(found.getContentCategory()).isEqualTo(ContentCategory.PDF);
        assertThat(found.getUploadedBy().getUsername()).isEqualTo("testuser");
        assertThat(found.getCourse().getName()).isEqualTo("Programming 101");
        assertThat(found.getLecturer().getName()).isEqualTo("Professor Smith");
    }

    @Test
    @DisplayName("Should save new content")
    public void shouldSaveNewContent() {
        // Given
        Content newContent = new Content();
        newContent.setTitle("New Test Content");
        newContent.setFilePath("/path/to/new.pdf");
        newContent.setContentCategory(ContentCategory.PDF);
        newContent.setUploadedBy(user);
        newContent.setUploadDate(LocalDate.now());
        newContent.setReportedCount(0);
        newContent.setOutdatedCount(0);
        newContent.setCourse(course);
        newContent.setFaculty(faculty);
        
        // When
        Content savedContent = contentRepository.save(newContent);
        
        // Then
        assertThat(savedContent.getId()).isNotNull();
        
        Content retrievedContent = contentRepository.findById(savedContent.getId()).orElse(null);
        assertThat(retrievedContent).isNotNull();
        assertThat(retrievedContent.getTitle()).isEqualTo("New Test Content");
        assertThat(retrievedContent.getContentCategory()).isEqualTo(ContentCategory.PDF);
    }

    @Test
    @DisplayName("Should update content")
    public void shouldUpdateContent() {
        // When
        content1.setTitle("Updated Title");
        content1.setReportedCount(5);
        content1.setOutdatedCount(3);
        contentRepository.save(content1);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Content updatedContent = contentRepository.findById(content1.getId()).orElse(null);
        assertThat(updatedContent).isNotNull();
        assertThat(updatedContent.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedContent.getReportedCount()).isEqualTo(5);
        assertThat(updatedContent.getOutdatedCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should delete content")
    public void shouldDeleteContent() {
        // When
        contentRepository.delete(content1);
        entityManager.flush();
        
        // Then
        assertThat(contentRepository.findById(content1.getId())).isEmpty();
        assertThat(contentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update content relationships")
    public void shouldUpdateContentRelationships() {
        // Create a new lecturer
        Lecturer newLecturer = new Lecturer("Dr. Johnson", "dr.johnson@university.edu");
        entityManager.persist(newLecturer);
        
        // Create a new course
        Course newCourse = new Course("Advanced Programming", faculty);
        entityManager.persist(newCourse);
        
        // Update content relationships
        content2.setLecturer(newLecturer);
        content2.setCourse(newCourse);
        contentRepository.save(content2);
        entityManager.flush();
        entityManager.clear();
        
        // Verify
        Content updatedContent = contentRepository.findById(content2.getId()).orElse(null);
        assertThat(updatedContent).isNotNull();
        assertThat(updatedContent.getLecturer().getName()).isEqualTo("Dr. Johnson");
        assertThat(updatedContent.getCourse().getName()).isEqualTo("Advanced Programming");
    }

    @Test
    @DisplayName("Should increment reported count")
    public void shouldIncrementReportedCount() {
        // Get initial reported count
        int initialReportedCount = content1.getReportedCount();
        
        // Increment reported count
        content1.setReportedCount(initialReportedCount + 1);
        contentRepository.save(content1);
        entityManager.flush();
        entityManager.clear();
        
        // Verify
        Content updatedContent = contentRepository.findById(content1.getId()).orElse(null);
        assertThat(updatedContent).isNotNull();
        assertThat(updatedContent.getReportedCount()).isEqualTo(initialReportedCount + 1);
    }

    @Test
    @DisplayName("Should increment outdated count")
    public void shouldIncrementOutdatedCount() {
        // Get initial outdated count
        int initialOutdatedCount = content1.getOutdatedCount();
        
        // Increment outdated count
        content1.setOutdatedCount(initialOutdatedCount + 1);
        contentRepository.save(content1);
        entityManager.flush();
        entityManager.clear();
        
        // Verify
        Content updatedContent = contentRepository.findById(content1.getId()).orElse(null);
        assertThat(updatedContent).isNotNull();
        assertThat(updatedContent.getOutdatedCount()).isEqualTo(initialOutdatedCount + 1);
    }
}