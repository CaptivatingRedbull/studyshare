package de.studyshare.studyshare.domain;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String passwordHash,
        Role role,
        String userName
        ) {

}
