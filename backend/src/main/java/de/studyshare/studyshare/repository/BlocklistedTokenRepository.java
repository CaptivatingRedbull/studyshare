package de.studyshare.studyshare.repository;

import de.studyshare.studyshare.domain.BlocklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.Optional;

/**
 * Repository interface for managing BlocklistedToken entities.
 * Provides methods to perform CRUD operations and custom queries on blocklisted
 * tokens.
 */
@Repository
public interface BlocklistedTokenRepository extends JpaRepository<BlocklistedToken, Long> {

    /**
     * Finds a blocklisted token by its JTI (JWT ID).
     *
     * @param jti The JWT ID.
     * @return An Optional containing the BlocklistedToken if found, or empty if
     *         not.
     */
    Optional<BlocklistedToken> findByJti(String jti);

    /**
     * Checks if a token with the given JTI exists in the blocklist.
     *
     * @param jti The JWT ID.
     * @return true if a token with the JTI is blocklisted, false otherwise.
     */
    boolean existsByJti(String jti);

    /**
     * Deletes all blocklisted tokens whose expiry date is before the given
     * timestamp.
     * This is useful for cleaning up old, expired tokens from the blocklist.
     *
     * @param now The current timestamp.
     * @return The number of tokens deleted.
     */
    long deleteByExpiryDateBefore(Instant now);
}
