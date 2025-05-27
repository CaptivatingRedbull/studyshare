package de.studyshare.studyshare.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;

/**
 * Repository interface for managing Course entities.
 * Provides methods to perform CRUD operations and custom queries on Course
 * data.
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Checks if a course with the given name and faculty exists.
     *
     * @param name    the name of the course
     * @param faculty the faculty to which the course belongs
     * @return true if a course with the given name and faculty exists, false
     *         otherwise
     */
    boolean existsByNameAndFaculty(String name, Faculty faculty);

    /**
     * Checks if a course with the given name, faculty, and a different ID exists.
     *
     * @param name     the name of the course
     * @param faculty  the faculty to which the course belongs
     * @param courseId the ID of the course to exclude from the check
     * @return true if a course with the given name and faculty exists, excluding
     *         the specified ID, false otherwise
     */
    boolean existsByNameAndFacultyAndIdNot(String name, Faculty faculty, Long courseId);

    /**
     * Checks if a course with the given faculty ID exists.
     *
     * @param facultyId the ID of the faculty to check
     * @return true if a course with the given faculty ID exists, false otherwise
     */
    boolean existsByFacultyId(Long facultyId);

    /**
     * Finds all courses associated with a specific faculty ID.
     *
     * @param facultyId the ID of the faculty to filter courses by
     * @return a set of Course entities associated with the specified faculty ID
     */
    Set<Course> findAllByFacultyId(Long facultyId);
}
