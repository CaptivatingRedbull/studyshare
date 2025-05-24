package de.studyshare.studyshare.dto.response;

import java.util.List;

import de.studyshare.studyshare.dto.entity.ContentDTO;

public record ContentPageResponse(
    List<ContentDTO> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages

) {

}
