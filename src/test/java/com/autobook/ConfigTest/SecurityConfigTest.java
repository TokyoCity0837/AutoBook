package com.autobook.ConfigTest;

import com.autobook.config.CustomOAuth2UserService;
import com.autobook.config.OAuth2SuccessHandler;
import com.autobook.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    @Test
    void testCorsConfigurationSource() {
        CustomOAuth2UserService oAuth = mock(CustomOAuth2UserService.class);
        OAuth2SuccessHandler handler = mock(OAuth2SuccessHandler.class);
        SecurityConfig config = new SecurityConfig(oAuth, handler);

        CorsConfigurationSource source = config.corsConfigurationSource();
        assertNotNull(source);
    }

    @Test
    void testAuthenticationManager() throws Exception {
        CustomOAuth2UserService oAuth = mock(CustomOAuth2UserService.class);
        OAuth2SuccessHandler handler = mock(OAuth2SuccessHandler.class);
        SecurityConfig config = new SecurityConfig(oAuth, handler);

        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager manager = config.authenticationManager(authConfig);
        assertNotNull(manager);
    }
}
