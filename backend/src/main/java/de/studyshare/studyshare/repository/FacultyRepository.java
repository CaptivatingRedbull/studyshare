package de.studyshare.studyshare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Faculty;

/**
 * Repository interface for managing Faculty entities.
 * Provides methods to perform CRUD operations and custom queries on Faculty
 * data.
 */
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    /**
     * Checks if a faculty with the given name exists.
     *
     * @param name the name of the faculty
     * @return true if a faculty with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if a faculty with the given name and a different ID exists.
     *
     * @param name      the name of the faculty
     * @param facultyId the ID of the faculty to exclude from the check
     * @return true if a faculty with the given name exists, excluding the specified
     *         ID, false otherwise
     */
    Optional<Faculty> findByName(String name);
}
