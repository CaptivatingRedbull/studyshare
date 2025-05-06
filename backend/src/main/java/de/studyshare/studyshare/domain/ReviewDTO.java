package de.studyshare.studyshare.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewDTO(
        Long id,
        @Min(1)
        @Max(5)
        int stars,
        String subject,
        String comment,
        User user,
        Content content
        ) {

}
