package de.studyshare.studyshare.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.repository.UserRepository;

@Service("userSecurityService")
public class UserSecurityService {

    private final UserRepository userRepository;

    public UserSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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