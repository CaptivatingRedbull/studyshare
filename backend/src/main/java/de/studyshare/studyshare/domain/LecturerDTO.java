package de.studyshare.studyshare.domain;

import java.util.Set;

public record LecturerDTO(
        Long id,
        String name,
        Set<Long> courseIds
        ) {

}
