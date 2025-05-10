package de.studyshare.studyshare.dto.entity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Lecturer;

public record CourseDTO(
        Long id,
        String name,
        FacultyDTO faculty,
        Set<Long> lecturerIds
        ) {

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
                        .collect(Collectors.toSet())
        );
    }
}
