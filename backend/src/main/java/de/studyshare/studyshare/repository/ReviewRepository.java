package de.studyshare.studyshare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Review;

public interface ReviewRepository  extends JpaRepository<Review,  Long> {
    List<Review> findByContentId(Long contentId);
}
