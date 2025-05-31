package de.studyshare.studyshare.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.repository.UserRepository;

/**
 * Service class for handling user security operations.
 * Provides methods to check if the current user is the owner of a specific user
 * account.
 */
@Service("userSecurityService")
public class UserSecurityService {

    private final UserRepository userRepository;

    /**
     * Constructs a UserSecurityService with the specified UserRepository.
     *
     * @param userRepository the repository to access user data
     */
    public UserSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks if the current user is the owner of the user account with the given
     * username.
     *
     * @param authentication the authentication object containing user details
     * @param username       the username of the user to check ownership for
     * @return true if the current user is the owner, false otherwise
     */
    public boolean isOwner(Authentication authentication, String username) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        String currentUsername;

        switch (principal) {
            case UserDetails userDetails ->
                currentUsername = userDetails.getUsername();
            case String string ->
                currentUsername = string;
            default -> {
                return false;
            }
        }

        return currentUsername.equals(username);
    }

    /**
     * Checks if the current user is the owner of the user account with the given
     * ID.
     *
     * @param authentication the authentication object containing user details
     * @param id             the ID of the user to check ownership for
     * @return true if the current user is the owner, false otherwise
     */
    public boolean isOwner(Authentication authentication, Long id) {
        if (authentication == null || !authentication.isAuthenticated() || id == null) {
            return false;
        }

        String username = userRepository.findById(id)
                .map(user -> user.getUsername())
                .orElse(null);

        if (username == null) {
            return false;
        }

        return isOwner(authentication, username);
    }
}