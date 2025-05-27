package de.studyshare.studyshare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.User;

/**
 * Repository interface for managing User entities.
 * Provides methods to perform CRUD operations and custom queries on User data.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a User by their email.
     *
     * @param email the email of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a User with the given email exists.
     *
     * @param email the email of the user
     * @return true if a User with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
