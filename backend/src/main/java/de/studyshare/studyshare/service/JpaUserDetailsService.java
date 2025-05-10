package de.studyshare.studyshare.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.User;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public JpaUserDetailsService(UserService userService) {
        this.userService = userService;
    }

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
