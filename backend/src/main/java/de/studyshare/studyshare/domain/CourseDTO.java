package de.studyshare.studyshare.domain;

import java.util.Set;

public record CourseDTO(
        Long id,
        String name,
        FacultyDTO faculty,
        Set<Long> lecturerIds
        ) {

}
