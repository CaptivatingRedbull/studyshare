package de.studyshare.studyshare.dto.entity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Lecturer;

public record LecturerDTO(
        Long id,
        String name,
        String email,
        Set<Long> courseIds
        ) {

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
                        .collect(Collectors.toSet())
        );
    }
}
