package de.studyshare.studyshare.dto.entity;

import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String username,
        Role role
        ) {

    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );
    }
}
