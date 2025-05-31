package de.studyshare.studyshare.dto.entity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Lecturer;

/**
 * Data Transfer Object for Lecturer entities.
 * Contains lecturer information for transfer between layers.
 */
public record LecturerDTO(
        /**
         * Unique identifier for the lecturer.
         */
        Long id,
        /**
         * Name of the lecturer.
         */
        String name,
        /**
         * Email address of the lecturer.
         */
        String email,
        /**
         * Set of course IDs associated with the lecturer.
         * This is used to link the lecturer to the courses they teach.
         */
        Set<Long> courseIds) {
    /**
     * Converts a Lecturer entity to a LecturerDTO.
     *
     * @param lecturer the Lecturer entity to convert
     * @return a LecturerDTO containing the lecturer's information, or null if the
     *         lecturer is null
     */
    public static LecturerDTO fromEntity(Lecturer lecturer) {
        if (lecturer == null) {
            return null;
        }
        return new LecturerDTO(
                lecturer.getId(),
                lecturer.getName(),
                lecturer.getEmail(),
                lecturer.getCourses() == null
                        ? Collections.emptySet()
                        : lecturer.getCourses().stream()
                                .map(Course::getId)
                                .collect(Collectors.toSet()));
    }
}
