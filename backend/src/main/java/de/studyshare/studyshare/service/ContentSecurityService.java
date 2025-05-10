package de.studyshare.studyshare.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.repository.ContentRepository;

@Service("contentSecurityService")
public class ContentSecurityService {

    private final ContentRepository contentRepository;

    public ContentSecurityService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

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
