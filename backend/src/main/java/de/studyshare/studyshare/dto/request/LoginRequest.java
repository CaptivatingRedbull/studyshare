package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for user login.
 * Contains the necessary fields for a user to log in to the system.
 */
public record LoginRequest(
        /**
         * The username of the user attempting to log in.
         * Cannot be blank.
         */
        @NotBlank(message = "Username cannot be blank") String username,
        /**
         * The password of the user attempting to log in.
         * Cannot be blank.
         */
        @NotBlank(message = "Password cannot be blank") String password) {

}
