package de.studyshare.studyshare.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.studyshare.studyshare.dto.entity.ContentDTO;
import de.studyshare.studyshare.dto.request.ContentCreateRequest;
import de.studyshare.studyshare.dto.request.ContentUpdateRequest;
import de.studyshare.studyshare.service.ContentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ContentDTO>> getAllContents() {
        return ResponseEntity.ok(contentService.getAllContents());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContentDTO> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContentDTO> createContent(@Valid @RequestBody ContentCreateRequest createRequest) {
        ContentDTO createdContent = contentService.createContent(createRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdContent.id())
                .toUri();
        return ResponseEntity.created(location).body(createdContent);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @contentSecurityService.isOwner(authentication, #id)")
    public ResponseEntity<ContentDTO> updateContent(@PathVariable Long id, @Valid @RequestBody ContentUpdateRequest updateRequest) {
        ContentDTO updatedContent = contentService.updateContent(id, updateRequest);
        return ResponseEntity.ok(updatedContent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @contentSecurityService.isOwner(authentication, #id)")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContentDTO> reportContent(@PathVariable Long id) {
        ContentDTO updatedContent = contentService.incrementReportCount(id);
        return ResponseEntity.ok(updatedContent);
    }

    @PostMapping("/{id}/mark-outdated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContentDTO> markContentAsOutdated(@PathVariable Long id) {
        ContentDTO updatedContent = contentService.incrementOutdatedCount(id);
        return ResponseEntity.ok(updatedContent);
    }
}
