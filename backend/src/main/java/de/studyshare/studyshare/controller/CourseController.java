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

import de.studyshare.studyshare.dto.entity.CourseDTO;
import de.studyshare.studyshare.dto.request.CourseCreateRequest;
import de.studyshare.studyshare.dto.request.CourseUpdateRequest;
import de.studyshare.studyshare.exception.BadRequestException;
import de.studyshare.studyshare.service.CourseService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseCreateRequest createRequest) {
        try {
            CourseDTO createdCourse = courseService.createCourse(createRequest);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdCourse.id())
                    .toUri();
            return ResponseEntity.created(location).body(createdCourse);
        } catch (Exception e) {
            throw new BadRequestException("Error creating course: " + e.getMessage());
        }

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id,
            @Valid @RequestBody CourseUpdateRequest updateRequest) {
        try {
            CourseDTO updatedCourse = courseService.updateCourse(id, updateRequest);
            return ResponseEntity.ok(updatedCourse);
        } catch (Exception e) {
            throw new BadRequestException("Error updating course: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/lecturers/{lecturerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> addLecturerToCourse(@PathVariable Long courseId, @PathVariable Long lecturerId) {
        CourseDTO updatedCourse = courseService.addLecturerToCourse(courseId, lecturerId);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{courseId}/lecturers/{lecturerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> removeLecturerFromCourse(@PathVariable Long courseId,
            @PathVariable Long lecturerId) {
        CourseDTO updatedCourse = courseService.removeLecturerFromCourse(courseId, lecturerId);
        return ResponseEntity.ok(updatedCourse);
    }

}
