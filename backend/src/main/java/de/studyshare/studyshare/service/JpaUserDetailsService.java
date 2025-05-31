package de.studyshare.studyshare.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.User;

/**
 * Service class that implements UserDetailsService to load user-specific data.
 * This service is used by Spring Security to authenticate users based on their
 * username.
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * Constructs a JpaUserDetailsService with the specified UserService.
     *
     * @param userService the service to retrieve user details
     */
    public JpaUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Loads user details by username.
     * This method retrieves the user from the UserService and constructs a
     * UserDetails object.
     *
     * @param username the username of the user to load
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userService.getInternalUserByUsername(username);
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPasswordHash())
                    .roles(user.getRole().toString())
                    .build();
        } catch (de.studyshare.studyshare.exception.ResourceNotFoundException ex) {
            throw new UsernameNotFoundException("User not found with username: " + username, ex);
        }
    }
}
