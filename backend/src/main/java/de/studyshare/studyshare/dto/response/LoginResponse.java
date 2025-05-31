package de.studyshare.studyshare.dto.response; // It's good practice to have a separate 'response' sub-package for DTOs

/**
 * Response DTO for user login.
 * Contains the token and username of the logged-in user.
 */
public record LoginResponse(
        /**
         * The authentication token for the user.
         * This token is used for subsequent requests to authenticate the user.
         */
        String token,
        /**
         * The username of the logged-in user.
         * This is used to identify the user in the system.
         */
        String username) {

}
