package de.studyshare.studyshare.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.studyshare.studyshare.AbstractDatabaseIntegrationTest;
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;

@DataJpaTest
public class LecturerRepositoryTest extends AbstractDatabaseIntegrationTest{

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private Lecturer lecturer1;
    private Lecturer lecturer2;
    private Lecturer lecturer3;

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
        lecturerRepository.deleteAll();
        courseRepository.deleteAll();
        facultyRepository.deleteAll();

        // Create test data
        lecturer1 = new Lecturer("John Doe", "john.doe@example.com");
        lecturer2 = new Lecturer("Jane Smith", "jane.smith@example.com");
        lecturer3 = new Lecturer("Alice Johnson", "alice@example.com");

        // Save test data
        lecturerRepository.saveAll(Arrays.asList(lecturer1, lecturer2, lecturer3));
    }

    @Test
    @DisplayName("Should find all lecturers")
    public void testFindAllLecturers() {
        // When
        List<Lecturer> lecturers = lecturerRepository.findAll();

        // Then
        assertThat(lecturers).hasSize(3);
        assertThat(lecturers).extracting("name").contains("John Doe", "Jane Smith", "Alice Johnson");
    }

    @Test
    @DisplayName("Should find lecturer by ID")
    public void testFindLecturerById() {
        // When
        Optional<Lecturer> foundLecturer = lecturerRepository.findById(lecturer1.getId());

        // Then
        assertThat(foundLecturer).isPresent();
        assertThat(foundLecturer.get().getName()).isEqualTo("John Doe");
        assertThat(foundLecturer.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should update lecturer")
    public void testUpdateLecturer() {
        // When
        lecturer2.setName("Jane Wilson");
        lecturer2.setEmail("jane.wilson@example.com");
        Lecturer updatedLecturer = lecturerRepository.save(lecturer2);

        // Then
        assertThat(updatedLecturer.getName()).isEqualTo("Jane Wilson");
        assertThat(updatedLecturer.getEmail()).isEqualTo("jane.wilson@example.com");

        // Verify in the database
        Optional<Lecturer> fromDb = lecturerRepository.findById(lecturer2.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getName()).isEqualTo("Jane Wilson");
    }

    @Test
    @DisplayName("Should delete lecturer")
    public void testDeleteLecturer() {
        // When
        lecturerRepository.delete(lecturer3);

        // Then
        assertThat(lecturerRepository.findById(lecturer3.getId())).isEmpty();
        assertThat(lecturerRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should check if lecturer exists by email")
    public void testExistsByEmail() {
        // When & Then
        assertThat(lecturerRepository.existsByEmail("john.doe@example.com")).isTrue();
        assertThat(lecturerRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should find lecturer by email")
    public void testFindByEmail() {
        // When
        Optional<Lecturer> foundLecturer = lecturerRepository.findByEmail("jane.smith@example.com");
        Optional<Lecturer> notFoundLecturer = lecturerRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundLecturer).isPresent();
        assertThat(foundLecturer.get().getName()).isEqualTo("Jane Smith");
        assertThat(notFoundLecturer).isEmpty();
    }

    @Test
    @DisplayName("Should save new lecturer")
    public void testSaveNewLecturer() {
        // Given
        Lecturer newLecturer = new Lecturer("Bob Brown", "bob.brown@example.com");

        // When
        Lecturer saved = lecturerRepository.save(newLecturer);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(lecturerRepository.count()).isEqualTo(4);
        assertThat(lecturerRepository.findByEmail("bob.brown@example.com")).isPresent();
    }

    @Test
    @DisplayName("Should establish many-to-many relationship between lecturer and course")
    public void testLecturerCourseRelationship() {
        // Given
        Faculty faculty = new Faculty("Computer Science");
        faculty = facultyRepository.save(faculty);

        Course course1 = new Course("Java Programming", faculty);
        Course course2 = new Course("Web Development", faculty);
        courseRepository.saveAll(Arrays.asList(course1, course2));

        // When - add courses to lecturer
        lecturer1.addCourse(course1);
        lecturer1.addCourse(course2);
        lecturerRepository.save(lecturer1);

        // Then - verify the relationship from both sides
        Lecturer savedLecturer = lecturerRepository.findById(lecturer1.getId()).get();
        assertThat(savedLecturer.getCourses()).hasSize(2);
        assertThat(savedLecturer.getCourses()).extracting("name").containsExactlyInAnyOrder("Java Programming", "Web Development");

        Course savedCourse1 = courseRepository.findById(course1.getId()).get();
        Course savedCourse2 = courseRepository.findById(course2.getId()).get();

        assertThat(savedCourse1.getLecturers()).hasSize(1);
        assertThat(savedCourse1.getLecturers()).extracting("name").contains("John Doe");
        assertThat(savedCourse2.getLecturers()).hasSize(1);
        assertThat(savedCourse2.getLecturers()).extracting("name").contains("John Doe");
    }

    @Test
    @DisplayName("Should properly remove course from lecturer relationship")
    public void testRemoveCourseFromLecturer() {
        // Given
        Faculty faculty = new Faculty("Computer Science");
        faculty = facultyRepository.save(faculty);

        Course course1 = new Course("Java Programming", faculty);
        Course course2 = new Course("Web Development", faculty);
        courseRepository.saveAll(Arrays.asList(course1, course2));

        lecturer1.addCourse(course1);
        lecturer1.addCourse(course2);
        lecturerRepository.save(lecturer1);

        // When - remove one course
        lecturer1.removeCourse(course1);
        lecturerRepository.save(lecturer1);

        // Then - verify course was removed from lecturer
        Lecturer refreshedLecturer = lecturerRepository.findById(lecturer1.getId()).get();
        assertThat(refreshedLecturer.getCourses()).hasSize(1);
        assertThat(refreshedLecturer.getCourses()).extracting("name").containsExactly("Web Development");

        // And lecturer was removed from course
        Course refreshedCourse1 = courseRepository.findById(course1.getId()).get();
        Course refreshedCourse2 = courseRepository.findById(course2.getId()).get();

        assertThat(refreshedCourse1.getLecturers()).isEmpty();
        assertThat(refreshedCourse2.getLecturers()).hasSize(1);
        assertThat(refreshedCourse2.getLecturers()).extracting("name").contains("John Doe");
    }

    @Test
    @DisplayName("Should properly establish relationship when adding lecturer to course")
    public void testAddLecturerToCourse() {
        // Given
        Faculty faculty = new Faculty("Computer Science");
        faculty = facultyRepository.save(faculty);

        Course course = new Course("Database Systems", faculty);
        course = courseRepository.save(course);

        // When - add lecturer to course
        course.addLecturer(lecturer1);
        courseRepository.save(course);

        // Then - verify bidirectional relationship is established
        Course savedCourse = courseRepository.findById(course.getId()).get();
        assertThat(savedCourse.getLecturers()).hasSize(1);
        assertThat(savedCourse.getLecturers()).extracting("name").contains("John Doe");

        Lecturer savedLecturer = lecturerRepository.findById(lecturer1.getId()).get();
        assertThat(savedLecturer.getCourses()).hasSize(1);
        assertThat(savedLecturer.getCourses()).extracting("name").contains("Database Systems");
    }

    @Test
    @DisplayName("Should find lecturers by course (alternative approach)")
    public void testFindLecturersByCourseAlternative() {
        // Given
        Faculty faculty = new Faculty("Computer Science");
        faculty = facultyRepository.save(faculty);

        Course course = new Course("Database Systems", faculty);
        course = courseRepository.save(course);

        lecturer1.addCourse(course);
        lecturer2.addCourse(course);
        lecturerRepository.saveAll(Arrays.asList(lecturer1, lecturer2));

        // When - fetch course and get lecturers from the relationship
        Course fetchedCourse = courseRepository.findById(course.getId()).orElseThrow();
        Set<Lecturer> lecturers = fetchedCourse.getLecturers();

        // Then
        assertThat(lecturers).hasSize(2);
        assertThat(lecturers).extracting("name").containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }

    @Test
    @DisplayName("Should return lecturers with pagination")
    public void testFindAllWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Lecturer> lecturerPage = lecturerRepository.findAll(pageable);

        // Then
        assertThat(lecturerPage.getTotalElements()).isEqualTo(3);
        assertThat(lecturerPage.getContent()).hasSize(2);
        assertThat(lecturerPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return lecturers sorted by name")
    public void testFindAllSortedByName() {
        // Given
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        // When
        List<Lecturer> lecturers = lecturerRepository.findAll(sort);

        // Then
        assertThat(lecturers).hasSize(3);
        assertThat(lecturers.get(0).getName()).isEqualTo("Alice Johnson");
        assertThat(lecturers.get(1).getName()).isEqualTo("Jane Smith");
        assertThat(lecturers.get(2).getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should handle finding by ID when repository is empty")
    public void testFindByIdWithEmptyRepository() {
        // Given
        lecturerRepository.deleteAll();

        // When/Then
        assertThat(lecturerRepository.findById(1L)).isEmpty();
    }

    @Test
    @DisplayName("Should maintain relationships when deleting a lecturer")
    public void testDeleteLecturerWithCourses() {
        // Given
        Faculty faculty = new Faculty("Computer Science");
        faculty = facultyRepository.save(faculty);

        Course course = new Course("Machine Learning", faculty);
        course = courseRepository.save(course);

        lecturer3.addCourse(course);
        lecturerRepository.save(lecturer3);

        // When - directly use repository instead of service
        course.removeLecturer(lecturer3);
        courseRepository.save(course);
        lecturerRepository.delete(lecturer3);

        // Then
        assertThat(lecturerRepository.findById(lecturer3.getId())).isEmpty();

        // Course should still exist but without the lecturer
        Course savedCourse = courseRepository.findById(course.getId()).get();
        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getLecturers()).isEmpty();
    }

    @Test
    @DisplayName("Should handle faculty deletion properly")
    public void testFacultyCascadeOperations() {
        // Given
        Faculty faculty = new Faculty("Computer Science");
        faculty = facultyRepository.save(faculty);

        Course course = new Course("Database Systems", faculty);
        course = courseRepository.save(course);

        lecturer1.addCourse(course);
        lecturerRepository.save(lecturer1);

        // When - directly use repository methods
        course.removeLecturer(lecturer1);
        courseRepository.save(course);
        courseRepository.delete(course);
        facultyRepository.delete(faculty);

        // Then
        assertThat(facultyRepository.findById(faculty.getId())).isEmpty();
        assertThat(courseRepository.findById(course.getId())).isEmpty();

        Lecturer refreshedLecturer = lecturerRepository.findById(lecturer1.getId()).get();
        assertThat(refreshedLecturer.getCourses()).isEmpty();
    }
}
