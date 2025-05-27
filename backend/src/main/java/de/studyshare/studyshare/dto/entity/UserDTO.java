package de.studyshare.studyshare.dto.entity;

import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;

/**
 * Data Transfer Object for User entities.
 * Contains user information for transfer between layers without exposing
 * sensitive data.
 */
public record UserDTO(
        /**
         * Unique identifier for the user.
         */
        Long id,

        /**
         * User's first name.
         */
        String firstName,

        /**
         * User's last name.
         */
        String lastName,

        /**
         * User's email address.
         */
        String email,

        /**
         * User's chosen username for the system.
         */
        String username,

        /**
         * Role assigned to the user (e.g., ADMIN, USER).
         */
        Role role) {

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user The User entity to convert
     * @return A new UserDTO with data from the entity, or null if the input is null
     */
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getRole());
    }
}