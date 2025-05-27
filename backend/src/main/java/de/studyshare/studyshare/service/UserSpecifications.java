package de.studyshare.studyshare.service;

import java.util.ArrayList;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import jakarta.persistence.criteria.Predicate;

/**
 * Specification class for filtering User entities based on various criteria.
 * Provides methods to create dynamic queries for user filtering.
 */
@Component
public class UserSpecifications {
    /**
     * Creates a Specification to filter User entities based on the provided
     * criteria.
     *
     * @param username  the username to filter by (optional)
     * @param email     the email to filter by (optional)
     * @param firstName the first name to filter by (optional)
     * @param lastName  the last name to filter by (optional)
     * @param role      the role to filter by (optional)
     * @return a Specification for filtering User entities
     */
    public static Specification<User> filterBy(
            String username,
            String email,
            String firstName,
            String lastName,
            Role role) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (username != null && !username.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"));
            }
            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"));
            }
            if (firstName != null && !firstName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        "%" + firstName.toLowerCase() + "%"));
            }
            if (lastName != null && !lastName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        "%" + lastName.toLowerCase() + "%"));
            }
            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
