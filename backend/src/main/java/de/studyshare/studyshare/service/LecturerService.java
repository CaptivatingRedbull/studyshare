package de.studyshare.studyshare.service;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.LecturerDTO;
import de.studyshare.studyshare.repository.LecturerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class LecturerService {
    
    private final LecturerRepository lecturerRepository;

    public LecturerService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    private final Function<Lecturer, LecturerDTO> lecturerToLecturerDTO = lecturer -> new LecturerDTO(
        lecturer.getId(),
        lecturer.getName(),
        lecturer.getCourses() != null ? lecturer.getCourses().stream().map(Course::getId).collect(Collectors.toSet()) : Collections.emptySet());

    public List<LecturerDTO> findAll() {
        return lecturerRepository.findAll().stream().map(lecturerToLecturerDTO)
            .collect(Collectors.toList());
    }

    public LecturerDTO findOne(Long id) {
        return lecturerRepository.findById(id)
            .map(lecturerToLecturerDTO)
            .orElseThrow(() -> new EntityNotFoundException("Lecturer not found with id " + id));
    }

    @Transactional
    public ResponseEntity<Lecturer> createLecturer(Lecturer lecturer){
        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLecturer);
    }

    public ResponseEntity<?> deleteLecturer(Long id) {
        return lecturerRepository.findById(id).map(lecturer -> {
            lecturerRepository.delete(lecturer);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
