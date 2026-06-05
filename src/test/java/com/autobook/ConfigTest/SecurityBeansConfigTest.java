package com.autobook.ConfigTest;

import com.autobook.config.SecurityBeansConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityBeansConfigTest {

    @Test
    void testPasswordEncoder() {
        SecurityBeansConfig config = new SecurityBeansConfig();
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.getClass().getSimpleName().contains("BCrypt"));
    }
}
