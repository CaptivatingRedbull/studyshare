package de.studyshare.studyshare.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing course.
 * Contains necessary fields to update a course in the system.
 */
public record CourseUpdateRequest(
        /**
         * The name of the course being updated.
         * Cannot be null, must not be blank, and must be between 2 and 150 characters.
         */
        @NotNull(message = "Course ID cannot be null") @Size(min = 2, max = 150, message = "Course name must be between 2 and 150 characters") String name,
        /**
         * The ID of the faculty associated with the course.
         * Cannot be null.
         */
        @NotNull(message = "Faculty ID cannot be null") Long facultyId,
        /**
         * The IDs of the lecturers associated with the course.
         * Cannot be null and must contain at least one lecturer ID.
         */
        @NotNull(message = "Lecturer IDs cannot be null") Set<Long> lecturerIds) {

}
