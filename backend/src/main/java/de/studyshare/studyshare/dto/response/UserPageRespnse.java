package de.studyshare.studyshare.dto.response;

import java.util.List;

import de.studyshare.studyshare.dto.entity.UserDTO;

/**
 * Response DTO for paginated user data.
 * Contains a list of users along with pagination details.
 */
public record UserPageRespnse(
        /**
         * List of users in the current page.
         */
        List<UserDTO> users,
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
        int totalPages) {

}
