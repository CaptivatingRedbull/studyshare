package de.studyshare.studyshare.dto.entity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Lecturer;

/**
 * Data Transfer Object for Course entities.
 * Contains course information for transfer between layers.
 */
public record CourseDTO(
        /**
         * Unique identifier for the course.
         */
        Long id,

        /**
         * The name of the course.
         */
        String name,

        /**
         * The faculty to which this course belongs.
         */
        FacultyDTO faculty,

        /**
         * Set of lecturer IDs associated with this course.
         */
        Set<Long> lecturerIds) {

    /**
     * Converts a Course entity to a CourseDTO.
     *
     * @param course The Course entity to convert
     * @return A new CourseDTO with data from the entity, or null if the input is
     *         null
     */
    public static CourseDTO fromEntity(Course course) {
        if (course == null) {
            return null;
        }
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getFaculty() != null ? FacultyDTO.fromEntity(course.getFaculty()) : null,
                course.getLecturers() == null
                        ? Collections.emptySet()
                        : course.getLecturers().stream()
                                .map(Lecturer::getId)
                                .collect(Collectors.toSet()));
    }
}