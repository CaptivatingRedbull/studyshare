package de.studyshare.studyshare.dto.response;

import java.util.List;

import de.studyshare.studyshare.dto.entity.ContentDTO;

/**
 * Response DTO for paginated content data.
 * Contains a list of content items along with pagination details.
 */
public record ContentPageResponse(
        /**
         * List of content items in the current page.
         */
        List<ContentDTO> content,
        /**
         * Current page number (0-indexed).
         */
        int pageNumber,
        /**
         * Size of the page (number of items per page).
         */
        int pageSize,
        /**
         * Total number of elements across all pages.
         */
        long totalElements,
        /**
         * Total number of pages available.
         */
        int totalPages

) {

}
