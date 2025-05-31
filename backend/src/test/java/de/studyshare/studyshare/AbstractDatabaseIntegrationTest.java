package de.studyshare.studyshare;

import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import de.studyshare.studyshare.config.TestConfig;
import de.studyshare.studyshare.repository.BlocklistedTokenRepository;
import de.studyshare.studyshare.repository.ContentRepository;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import de.studyshare.studyshare.repository.ReviewRepository;
import de.studyshare.studyshare.repository.UserRepository;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DirtiesContext
@Testcontainers
@Import(TestConfig.class)
@ActiveProfiles("test")
public abstract class AbstractDatabaseIntegrationTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate; // Autowire JdbcTemplate

    // Keep repositories if your concrete test setup methods will use them for convenience
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected FacultyRepository facultyRepository;
    @Autowired
    protected CourseRepository courseRepository;
    @Autowired
    protected LecturerRepository lecturerRepository;
    @Autowired
    protected ContentRepository contentRepository;
    @Autowired
    protected ReviewRepository reviewRepository;
    @Autowired
    protected BlocklistedTokenRepository blocklistedTokenRepository;

    // Shared container instance using singleton pattern
    private static volatile MariaDBContainer<?> sharedMariaDbContainer;
    private static final Object containerLock = new Object();

    // Getter for the shared container with lazy initialization
    @SuppressWarnings("resource")
    protected static MariaDBContainer<?> getSharedContainer() {
        if (sharedMariaDbContainer == null) {
            synchronized (containerLock) {
                if (sharedMariaDbContainer == null) {
                    sharedMariaDbContainer = new MariaDBContainer<>("mariadb:10.11")
                            .withDatabaseName("studyshare_db_test")
                            .withUsername("studyshare_user_test")
                            .withPassword("test_password")
                            .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci");
                }
            }
        }
        return sharedMariaDbContainer;
    }

    @Container
    static final MariaDBContainer<?> mariaDbContainer = getSharedContainer();

    @DynamicPropertySource
    static void overrideMariaDbProperties(DynamicPropertyRegistry registry) {
        if (!mariaDbContainer.isRunning()) { // Defensive check, though @Container should handle start
            mariaDbContainer.start();
        }
        registry.add("spring.datasource.url", () -> mariaDbContainer.getJdbcUrl() + "?useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci");
        registry.add("spring.datasource.username", mariaDbContainer::getUsername);
        registry.add("spring.datasource.password", mariaDbContainer::getPassword);
        registry.add("spring.datasource.driverClassName", () -> "org.mariadb.jdbc.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MariaDBDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        
        // Better connection pool configuration for tests
        registry.add("spring.datasource.hikari.connection-timeout", () -> "5000");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "3");
        registry.add("spring.datasource.hikari.minimum-idle", () -> "1");
        registry.add("spring.datasource.hikari.idle-timeout", () -> "10000");
        registry.add("spring.datasource.hikari.max-lifetime", () -> "30000");
        registry.add("spring.datasource.hikari.leak-detection-threshold", () -> "15000");
        
        // Disable schedulers that might cause connection issues during shutdown
        registry.add("spring.task.scheduling.pool.size", () -> "0");
    }

    @BeforeAll
    static void beforeAll() {
        
    }

    @BeforeEach
    public void resetDatabaseBeforeEachTest() {
        // Ensure container is running before database operations
        if (!mariaDbContainer.isRunning()) {
            mariaDbContainer.start();
        }
        
        List<String> tableNames = List.of(
            "review",
            "content",
            "course_lecturer", 
            "course",
            "lecturer",
            "faculty",
            "blocklisted_tokens",
            "users"
        );

        try {
            // For MariaDB/MySQL to allow out-of-order deletion or TRUNCATE on tables with FKs
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");

            for (String tableName : tableNames) {
                // Using DELETE FROM. TRUNCATE TABLE is faster but might have more issues with FKs even if disabled.
                jdbcTemplate.execute("DELETE FROM " + tableName + ";");
                
                // Reset AUTO_INCREMENT. Wrapped in try-catch for tables without it (like join tables).
                try {
                    jdbcTemplate.execute("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1;");
                } catch (Exception e) {
                    // Ignore - table might not have AUTO_INCREMENT
                }
            }

            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
        } catch (Exception e) {
            // If database reset fails, log and rethrow
            System.err.println("Failed to reset database: " + e.getMessage());
            throw e;
        }
    }

    @org.junit.jupiter.api.AfterEach
    public void cleanupAfterEachTest() {
        
    }

    @AfterAll
    static void afterAll() {
        // Don't stop the shared container here - let Testcontainers handle it
        // The container will be reused across test classes for better performance
    }
}