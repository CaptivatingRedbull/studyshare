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

import de.studyshare.studyshare.dto.entity.LecturerDTO;
import de.studyshare.studyshare.dto.request.LecturerCreateRequest;
import de.studyshare.studyshare.dto.request.LecturerUpdateRequest;
import de.studyshare.studyshare.service.LecturerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LecturerDTO>> getAllLecturers() {
        return ResponseEntity.ok(lecturerService.getAllLecturers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LecturerDTO> getLecturerById(@PathVariable Long id) {
        return ResponseEntity.ok(lecturerService.getLecturerById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LecturerDTO> createLecturer(@Valid @RequestBody LecturerCreateRequest createRequest) {
        LecturerDTO createdLecturer = lecturerService.createLecturer(createRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdLecturer.id())
                .toUri();
        return ResponseEntity.created(location).body(createdLecturer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LecturerDTO> updateLecturer(@PathVariable Long id, @Valid @RequestBody LecturerUpdateRequest updateRequest) {
        LecturerDTO updatedLecturer = lecturerService.updateLecturer(id, updateRequest);
        return ResponseEntity.ok(updatedLecturer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }
}
