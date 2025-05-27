package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new faculty.
 * Contains necessary fields to create a faculty in the system.
 */
public record FacultyCreateRequest(
        /**
         * The name of the faculty being created.
         * Cannot be null, must not be blank, and must be between 3 and 100 characters.
         */
        @NotNull(message = "Faculty name cannot be null") @NotBlank(message = "Faculty name cannot be blank") @Size(min = 3, max = 100, message = "Faculty name must be between 3 and 100 characters") String name) {

}
