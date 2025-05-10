package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ReviewUpdateRequest(
        @Min(value = 1, message = "Stars must be at least 1")
        @Max(value = 5, message = "Stars must be at most 5")
        Integer stars,
        @Size(max = 100, message = "Subject must be less than 100 characters")
        String subject,
        @Size(max = 2000, message = "Comment must be less than 2000 characters")
        String comment
        ) {

}
