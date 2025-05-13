package de.studyshare.studyshare.dto.response; // It's good practice to have a separate 'response' sub-package for DTOs

public record LoginResponse(String token,
        String username) {

}
