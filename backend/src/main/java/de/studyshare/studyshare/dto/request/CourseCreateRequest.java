package de.studyshare.studyshare.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new course.
 * Contains necessary fields to create a course in the system.
 */
public record CourseCreateRequest(
        /**
         * The name of the course being created.
         * Cannot be null or blank, and must be between 2 and 150 characters.
         */
        @NotNull(message = "Course ID cannot be null") @NotBlank(message = "Course name cannot be blank") @Size(min = 2, max = 150, message = "Course name must be between 2 and 150 characters") String name,
        /**
         * The description of the course being created.
         * Cannot be null or blank, and must not exceed 500 characters.
         */
        @NotNull(message = "Faculty ID cannot be null") Long facultyId,
        /**
         * The IDs of the lecturers associated with the course.
         * Cannot be null and must contain at least one lecturer ID.
         */
        @NotNull(message = "Lecturer IDs cannot be null") Set<Long> lecturerIds) {

}
