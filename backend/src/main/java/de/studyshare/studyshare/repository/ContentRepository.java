package de.studyshare.studyshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {

}
