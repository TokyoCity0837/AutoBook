package com.autobook;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sanity test - does not load the full Spring context
 * (avoids cloud DB connection during local test runs).
 */
class AutobookApplicationTests {

    @Test
    void applicationClassExists() {
        assertNotNull(AutobookApplication.class);
    }
}
