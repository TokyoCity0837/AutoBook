package com.autobook.ConfigTest;

import com.autobook.config.AsyncConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AsyncConfigTest {

    @Test
    void testAsyncConfig() {
        AsyncConfig config = new AsyncConfig();
        assertNotNull(config);
    }
}
