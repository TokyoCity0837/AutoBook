package com.autobook.Generic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service dedicated to running background tasks asynchronously.
 */
@Slf4j
@Service
public class AsyncActivityLogger {

    /**
     * Simulates an asynchronous background task, such as compiling statistics,
     * sending a welcome email, or writing audit records to a data warehouse.
     *
     * @param username the username being processed
     */
    @Async
    public void logRegistrationAsync(String username) {
        log.info("Spawning background thread for user registration audit: {}", username);
        log.info("Current execution thread: {}", Thread.currentThread().getName());

        try {
            // Simulate a heavy IO/network operation
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Async background thread interrupted", e);
        }

        log.info("Background auditing successfully finalized for user: {}", username);
    }
}
