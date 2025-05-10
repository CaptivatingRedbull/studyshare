package de.studyshare.studyshare.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.dto.entity.FacultyDTO;
import de.studyshare.studyshare.dto.request.FacultyCreateRequest;
import de.studyshare.studyshare.dto.request.FacultyUpdateRequest;
import de.studyshare.studyshare.exception.DuplicateResourceException;
import de.studyshare.studyshare.exception.ResourceNotFoundException;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import jakarta.transaction.Transactional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;

    public FacultyService(FacultyRepository facultyRepository, CourseRepository courseRepository) {
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public List<FacultyDTO> getAllFaculties() {
        return facultyRepository.findAll()
                .stream()
                .map(FacultyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public FacultyDTO getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .map(FacultyDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));
    }

    @Transactional
    public FacultyDTO createFaculty(FacultyCreateRequest createRequest) {
        if (facultyRepository.existsByName(createRequest.name())) {
            throw new DuplicateResourceException("Faculty", "name", createRequest.name());
        }
        Faculty faculty = new Faculty(createRequest.name());
        Faculty savedFaculty = facultyRepository.save(faculty);
        return FacultyDTO.fromEntity(savedFaculty);
    }

    @Transactional
    public FacultyDTO updateFaculty(Long id, FacultyUpdateRequest updateRequest) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));

        if (!faculty.getName().equalsIgnoreCase(updateRequest.name())
                && facultyRepository.existsByName(updateRequest.name())) {
            throw new DuplicateResourceException("Faculty", "name", updateRequest.name());
        }
        faculty.setName(updateRequest.name());
        Faculty updatedFaculty = facultyRepository.save(faculty);
        return FacultyDTO.fromEntity(updatedFaculty);
    }

    @Transactional
    public void deleteFaculty(long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));

        if (courseRepository.existsByFacultyId(id)) {
            throw new DuplicateResourceException(
                    "Cannot delete faculty '" + faculty.getName() + "' as it has associated courses. Please reassign or delete them first."
            );
        }
        facultyRepository.delete(faculty);
    }
}
