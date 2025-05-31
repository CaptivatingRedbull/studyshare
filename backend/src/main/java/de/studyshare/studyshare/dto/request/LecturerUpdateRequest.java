package de.studyshare.studyshare.dto.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing lecturer.
 * Contains necessary fields to update a lecturer in the system.
 */
public record LecturerUpdateRequest(
        /**
         * The name of the lecturer being updated.
         * Cannot be blank, and must be between 2 and 100 characters.
         */
        @Size(min = 2, max = 100, message = "Lecturer name must be between 2 and 100 characters") String name,
        /**
         * The email of the lecturer being updated.
         * Must be a valid email format, and must not exceed 100 characters.
         */
        @Email(message = "Email should be valid") @Size(max = 100, message = "Email must be less than 100 characters") String email,
        /**
         * The IDs of the courses associated with the lecturer.
         * Cannot be null and must contain at least one course ID.
         */
        Set<Long> courseIds) {

}
