package de.studyshare.studyshare.service;

import de.studyshare.studyshare.domain.BlocklistedToken;
import de.studyshare.studyshare.repository.BlocklistedTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

/**
 * Service for managing the blocklist of JWTs.
 * Uses a database table to store invalidated token identifiers (JTIs).
 */
@Service
public class TokenBlocklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlocklistService.class);
    private final BlocklistedTokenRepository blocklistedTokenRepository;
    private final JwtUtil jwtUtil; // To extract expiry date
    
    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    /**
     * Constructor for TokenBlocklistService.
     * 
     * @param blocklistedTokenRepository Repository for accessing blocklisted
     *                                   tokens.
     * @param jwtUtil                    Utility for handling JWT operations.
     */
    public TokenBlocklistService(BlocklistedTokenRepository blocklistedTokenRepository, JwtUtil jwtUtil) {
        this.blocklistedTokenRepository = blocklistedTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Adds a token's JTI to the blocklist.
     * The token's original expiry date is also stored for cleanup purposes.
     * 
     * @param token The JWT string.
     */
    @Transactional
    public void addToBlocklist(String token) {
        try {
            String jti = jwtUtil.extractJti(token);
            Date expiryDate = jwtUtil.extractExpiration(token);
            if (jti != null && expiryDate != null) {
                if (!blocklistedTokenRepository.existsByJti(jti)) {
                    BlocklistedToken blocklisted = new BlocklistedToken(jti, expiryDate.toInstant());
                    blocklistedTokenRepository.save(blocklisted);
                    logger.info("Token JTI added to blocklist: {}", jti);
                } else {
                    logger.warn("Attempted to blocklist an already blocklisted JTI: {}", jti);
                }
            } else {
                logger.error("Could not extract JTI or expiry from token for blocklisting.");
            }
        } catch (Exception e) {
            logger.error("Error adding token to blocklist: {}", e.getMessage());
        }
    }

    /**
     * Checks if a token's JTI is in the blocklist.
     * 
     * @param token The JWT string.
     * @return true if the token's JTI is blocklisted, false otherwise.
     */
    public boolean isBlocklisted(String token) {
        try {
            String jti = jwtUtil.extractJti(token);
            if (jti != null) {
                return blocklistedTokenRepository.existsByJti(jti);
            }
            return false; // Cannot determine if blocklisted without JTI
        } catch (Exception e) {
            // If token is malformed or JTI cannot be extracted, treat as potentially risky
            logger.warn("Could not extract JTI for blocklist check, token might be invalid: {}", e.getMessage());
            return true; 
        }
    }

    /**
     * Periodically cleans up expired tokens from the blocklist.
     * Runs every hour in production, but skips execution during tests.
     */
    @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hour
    @Transactional
    public void cleanupExpiredTokens() {
        // Skip cleanup during tests to prevent database connection issues
        if (activeProfiles != null && (activeProfiles.contains("test") || activeProfiles.contains("junit"))) {
            logger.debug("Skipping scheduled cleanup during test execution (active profiles: {})", activeProfiles);
            return;
        }
        
        Instant now = Instant.now();
        logger.info("Running scheduled cleanup of expired blocklisted tokens before: {}", now);
        long count = blocklistedTokenRepository.deleteByExpiryDateBefore(now);
        logger.info("Cleaned up {} expired blocklisted tokens.", count);
    }
}