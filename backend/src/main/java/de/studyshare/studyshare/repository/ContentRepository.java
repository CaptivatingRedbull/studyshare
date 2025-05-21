package de.studyshare.studyshare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.studyshare.studyshare.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {
    List<Content> findByFacultyId(Long facultyId);

    List<Content> findByCourseId(Long courseId);
}
