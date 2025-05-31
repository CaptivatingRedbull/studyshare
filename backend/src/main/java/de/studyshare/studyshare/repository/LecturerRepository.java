package de.studyshare.studyshare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Lecturer;

/**
 * Repository interface for managing Lecturer entities.
 * Provides methods to perform CRUD operations and custom queries on Lecturer
 * data.
 */
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    /**
     * Checks if a lecturer with the given email exists.
     *
     * @param email the email of the lecturer
     * @return true if a lecturer with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a lecturer with the given email and a different ID exists.
     *
     * @param email      the email of the lecturer
     * @param lecturerId the ID of the lecturer to exclude from the check
     * @return true if a lecturer with the given email exists, excluding the
     *         specified ID, false otherwise
     */
    Optional<Lecturer> findByEmail(String email);

}
