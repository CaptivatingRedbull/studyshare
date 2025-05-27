package de.studyshare.studyshare.dto.entity;

import de.studyshare.studyshare.domain.Faculty;

/**
 * Data Transfer Object for Faculty entities.
 * Contains faculty information for transfer between layers.
 */
public record FacultyDTO(
        /**
         * Unique identifier for the faculty.
         */
        Long id,

        /**
         * The name of the faculty.
         */
        String name) {

    /**
     * Converts a Faculty entity to a FacultyDTO.
     *
     * @param faculty The Faculty entity to convert
     * @return A new FacultyDTO with data from the entity, or null if the input is
     *         null
     */
    public static FacultyDTO fromEntity(Faculty faculty) {
        if (faculty == null) {
            return null;
        }
        return new FacultyDTO(faculty.getId(), faculty.getName());
    }
}