package de.studyshare.studyshare.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

/**
 * Entity representing a blocklisted JWT.
 * This is used to store tokens (or their identifiers) that have been
 * invalidated
 * before their natural expiration time (e.g., due to logout, security event).
 */
@Entity
@Table(name = "blocklisted_tokens", indexes = {
        @Index(name = "idx_blocklisted_token_jti", columnList = "jti", unique = true),
        @Index(name = "idx_blocklisted_token_expiry", columnList = "expiryDate")
})
public class BlocklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The JWT ID (jti claim) of the blocklisted token.
     * This should be unique.
     */
    @NotBlank(message = "JTI cannot be blank")
    @Column(nullable = false, unique = true, length = 100) // Adjusted length for JTI
    private String jti;

    /**
     * The original expiry date of the JWT.
     * This is useful for cleaning up the blocklist table.
     */
    @NotNull(message = "Expiry date cannot be null")
    @Column(nullable = false)
    private Instant expiryDate;

    /**
     * Default constructor required by JPA.
     */
    public BlocklistedToken() {
    }

    /**
     * Constructs a new BlocklistedToken.
     * 
     * @param jti        The JWT ID (jti claim).
     * @param expiryDate The original expiry date of the token.
     */
    public BlocklistedToken(String jti, Instant expiryDate) {
        this.jti = jti;
        this.expiryDate = expiryDate;
    }

    /**
     * Gets the unique identifier of the blocklisted token.
     * 
     * @return the unique identifier of the blocklisted token.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the blocklisted token.
     * This is typically used by JPA.
     *
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the JWT ID (jti claim) of the blocklisted token.
     * 
     * @return the JWT ID of the blocklisted token.
     */
    public String getJti() {
        return jti;
    }

    /**
     * Sets the JWT ID (jti claim) of the blocklisted token.
     * 
     * @param jti the JWT ID to set
     */
    public void setJti(String jti) {
        this.jti = jti;
    }

    /**
     * Gets the original expiry date of the blocklisted token.
     * 
     * @return the expiry date of the blocklisted token.
     */
    public Instant getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the original expiry date of the blocklisted token.
     * 
     * @param expiryDate the expiry date to set
     */
    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Checks if the blocklisted token is expired.
     * 
     * @return true if the token is expired, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BlocklistedToken that = (BlocklistedToken) o;
        return Objects.equals(jti, that.jti); // JTI should be unique
    }

    /**
     * Generates a hash code for the blocklisted token based on its JTI.
     * 
     * @return the hash code of the blocklisted token.
     */
    @Override
    public int hashCode() {
        return Objects.hash(jti);
    }
}