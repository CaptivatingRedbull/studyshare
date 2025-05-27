package de.studyshare.studyshare.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.repository.ContentRepository;

/**
 * Service class for handling content security operations.
 * Provides methods to check if the current user is the owner of a specific
 * content.
 */
@Service("contentSecurityService")
public class ContentSecurityService {

    private final ContentRepository contentRepository;

    /**
     * Constructs a ContentSecurityService with the specified ContentRepository.
     *
     * @param contentRepository the repository to access content data
     */
    public ContentSecurityService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    /**
     * Checks if the current user is the owner of the content with the specified ID.
     *
     * @param authentication the authentication object containing user details
     * @param contentId      the ID of the content to check ownership for
     * @return true if the current user is the owner of the content, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isOwner(Authentication authentication, Long contentId) {
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

        Optional<Content> contentOptional = contentRepository.findById(contentId);
        if (contentOptional.isEmpty()) {
            return false;
        }

        Content content = contentOptional.get();
        return content.getUploadedBy() != null && content.getUploadedBy().getUsername().equals(currentUsername);
    }
}
