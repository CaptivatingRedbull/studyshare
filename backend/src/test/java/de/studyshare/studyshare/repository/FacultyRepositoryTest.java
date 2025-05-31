package de.studyshare.studyshare.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;

@DataJpaTest
public class FacultyRepositoryTest extends AbstractDatabaseIntegrationTest{
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private FacultyRepository facultyRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    private Faculty faculty1;
    private Faculty faculty2;
    private Faculty faculty3;
    
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
        // Clear any existing data
        facultyRepository.deleteAll();
        
        // Create faculties
        faculty1 = new Faculty("Computer Science");
        faculty2 = new Faculty("Mathematics");
        faculty3 = new Faculty("Engineering");
        
        // Save faculties
        entityManager.persist(faculty1);
        entityManager.persist(faculty2);
        entityManager.persist(faculty3);
        
        entityManager.flush();
    }
    
    @Test
    @DisplayName("Should find all faculties")
    public void shouldFindAllFaculties() {
        // When
        List<Faculty> faculties = facultyRepository.findAll();
        
        // Then
        assertThat(faculties).hasSize(3);
        assertThat(faculties).extracting("name")
            .containsExactlyInAnyOrder("Computer Science", "Mathematics", "Engineering");
    }
    
    @Test
    @DisplayName("Should find faculty by ID")
    public void shouldFindFacultyById() {
        // When
        Faculty found = facultyRepository.findById(faculty1.getId()).orElse(null);
        
        // Then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Computer Science");
    }
    
    @Test
    @DisplayName("Should find faculty by name")
    public void shouldFindFacultyByName() {
        // When
        Optional<Faculty> found = facultyRepository.findByName("Mathematics");
        Optional<Faculty> notFound = facultyRepository.findByName("Physics");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Mathematics");
        assertThat(notFound).isNotPresent();
    }
    
    @Test
    @DisplayName("Should check if faculty exists by name")
    public void shouldCheckIfFacultyExistsByName() {
        // When
        boolean exists = facultyRepository.existsByName("Computer Science");
        boolean notExists = facultyRepository.existsByName("Biology");
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
    
    @Test
    @DisplayName("Should save new faculty")
    public void shouldSaveNewFaculty() {
        // Given
        Faculty newFaculty = new Faculty("Physics");
        
        // When
        Faculty savedFaculty = facultyRepository.save(newFaculty);
        
        // Then
        assertThat(savedFaculty.getId()).isNotNull();
        
        Faculty retrievedFaculty = facultyRepository.findById(savedFaculty.getId()).orElse(null);
        assertThat(retrievedFaculty).isNotNull();
        assertThat(retrievedFaculty.getName()).isEqualTo("Physics");
    }
    
    @Test
    @DisplayName("Should update faculty")
    public void shouldUpdateFaculty() {
        // When
        faculty2.setName("Applied Mathematics");
        facultyRepository.save(faculty2);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Faculty updatedFaculty = facultyRepository.findById(faculty2.getId()).orElse(null);
        assertThat(updatedFaculty).isNotNull();
        assertThat(updatedFaculty.getName()).isEqualTo("Applied Mathematics");
    }
    
    @Test
    @DisplayName("Should delete faculty")
    public void shouldDeleteFaculty() {
        // When
        facultyRepository.delete(faculty3);
        entityManager.flush();
        
        // Then
        assertThat(facultyRepository.findById(faculty3.getId())).isEmpty();
        assertThat(facultyRepository.count()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should maintain relationships with courses")
    public void shouldMaintainRelationshipsWithCourses() {
        // Given
        Course course1 = new Course("Calculus", faculty2);
        Course course2 = new Course("Linear Algebra", faculty2);
        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.flush();
        entityManager.clear();
        
        // When
        Faculty retrievedFaculty = facultyRepository.findById(faculty2.getId()).orElse(null);
        Set<Course> courses = courseRepository.findAllByFacultyId(faculty2.getId());
        
        // Then
        assertThat(retrievedFaculty).isNotNull();
        assertThat(courses).hasSize(2);
        assertThat(courses).extracting("name")
            .containsExactlyInAnyOrder("Calculus", "Linear Algebra");
        assertThat(courses).extracting("faculty.id")
            .containsOnly(faculty2.getId());
    }
    
    @Test
    @DisplayName("Should handle many-to-many relationships through courses")
    public void shouldHandleManyToManyRelationshipsThroughCourses() {
        // Given
        Course course = new Course("Databases", faculty1);
        entityManager.persist(course);
        
        Lecturer lecturer1 = new Lecturer("John Doe", "john.doe@example.com");
        Lecturer lecturer2 = new Lecturer("Jane Smith", "jane.smith@example.com");
        entityManager.persist(lecturer1);
        entityManager.persist(lecturer2);
        
        course.addLecturer(lecturer1);
        course.addLecturer(lecturer2);
        entityManager.persist(course);
        entityManager.flush();
        entityManager.clear();
        
        // When
        Faculty retrievedFaculty = facultyRepository.findById(faculty1.getId()).orElse(null);
        Set<Course> courses = courseRepository.findAllByFacultyId(faculty1.getId());
        Course retrievedCourse = courses.iterator().next();
        
        // Then
        assertThat(retrievedFaculty).isNotNull();
        assertThat(courses).hasSize(1);
        assertThat(retrievedCourse.getLecturers()).hasSize(2);
        assertThat(retrievedCourse.getLecturers()).extracting("name")
            .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }
}