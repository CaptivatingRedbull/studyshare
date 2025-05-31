package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing review.
 * Contains necessary fields to update a review in the system.
 */
public record ReviewUpdateRequest(
        /**
         * The ID of the review being updated.
         * Cannot be null.
         */
        @NotNull(message = "Stars cannot be null") @Min(value = 1, message = "Stars must be at least 1") @Max(value = 5, message = "Stars must be at most 5") Integer stars,
        /**
         * The subject of the review being updated.
         * Cannot be null, and must be less than 100 characters.
         */
        @NotNull(message = "Subject cannot be null") @Size(max = 100, message = "Subject must be less than 100 characters") String subject,
        /**
         * The comment of the review being updated.
         * Cannot be null, and must be less than 2000 characters.
         */
        @NotNull(message = "Comment cannot be null") @Size(max = 2000, message = "Comment must be less than 2000 characters") String comment) {

}
