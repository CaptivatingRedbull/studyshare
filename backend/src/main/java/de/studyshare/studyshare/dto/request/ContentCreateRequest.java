package de.studyshare.studyshare.dto.request;

import de.studyshare.studyshare.domain.ContentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating new content.
 * Contains necessary fields to create content in the system.
 */
public record ContentCreateRequest(
        /**
         * The category of the content being created.
         * Cannot be null.
         */
        @NotNull(message = "Content category cannot be null") ContentCategory contentCategory,
        /**
         * The ID of the course associated with the content.
         * Cannot be null.
         */
        @NotNull(message = "Course ID cannot be null") Long courseId,
        /**
         * The ID of the lecturer creating the content.
         * Cannot be null.
         */
        @NotNull(message = "Lecturer ID cannot be null") Long lecturerId,
        /**
         * The ID of the faculty associated with the content.
         * Cannot be null.
         */
        @NotNull(message = "Faculty ID cannot be null") Long facultyId,
        /**
         * The title of the content being created.
         * Cannot be null or blank, and must not exceed 255 characters.
         */
        @NotNull(message = "Title cannot be null") @NotBlank(message = "Title cannot be blank") @Size(max = 255, message = "Title is too long") String title) {

}
