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

import de.studyshare.studyshare.dto.entity.FacultyDTO;
import de.studyshare.studyshare.dto.request.FacultyCreateRequest;
import de.studyshare.studyshare.dto.request.FacultyUpdateRequest;
import de.studyshare.studyshare.service.FacultyService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FacultyDTO>> getAllFaculties() {
        List<FacultyDTO> faculties = facultyService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FacultyDTO> getFacultyById(@PathVariable Long id) {
        FacultyDTO faculty = facultyService.getFacultyById(id);
        return ResponseEntity.ok(faculty);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultyDTO> createFaculty(@Valid @RequestBody FacultyCreateRequest createRequest) {
        FacultyDTO createdFaculty = facultyService.createFaculty(createRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFaculty.id())
                .toUri();
        return ResponseEntity.created(location).body(createdFaculty);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultyDTO> updateFaculty(@PathVariable Long id,
            @Valid @RequestBody FacultyUpdateRequest updateRequest) {
        FacultyDTO updatedFaculty = facultyService.updateFaculty(id, updateRequest);
        return ResponseEntity.ok(updatedFaculty);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFaculty(@PathVariable long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
}
