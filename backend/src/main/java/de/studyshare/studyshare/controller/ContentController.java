package de.studyshare.studyshare.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentDTO;
import de.studyshare.studyshare.service.ContentService;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public List<ContentDTO> listAll() {
        return contentService.getAllContents();
    }

    @GetMapping("/{id}")
    public ContentDTO getOne(@PathVariable Long id) {
        return contentService.getContentById(id);
    }

    @PostMapping
    public ResponseEntity<ContentDTO> create(@RequestBody Content content) {
        ContentDTO dto = contentService.createContent(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ContentDTO update(
            @PathVariable Long id,
            @RequestBody Content payload) {
        return contentService.updateContent(id, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        contentService.deleteContent(id);
    }
}