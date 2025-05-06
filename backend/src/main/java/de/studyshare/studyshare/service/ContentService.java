package de.studyshare.studyshare.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentDTO;
import de.studyshare.studyshare.repository.ContentRepository;

@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Transactional(readOnly = true)
    public List<ContentDTO> getAllContents() {
        return contentRepository.findAll()
                                .stream()
                                .map(Content::toDto)
                                .toList();
    }

    @Transactional(readOnly = true)
    public ContentDTO getContentById(Long id) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Content not found: " + id));
        return content.toDto();
    }

    @Transactional
    public ContentDTO createContent(Content content) {
        Content saved = contentRepository.save(content);
        return saved.toDto();
    }

    @Transactional
    public ContentDTO updateContent(Long id, Content updatedData) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Content not found: " + id));
        // copy over the fields you allow updating
        content.setFilePath(updatedData.getFilePath());
        content.setUploadDate(updatedData.getUploadDate());
        content.setCategory(updatedData.getCategory());
        content.setLecturer(updatedData.getLecturer());
        content.setCourse(updatedData.getCourse());
        content.setFaculty(updatedData.getFaculty());
        // etc…
        Content saved = contentRepository.save(content);
        return saved.toDto();
    }

    @Transactional
    public void deleteContent(Long id) {
        contentRepository.deleteById(id);
    }
}
