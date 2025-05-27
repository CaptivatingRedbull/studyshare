package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.NotBlank; // Keep if name is always required for update
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FacultyUpdateRequest(
        /**
         * The name of the faculty being updated.
         * Cannot be null, must not be blank, and must be between 3 and 100 characters.
         */
        @NotNull(message = "Faculty name cannot be null") @NotBlank(message = "Faculty name cannot be blank") @Size(min = 3, max = 100, message = "Faculty name must be between 3 and 100 characters") String name) {

}
