package de.studyshare.studyshare.service;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for filtering Content entities based on various criteria.
 * Provides methods to create dynamic queries for content filtering.
 */
@Component
public class ContentSpecifications {

    /**
     * Creates a Specification to filter Content entities based on the provided
     * criteria.
     *
     * @param facultyId  the ID of the faculty to filter by (optional)
     * @param courseId   the ID of the course to filter by (optional)
     * @param lecturerId the ID of the lecturer to filter by (optional)
     * @param category   the content category to filter by (optional)
     * @param searchTerm a search term to filter content titles (optional)
     * @return a Specification for filtering Content entities
     */
    public static Specification<Content> filterBy(
            Long facultyId,
            Long courseId,
            Long lecturerId,
            ContentCategory category,
            String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (facultyId != null) {
                Join<Content, Faculty> facultyJoin = root.join("faculty");
                predicates.add(criteriaBuilder.equal(facultyJoin.get("id"), facultyId));
            }
            if (courseId != null) {
                Join<Content, Course> courseJoin = root.join("course");
                predicates.add(criteriaBuilder.equal(courseJoin.get("id"), courseId));
            }
            if (lecturerId != null) {
                Join<Content, Lecturer> lecturerJoin = root.join("lecturer");
                predicates.add(criteriaBuilder.equal(lecturerJoin.get("id"), lecturerId));
            }
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("contentCategory"), category));
            }
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String likePattern = "%" + searchTerm.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}