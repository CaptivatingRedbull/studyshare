package de.studyshare.studyshare.service;

import java.util.List;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.FacultyDTO;
import de.studyshare.studyshare.repository.FacultyRepository;
import jakarta.transaction.Transactional;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    private final Function<Faculty, FacultyDTO> 
        facultyToFacultyDTO = faculty -> new FacultyDTO(
            faculty.getId(), faculty.getName()
        );
    
    public FacultyService(FacultyRepository facultyRepository){
        this.facultyRepository = facultyRepository;
    }

    @Transactional
    public List<FacultyDTO> getFaculties(){
        return facultyRepository.findAll()
                .stream()
                .map(facultyToFacultyDTO)
                .toList();
    }

    public ResponseEntity<Faculty> createFaculty(Faculty faculty){
        Faculty savedFaculty = facultyRepository.save(faculty);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFaculty);
    }

    public ResponseEntity<?> deleteFaculty(long id) {
        return facultyRepository.findById(id).map(faculty -> {
            facultyRepository.delete(faculty);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    } 
}
