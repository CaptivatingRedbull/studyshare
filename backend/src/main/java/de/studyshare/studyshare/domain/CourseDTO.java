package de.studyshare.studyshare.domain;

public record CourseDTO(
        Long id,
        String name,
        FacultyDTO faculty
        ) {

}
