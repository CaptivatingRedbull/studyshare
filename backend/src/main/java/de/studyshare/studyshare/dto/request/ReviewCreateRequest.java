package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequest(
        @NotNull(message = "Stars cannot be null")
        @Min(value = 1, message = "Stars must be at least 1")
        @Max(value = 5, message = "Stars must be at most 5")
        Integer stars,
        @NotNull(message = "Subject cannot be null")
        @NotBlank(message = "Subject cannot be blank")
        @Size(max = 100, message = "Subject must be less than 100 characters")
        String subject,
        @NotNull(message = "Comment cannot be null")
        @Size(max = 2000, message = "Comment must be less than 2000 characters")
        String comment
        ) {

}
