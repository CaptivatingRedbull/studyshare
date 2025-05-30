package de.studyshare.studyshare.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Test configuration that provides a properly configured task scheduler for tests.
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * Provides a properly configured task scheduler for tests.
     * This overrides the auto-configured scheduler to ensure proper setup.
     * 
     * @return A properly configured task scheduler for tests
     */
    @Bean(name = "taskScheduler")
    @Primary
    public TaskScheduler testTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // Set a valid pool size
        scheduler.setThreadNamePrefix("test-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(1);
        scheduler.initialize(); // Initialize the scheduler properly
        return scheduler;
    }
}
