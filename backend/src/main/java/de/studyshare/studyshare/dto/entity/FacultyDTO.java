package de.studyshare.studyshare.dto.entity;

import de.studyshare.studyshare.domain.Faculty;

public record FacultyDTO(
        Long id,
        String name
        ) {

    public static FacultyDTO fromEntity(Faculty faculty) {
        if (faculty == null) {
            return null;
        }
        return new FacultyDTO(faculty.getId(), faculty.getName());
    }
}
