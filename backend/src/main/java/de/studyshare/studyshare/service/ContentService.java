package de.studyshare.studyshare.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.ContentDTO;
import de.studyshare.studyshare.dto.request.ContentCreateRequest;
import de.studyshare.studyshare.dto.request.ContentUpdateRequest;
import de.studyshare.studyshare.exception.BadRequestException;
import de.studyshare.studyshare.exception.ResourceNotFoundException;
import de.studyshare.studyshare.repository.ContentRepository;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import de.studyshare.studyshare.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final LecturerRepository lecturerRepository;

    public ContentService(ContentRepository contentRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            FacultyRepository facultyRepository,
            LecturerRepository lecturerRepository) {
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.lecturerRepository = lecturerRepository;
    }

    @Transactional
    public List<ContentDTO> getAllContents() {
        return contentRepository.findAll().stream()
                .map(ContentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContentDTO getContentById(Long id) {
        return contentRepository.findById(id)
                .map(ContentDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));
    }

    @Transactional
    public ContentDTO createContent(ContentCreateRequest createRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User uploadedByUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername + " (authenticated user not found)"));

        Course course = courseRepository.findById(createRequest.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", createRequest.courseId()));
        Faculty faculty = facultyRepository.findById(createRequest.facultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", createRequest.facultyId()));

        Lecturer lecturer = null;
        if (createRequest.lecturerId() != null) {
            lecturer = lecturerRepository.findById(createRequest.lecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lecturer", "id", createRequest.lecturerId()));
        }

        if (!course.getFaculty().getId().equals(faculty.getId())) {
            throw new BadRequestException("The specified course (ID: " + course.getId() + ") does not belong to the specified faculty (ID: " + faculty.getId() + ").");
        }

        if (lecturer != null && !course.getLecturers().contains(lecturer)) {
            throw new BadRequestException("The specified lecturer (ID: " + lecturer.getId() + ") is not associated with the specified course (ID: " + course.getId() + ").");
        }

        Content content = new Content();
        content.setUploadedBy(uploadedByUser);
        content.setUploadDate(LocalDate.now());
        content.setContentCategory(createRequest.contentCategory());
        content.setCourse(course);
        content.setFaculty(faculty);
        content.setLecturer(lecturer);
        content.setFilePath(createRequest.filePath());
        content.setTitle(createRequest.title());

        Content savedContent = contentRepository.save(content);
        return ContentDTO.fromEntity(savedContent);
    }

    @Transactional
    public ContentDTO updateContent(Long id, ContentUpdateRequest updateRequest) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));

        if (updateRequest.contentCategory() != null) {
            content.setContentCategory(updateRequest.contentCategory());
        }
        if (updateRequest.filePath() != null) {
            content.setFilePath(updateRequest.filePath());
        }
        if (updateRequest.title() != null) {
            content.setTitle(updateRequest.title());
        }

        if (updateRequest.courseId() != null) {
            Course newCourse = courseRepository.findById(updateRequest.courseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", "id", updateRequest.courseId()));
            content.setCourse(newCourse);

        }
        if (updateRequest.facultyId() != null) {
            Faculty newFaculty = facultyRepository.findById(updateRequest.facultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", updateRequest.facultyId()));
            content.setFaculty(newFaculty);
        }
        if (updateRequest.lecturerId() != null) {
            Lecturer newLecturer = lecturerRepository.findById(updateRequest.lecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lecturer", "id", updateRequest.lecturerId()));
            content.setLecturer(newLecturer);
        } else if (updateRequest.lecturerId() == null && content.getLecturer() != null) {
            content.setLecturer(null);
        }

        if (content.getCourse() != null && !content.getCourse().getFaculty().getId().equals(content.getFaculty().getId())) {
            throw new BadRequestException("The content's course (ID: " + content.getCourse().getId() + ") must belong to the content's faculty (ID: " + content.getFaculty().getId() + ").");
        }
        if (content.getLecturer() != null && content.getCourse() != null && !content.getCourse().getLecturers().contains(content.getLecturer())) {
            throw new BadRequestException("The content's lecturer (ID: " + content.getLecturer().getId() + ") must be associated with the content's course (ID: " + content.getCourse().getId() + ").");
        }

        Content updatedContent = contentRepository.save(content);
        return ContentDTO.fromEntity(updatedContent);
    }

    @Transactional
    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));

        contentRepository.delete(content);

    }

    @Transactional
    public ContentDTO incrementReportCount(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));
        content.setReportedCount(content.getReportedCount() + 1);

        return ContentDTO.fromEntity(contentRepository.save(content));
    }

    @Transactional
    public ContentDTO incrementOutdatedCount(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));
        content.setOutdatedCount(content.getOutdatedCount() + 1);

        return ContentDTO.fromEntity(contentRepository.save(content));
    }
}
