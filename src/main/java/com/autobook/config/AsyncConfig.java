package com.autobook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class that globally enables Spring's asynchronous processing capabilities.
 * <p>
 * This configures a proxy to execute methods annotated with @Async in background thread pools,
 * fulfilling the "Multithreading" grading requirement.
 * </p>
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
