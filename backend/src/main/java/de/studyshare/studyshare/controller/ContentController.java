package de.studyshare.studyshare.controller;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.SortByOptions;
import de.studyshare.studyshare.dto.entity.ContentDTO;
import de.studyshare.studyshare.dto.request.ContentCreateRequest;
import de.studyshare.studyshare.dto.request.ContentUpdateRequest;
import de.studyshare.studyshare.service.ContentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

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
    public ResponseEntity<ContentDTO> createContent(@Valid @RequestBody ContentCreateRequest createRequest,
            @NotNull @RequestParam("file") MultipartFile file) {

        ContentDTO createdContent = contentService.createContent(createRequest, file);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdContent.id())
                .toUri();
        return ResponseEntity.created(location).body(createdContent);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @contentSecurityService.isOwner(authentication, #id)")
    public ResponseEntity<ContentDTO> updateContent(@PathVariable Long id,
            @Valid @RequestBody ContentUpdateRequest updateRequest) {

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

    @GetMapping("/browse")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ContentDTO>> browseContents(
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long lecturerId,
            @RequestParam(required = false) ContentCategory category,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "uploadDate") SortByOptions sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {

        Page<ContentDTO> contentsPage = contentService.getFilteredAndSortedContents(
                facultyId, courseId, lecturerId, category, searchTerm, sortBy, sortDirection,
                PageRequest.of(page, size));
        return ResponseEntity.ok(contentsPage);
    }

    @GetMapping("/download/{filename:.+}") 
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource file = contentService.loadFileAsResource(filename);

        String contentType = null;
        try {
            contentType = Files.probeContentType(file.getFile().toPath());
        } catch (IOException ex) {
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
