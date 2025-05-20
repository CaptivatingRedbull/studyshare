package de.studyshare.studyshare.repository;

import java.util.List;
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

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;

@DataJpaTest
public class CourseRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private FacultyRepository facultyRepository;
    
    @Autowired
    private LecturerRepository lecturerRepository;
    
    private Faculty faculty1;
    private Faculty faculty2;
    private Course course1;
    private Course course2;
    private Course course3;
    private Lecturer lecturer1;
    private Lecturer lecturer2;
    
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
        courseRepository.deleteAll();
        facultyRepository.deleteAll();
        lecturerRepository.deleteAll();
        
        // Create faculties
        faculty1 = new Faculty("Computer Science");
        faculty2 = new Faculty("Mathematics");
        entityManager.persist(faculty1);
        entityManager.persist(faculty2);
        
        // Create courses
        course1 = new Course("Programming 101", faculty1);
        course2 = new Course("Web Development", faculty1);
        course3 = new Course("Linear Algebra", faculty2);
        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.persist(course3);
        
        // Create lecturers
        lecturer1 = new Lecturer("John Doe", "john.doe@example.com");
        lecturer2 = new Lecturer("Jane Smith", "jane.smith@example.com");
        entityManager.persist(lecturer1);
        entityManager.persist(lecturer2);
        
        // Add lecturers to courses
        course1.addLecturer(lecturer1);
        course2.addLecturer(lecturer1);
        course2.addLecturer(lecturer2);
        course3.addLecturer(lecturer2);
        
        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.persist(course3);
        
        entityManager.flush();
    }
    
    @Test
    @DisplayName("Should find all courses")
    public void shouldFindAllCourses() {
        // When
        List<Course> courses = courseRepository.findAll();
        
        // Then
        assertThat(courses).hasSize(3);
        assertThat(courses).extracting("name")
            .containsExactlyInAnyOrder("Programming 101", "Web Development", "Linear Algebra");
    }
    
    @Test
    @DisplayName("Should find course by ID")
    public void shouldFindCourseById() {
        // When
        Course found = courseRepository.findById(course1.getId()).orElse(null);
        
        // Then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Programming 101");
        assertThat(found.getFaculty().getName()).isEqualTo("Computer Science");
    }
    
    @Test
    @DisplayName("Should save new course")
    public void shouldSaveNewCourse() {
        // Given
        Course newCourse = new Course("Data Science", faculty1);
        
        // When
        Course savedCourse = courseRepository.save(newCourse);
        
        // Then
        assertThat(savedCourse.getId()).isNotNull();
        
        Course retrievedCourse = courseRepository.findById(savedCourse.getId()).orElse(null);
        assertThat(retrievedCourse).isNotNull();
        assertThat(retrievedCourse.getName()).isEqualTo("Data Science");
        assertThat(retrievedCourse.getFaculty().getName()).isEqualTo("Computer Science");
    }
    
    @Test
    @DisplayName("Should update course")
    public void shouldUpdateCourse() {
        // When
        course1.setName("Advanced Programming");
        courseRepository.save(course1);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Course updatedCourse = courseRepository.findById(course1.getId()).orElse(null);
        assertThat(updatedCourse).isNotNull();
        assertThat(updatedCourse.getName()).isEqualTo("Advanced Programming");
    }
    
    @Test
    @DisplayName("Should check if course exists by name and faculty")
    public void shouldCheckIfCourseExistsByNameAndFaculty() {
        // When
        boolean exists = courseRepository.existsByNameAndFaculty("Programming 101", faculty1);
        boolean notExists = courseRepository.existsByNameAndFaculty("Programming 101", faculty2);
        boolean nonExisting = courseRepository.existsByNameAndFaculty("Nonexistent Course", faculty1);
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        assertThat(nonExisting).isFalse();
    }
    
    @Test
    @DisplayName("Should check if course exists by name and faculty and not ID")
    public void shouldCheckIfCourseExistsByNameAndFacultyAndIdNot() {
        // Given
        Course duplicateCourse = new Course("Programming 101", faculty1);
        entityManager.persist(duplicateCourse);
        entityManager.flush();
        
        // When
        boolean exists = courseRepository.existsByNameAndFacultyAndIdNot("Programming 101", faculty1, duplicateCourse.getId());
        boolean notExists = courseRepository.existsByNameAndFacultyAndIdNot("Web Development", faculty1, course2.getId());
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
    
    @Test
    @DisplayName("Should check if courses exist by faculty ID")
    public void shouldCheckIfCoursesExistByFacultyId() {
        // When
        boolean exists = courseRepository.existsByFacultyId(faculty1.getId());
        
        // Create a new faculty with no courses
        Faculty emptyFaculty = new Faculty("Empty Faculty");
        entityManager.persist(emptyFaculty);
        boolean notExists = courseRepository.existsByFacultyId(emptyFaculty.getId());
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
    
    @Test
    @DisplayName("Should find all courses by faculty ID")
    public void shouldFindAllCoursesByFacultyId() {
        // When
        Set<Course> computerScienceCourses = courseRepository.findAllByFacultyId(faculty1.getId());
        Set<Course> mathCourses = courseRepository.findAllByFacultyId(faculty2.getId());
        
        // Then
        assertThat(computerScienceCourses).hasSize(2);
        assertThat(computerScienceCourses).extracting("name")
            .containsExactlyInAnyOrder("Programming 101", "Web Development");
            
        assertThat(mathCourses).hasSize(1);
        assertThat(mathCourses).extracting("name").containsExactly("Linear Algebra");
    }
    
    @Test
    @DisplayName("Should manage course-lecturer relationship")
    public void shouldManageCourseAndLecturerRelationship() {
        // Given
        Lecturer newLecturer = new Lecturer("Bob Brown", "bob.brown@example.com");
        entityManager.persist(newLecturer);
        
        // When - add lecturer to course
        course3.addLecturer(newLecturer);
        courseRepository.save(course3);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Course refreshedCourse = courseRepository.findById(course3.getId()).orElse(null);
        assertThat(refreshedCourse).isNotNull();
        assertThat(refreshedCourse.getLecturers()).hasSize(2);
        assertThat(refreshedCourse.getLecturers()).extracting("name")
            .containsExactlyInAnyOrder("Jane Smith", "Bob Brown");
            
        // When - remove lecturer from course
        refreshedCourse.removeLecturer(lecturer2);
        courseRepository.save(refreshedCourse);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Course updatedCourse = courseRepository.findById(course3.getId()).orElse(null);
        assertThat(updatedCourse).isNotNull();
        assertThat(updatedCourse.getLecturers()).hasSize(1);
        assertThat(updatedCourse.getLecturers()).extracting("name").containsExactly("Bob Brown");
        
        // Verify lecturer still exists
        Lecturer lecturer = lecturerRepository.findById(lecturer2.getId()).orElse(null);
        assertThat(lecturer).isNotNull();
    }
}